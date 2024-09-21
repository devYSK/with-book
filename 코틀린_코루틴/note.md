# 코틀린 코루틴 : Deep Dive

* 깃허브 : https://github.com/MarcinMoskala/coroutines_sources



# 1부 코틀린 코루틴 이해하기
## 1장 코틀린 코루틴을 배워야 하는 이유
스레드를 사용하는 비용은 매우 크다. 명시적으로 생성하고, 유지되어야 하며 스레드를 위한 메모리 할당도 되어야 한다.

또한 수백만의 요청의 응답할때마다 블로킹을 한다면 메모리와 프로세서 사용에 엄청난 비용이 든다.

10만개의 스레드를 생성하는것과 10만개의 코루틴을 생성하는 비용은 어마어마하게 차이가 난다. 

## 2장 시퀀스 빌더

코틀린의 sequence는 List, set과 비슷하지만 필요할 때마다 값을 하나씩 계산하는 lazy 처리를 한다.

특징은 다음과 같다.

* 요구되는 연산을 최소한으로 수행
* 무한정이 될 수 있음
* 메모리 사용이 효율적

```kotlin
val seq = sequence {
    yield(1)
    yield(2)
    yield(3)
}

fun main() {
    for (num in seq) {
        print(num)
    } // 123
}
```

시퀀스는 sequence 함수를 이용해 정의하며 yield함수를 호출하여 다음 값을 생성한다.



## 3장 중단은 어떻게 작동할까?

코루틴은 중단되었을 때 Continuation 객체를 반환하고, 이 객체를 이용하면 멈췄던 곳에서 다시 시작할 수 있다.

코루틴은 스레드와 다르다. 스레드는 저장이 불가능하고 멈추는것만 가능하다. 

다음 코드의 중단 지점은 어디일까

```kotlin
suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        println("Before too")
        continuation.resume(Unit)
    }

    println("After")
}
```

* suspendCancellableCoroutine의 인자로 들어간 람다 함수는 중단되기 전에 실행된다. 

continuation.resume을 호출하면 중단된 코루틴을 실행한다. 

```kotlin
suspend fun main() {
    val i: Int = suspendCancellableCoroutine<Int> { c ->
        c.resume(42)
    }
    println(i) // 42

    val str: String = suspendCancellableCoroutine<String> { c ->
        c.resume("Some text")
    }
    println(str) // Some text

    val b: Boolean = suspendCancellableCoroutine<Boolean> { c ->
        c.resume(true)
    }
    println(b) // true
}
```

resume을 통해 반환되는 값은 반드시 지정된 타입과 같은 타입 이어야 한다. 

즉 중단함과 동시에 데이터를 받고 나면, 받은 데이터를 resume 함수로 통해 보내는것이다. 

### 예외로 재개하기 

suspendCoroutine에서 resumeWithException으로 예외를 받을  수 있다.

```kotlin
class MyException : Throwable("Just an exception")

suspend fun main() {
    try {
        suspendCancellableCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    } catch (e: MyException) {
        println("Caught!")
    }
}
```

예를들어 api 네트워크 관련 예외로 던질 수 있다. 핸들러로 가라잇~

```kotlin
suspend fun main2() {
    try {
        suspendCancellableCoroutine<Unit> { cont ->
            if (true) {
                // api 에러 반환 
                cont.resumeWithException(MyException())
            } else {
                cont.resume(Unit)
            }
        }
    } catch (e: MyException) {
        println("Caught!")
    }
}
```

## 4장 코루틴의 실제 구현

- ﻿﻿중단 함수는 함수가 시작할 때와 중단 함수가 호출되었을 때 상태를 가진다 는 점에서 상태 머신(state machine)과 비슷합니다. (상태 패턴)
- ﻿﻿컨티뉴에이션(continuation) 객체는 상태를 나타내는 숫자와 로컬 데이터를 가지고 있습니다.
- ﻿﻿함수의 컨티뉴에이션 객체가 이 함수를 부르는 다른 함수의 컨티뉴에이션 객체를 장식(decorate)합니다. 그 결과, 모든 컨티뉴에이션 객체는 실행을 재개하거나 재개된 함수를 완료할 때 사용되는 콜 스택으로 사용됩니다.

코틀린 팀은 컨티뉴에이션 전달 방식을 택했다. CPS

서스펜드 함수를 자세하게 보면

```kotlin
suspend fun getUser(): User?
suspend fun setUser(user: User)
suspend fun checkAvailability(flight: Flight): Boolean

// under the hood is
fun getUser(continuation: Continuation<*>): Any?
fun setUser(user: User, continuation: Continuation<*>): Any
fun checkAvailability(
  flight: Flight,
  continuation: Continuation<*>
): Any
```

이렇게 컨티뉴에이션을 전달한다.

반환 타입이 Any? 또는 Any인 이유는 중단 함수 실행 도중 중단되면 값을 반환하지 않을 수 있기 때문이다. 이때 중단함수는 marker인 COROUTINE_SUSPENDED를 반환한다. 

아마도 언젠가 유니언 타입인 User? | COROUTINE_SUSPENDED 같은 타입을 도입하면 이렇게 바뀔것이다.

* 도입한다고 한다 ㅎ 

아래 예시를 보자

```kotlin
suspend fun myFunction() {
  println("Before")
  delay(1000) // suspending
  println("After")
}
// 벗기면 아래이다 
fun myFunction(continuation: Continuation<*>): Any
```

이 함수는 state(상태)를 저장하기 위해 자신만의 Continuation 객체가 필요하다. 

이제 이걸 row하게 까보면 아래처럼 된다.

```kotlin
// A simplified picture of how myFunction looks under the hood
fun myFunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    if (continuation.label == 0) {
        println("Before")
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }
    if (continuation.label == 1) {
        println("After")
        return Unit
    }
    error("Impossible")
}
```

* 내부적으로 라벨을 맥여 처리를 하고 있다.

delay에 의해 중단된경우 COROUTINE_SUSPENDED를 반환한다. 

중단이 일어나면 콜 스택에 있는 모든 함수가 종료되며 중단된 코루틴을 실행하던 스레드를 실행 가능한 코드가 사용할 수 있게 된다. 



함수가 중간된 후에 다시 사용할 지역 변수나 파라미터와 같은 상태를 가지고 있다면 함수의 continuation 객체에 상태를 저장해야 한다.

````kotlin

suspend fun myFunction() {
  println("Before")
  var counter = 0
  delay(1000) // suspending
  counter++
  println("Counter: $counter")
  println("After")
}
```


```
//2
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

fun myFunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    var counter = continuation.counter

    if (continuation.label == 0) {
        println("Before")
        counter = 0
        continuation.counter = counter
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }
    if (continuation.label == 1) {
        counter = (counter as Int) + 1
        println("Counter: $counter")
        println("After")
        return Unit
    }
    error("Impossible")
}
````

r간략화된 코드를 보면, 지역변수나 파라미터 같이 함수 내에서 사용되던 값들은 함수 내부 필드에 있다가 중단되기 직전에 저장되고 재개될 때 복구된다. 

### 콜스택 (call stack)

함수 a가 함수 b를 호출하면 a의 상태와 b가 끝나면 실행될 지점을 어딘가에 저장해야 한다. 이 정보들을 콜스택 이라는 자료구조에 저장한다.

코루틴을 중단하면 스레드를 반환해 콜 스택에 있는 정보가 사라진다. 

대신 컨티뉴 에이션 객체가 콜 스택의 역할을 대신한다.

중단이 되었을 때 상태(abel)과 함수의 지역 변수와 파라미터(필드) 그리고 중단 함수를 호출한 함수가 재개될 위치 정보를 가지고 있어 다행이다.

하나의 컨티뉴에이션 객체가 다른 하나를 참조하고, 참조된 객체가 또 다른 컨티뉴 에이션 객체를 참조한다.  

### 실제 코드

컨티뉴에이션 객체와 중단 함수를 컴파일한 실제 코드는 최적화 되어 있으며 몇가지 처리 과정이 더 포함되어있어 복잡하다

* 예외가 발생했을 때 더 나은 스택 트레이스 생성
* 코루틴 중단 인터셉션
* 사용하지 않는 변수 제거 및 테일콜 최적화



### suspend 함수의 성능과 비용

함수를 상태로 나누는 것은 숫자를 비교하는 것만큼 쉬운 일이며 실행점이 변하는 비용 또한 거의 들지 않는다.

지역 변수를 복사하지 않고 새로운 변수가 메모리 내 특정 값을 가리키게 한다.

즉 컨티뉴에이션 객체 생성시에만 비용이 좀 들지만 큰 문제는 아니다.

### 요약 및 결론

- ﻿﻿중단 함수는 상태 머신과 비슷해 함수가 시작될 때와 중단 함수를 호출한 뒤 의 상태를 가집니다.
- ﻿﻿상태를 나타내는 값과 로컬 데이터는 컨티뉴에이션 객체에 저장됩니다.
- ﻿﻿호출된 함수의 컨티뉴에이션 객체는 호출한 함수의 컨티뉴에이션을 장식합 니다. 그 결과, 모든 컨티뉴에이션 객체는 함수가 재개될 때 또는 재개된 함 수가 완료될 때 사용되는 콜 스택의 역할을 합니다.



## 5장 코루틴: 언어 차원에서의 지원 vs 라이브러리

라이브러리 없이 언어 차원에서도 지원한다.

suspendCoroutine, Continuation을 지원하지만 매우 로우하며, 애플리케이션 개발자보다 라이브러리 개발자들에게 더 적합하다.

때문에 kotlinx.coroutines 라이브러리가 있으며 훨씬 코루틴을 쉽게 사용할 수 있다

| 항목              | 언어 차원에서의 지원                                         | kotlinx.coroutines 라이브러리                                |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **의존성**        | 코틀린 기본 라이브러리에 포함되어 있으며 별도의 의존성 추가가 필요 없다. | 컴파일러가 지원하며, 코틀린 기본 라이브러리에 의존성을 추가해야 한다. |
| **포함 패키지**   | `kotlin.coroutines` 패키지에 포함되어 있다.                  | `kotlinx.coroutines` 패키지에 포함되어 있다.                 |
| **제공 기능**     | `Continuation`, `suspendCoroutine` 등의 기본적인 기능들과 `suspend` 키워드만 제공한다. | `launch`, `async`, `Deferred` 등 다양한 기능을 제공한다.     |
| **사용 편의성**   | 직접 사용하기 매우 어렵다.                                   | 직접 사용하기 편리하게 설계되어 있다.                        |
| **동시성 스타일** | 거의 모든 동시성 스타일이 허용된다.                          | 단 하나의 명확한 동시성 스타일을 위해 설계되어 있다.         |

# 2부 코틀린 코루틴 라이브러리

## 6장 코루틴 빌더
모든 서스펜드 함수는 서스펜드 함수에 의해서만 호출되어야 한다.

서스펜드 함수를 연속으로 호출하면 시작되는 지점이 있는데 그곳이 바로 **코루틴 빌더이다.**

kotlinx.coroutines 라이브러리가 제공하는 3가지 필수적인 코루틴 빌더를 보자

* launch
* runBlocking
* async

### launch 빌더

launch가 작동하는 방식은 새 스레드를 시작하는것과 비슷하다.

각자 별개로 실행된다.

블로킹된 스레드를 유지하는건 비용이 들지만 중단된 코루틴을 유지하는건 거의 공짜다 ( 코루틴 구현을 봐라.)

### runBlocking 빌더

시작한 스레드를 중단시키는 코루틴 빌더이다.

정확하게 말하면, 새로운 코루틴을 실행한 뒤 완료될 때까지 현재 물고있는 스레드를 중단 가능한 상태로 블로킹 한다. 

주로 다음과 같은 사유로 사용된다

* 프로그램 종료 방지 위해 스레드 블로킹
* 유닛 테스트 블로킹
* 일반 함수에서 서스펜드 함수 호출시 



### async 빌더

launch와 비슷하지만 값을 생성하여 반환한다. 

Deffered<T> 객체를 반환하며 작업이 끝나면 값을 반환하는 메서드인데 await가 있다. 

async 빌더는 호출되자마자 코루틴을 즉시 시작한다.

따라서 여러 작업을 이용해 호출하는데 도움된다 



### 현업에서의 코루틴 사용

큰 애플리케이션에서는 코루틴 스코프를 직접 만들어 사용한다.

첫번째 빌더가 스코프에 시작되면 다른 빌더가 첫 빌더의 스코프에서 시작될 수 있다. 

### coroutineScope 사용하기

스코프가 필요하지만, 함수의 인자로 스코프를 넘기고 싶지 않다면 중단함수 밖에서 coroutineScope 함수를 사용한다

```kotlin
suspend fun getArticlesForUser(
    userToken: String?,
): List<ArticleJson> = coroutineScope {
    val articles = async { articleRepository.getArticles() }
    val user = userService.getUser(userToken)
  
    articles.await()
        .filter { canSeeOnList(user, it) }
        .map { toArticleJson(it) }
}
```

## 7장 코루틴 컨텍스트

코루틴 빌더의 시그니처를 보면 첫번째 파라미터가 CoroutineContext다

```kotlin
public fun CoroutineScope.launch( // 확장함수 
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit // 리시버 
): Job 

public fun <T> CoroutineScope.async( // 확장함수 
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T // 리시버 
): Deferred<T> 
```

마지막 인자의 리시버도 CoroutineScope 타입이다. 



### CoroutineContext 인터페이스

CoroutineContext는 원소나 원소들의 집합을 나타내는 인터페이스

Job CoroutineName, CoroutineDispatcher 같은 element 또한 CoroutineContext다

두개의 CoroutineContext 인터페이스를 더하면 합쳐 하나의 CoroutineContext가 된다 

### 컨텍스트 폴딩 

fold란 함수형 프로그래밍에서 많이 사용하는 패턴으로, 리스트나 컬렉션 모든 요소를 하나의 값으로 축약하는 연산을 의미한다.

이 fold를 이용해서 여러 CoroutineContext를 합쳐 하나의 CoroutineContext를 만든다. 

### Suspend 함수에서 컨텍스트에 접근하기

CoroutineScope는 Context에 접근할 때 사용하는 coroutineContext 프로퍼티를 가지고 있으며, 컨텍스트는 suspend 함수 사이에 전달되는 continuation 객체가 참조하고 있어 접근이 가능하다. 

## 8장 잡과 자식 코루틴 기다리기

구조화된 동시성에서 부모-자식 관계는 다음 특성을 갖는다.

- ﻿﻿자식은 부모로부터 컨텍스트를 상속받습니다.
- ﻿﻿부모는 모든 자식이 작업을 마칠 때까지 기다립니다.
- ﻿﻿부모 코루틴이 취소되면 자식 코루틴도 취소됩니다.
- ﻿﻿자식 코루틴에서 에러가 발생하면, 부모 코루틴 또한 에러로 소멸합니다.

이 특성은 Job (Context)와 관련이 있다.

Job은 생명주기를 가지며 취소 가능하다. 



## 9장 코루틴 취소

코틀린 코루틴이 제시한 중단함수의 취소방식은 아주 간단하고 편리하며 안전하다.

Job 인터페이스는 cancel 메서드를 가지고 있어 Deferred도 cancel 메서드가 있다.

다음과 같은 효과를 가져올 수 있다.

- ﻿﻿호출한 코루틴은 첫 번째 중단점(아래 예제에서는 deLay)에서 잡을 끝냅 니다.
- ﻿﻿잡이 자식을 가지고 있다면, 그들 또한 취소됩니다. 하지만 부모는 영향을 받지 않습니다.
- ﻿﻿잡이 취소되면, 취소된 잡은 새로운 코루틴의 부모로 사용될 수 없습니다.
   취소된 잡은 Canceling 상태가 되었다가 Cancelled 상태로 바뀝니다.

```kotlin
suspend fun main(): Unit = coroutineScope {
    val job = launch {
        repeat(1_000) { i ->
            delay(200)
            println("Printing $i")
        }
    }

    delay(1100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
}

```

cancel 메서드 인자로 예외를 넣으면 원인을 명확하게 할 수 있다.

```kotlin
 public fun cancel(cause: CancellationException? = null)

    /**
     * @suppress This method has bad semantics when cause is not a [CancellationException]. Use [cancel].
     */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Since 1.2.0, binary compatibility with versions <= 1.1.x")
    public fun cancel(cause: Throwable? = null): Boolean
```

cancel이 호출된 뒤 취소과정이 완료되는걸 기다리기 위해 join을 호출해야 한다. join을 호출하지 않으면 경쟁 상태에 빠질 수 있다. 

job.join을 추가하면 코루틴이 취소를 마칠때까지 중단되므로 경쟁 상태가 발생하지 않는다. 

간단한 방법으로 cancelAndJoin()을 호출하면 된다 그냥.

```kotlin
job.cancelAndJoin()
```

코루틴이 취소되었을 때도 반드시 실행해야 하는 작업이 있다면, 그 코드를 **`withContext(NonCancellable)`**로 감싸서 **취소되지 않고 실행**될 수 있도록 해야 한다

**코루틴이 취소되면**: 보통 코루틴이 취소되면 모든 작업이 중단됩니다. 즉, 어떤 코드를 실행하려고 해도 취소 상태에서는 제대로 실행되지 않을 수 있습니다.

**중단 함수가 필요할 때**: 하지만 취소된 후에도 **꼭 실행해야 하는 작업**이 있을 수 있습니다. 예를 들어, 데이터베이스 작업이 실패하면 그동안 변경된 데이터를 롤백해야 하죠. 이 작업을 취소된 코루틴 상태에서 하려고 하면 실행이 안 될 수 있습니다.

**`withContext(NonCancellable)`의 역할**: 이때 `withContext(NonCancellable)`를 사용합니다. 이 코드는 **특정 코드 블록이 취소되지 않도록 보호**해줍니다. 즉, 코루틴이 취소된 상태여도 이 블록 안의 코드는 **취소되지 않고** 실행됩니다. 덕분에 중단 함수도 블록 내부에서 제대로 호출할 수 있게 됩니다.

```kotlin
suspend fun performDatabaseOperation() {
    try {
        // 데이터베이스에 변경사항 적용
    } catch (e: Exception) {
        // 코루틴이 취소되었어도 롤백은 반드시 수행해야 함
        withContext(NonCancellable) {
            rollbackDatabaseChanges()
        }
    }
}
```

### invokeOnCompletion

자원을 해제하는데 자주 사용되는 또 다른 방법은 invokeOnCompletion메서드다.

invokeOnCompletion메서드는 Jobdㅣ Completed나 Cancelled와 같은 마지막 상태에 도달했을 때 호출될 핸들러를 지정하는 역할을 한다

```kotlin
suspend fun main(): Unit = coroutineScope {
  val job = launch {
    delay(1000)
  }
  
  job.invokeOnCompletion { exeception: Throwable? ->
    println("finished")
  }
  delay(400)
  job.cancelAndJoin()
}
```

* throwable은 job이 예외없이 끝나면 null이다
* 코루틴이 취소되었으면 CancellationException이다
* 코루틴을 종료시킨 또다른 예외일 수 잇다.

### 취소할수 없는 Job 취소하기 yield, job.isActive, ensureActive()

중단 가능하지않으면서, CPU 집약적이거나 시간 집약적인 연산들이 suspend 함수에 있따면 각 연산들 사이에 yield를 사용하는것이 좋다. 그래야 중단 가능하기 때문이다. yield가 없으면 절대 코루틴이 스레드를 양보하지 않기 때문에 취소 등을 알 수 없다

```kotlin
suspend fun cpuIntensiveOperations() = 
	withContext(Dispatcher.Default) {
    cpuRun()
    yield()
    cpuRun()
    yield()
    cpuRun()
  }
```

또는

```kotlin
suspend fun cpuIntensiveOperations() = coroutinScope {
  val job = Job()
  launch(job) {
    do {
      Thread.sleep(200)
    } while(isActive)
  }
}
```

또는

```kotlin
suspend fun cpuIntensiveOperations() = coroutinScope {
  val job = Job()
  launch(job) {
    while(true) {
      ensureActive()
      println("")
    }
  }
}
```

일반적으로 ensureActive()가 가벼워 제일 많이 쓰이고, yield함수는 최상위 중단 함수이고, 스코프가 필요 없어서 스레드가 바뀌는 문제가 생길수도 있다.



yield는 cpu 사용량이 크거나 스레드를 블로킹하는 중단 함수에서 자주 사용된다

## 10장 예외 처리

잡히지 않은 예외가 발생하면 코루틴은 종료된다. 

코루틴은 부모도 종료시키며 취소된 부모는 자식들 모두를 취소시킨다.



코루틴이 종료되기 전 예외를 잡는거 도움이 되지만 늦으면 손 쓸 수 없다.

코루틴 빌더인 async, launch를 try-catch로 래핑하는건 전혀 도움이 안되고 빌더 내부에서 잡아야 한다

```kotlin
fun main() = runBlocking {
  try {
    launch {
      delay(100)
      throw Error("error")
    }
  } catch (e: Throwable) { // 전혀 도움이 안됌. 
    println("예외를 잡았을까? ")
  }
}
// 위는 x

fun main() = runBlocking {

   launch {
		 try {
     	delay(100)
     	throw Error("error")
     } catch (e: Throwable) {
       println("good")
     }
   }
 
}

```

때문에 내부에서 잡아야 한다 

### SupervisorJob

supervisorJob을 사용하면 자식에서 발생한 모든 예외를 무시할 수 있다. 

![image-20240920213830262](./images//image-20240920213830262.png)

* 코루틴 6번이 예외가 발생해서, 일반적으로 예외 처리가 안되어 있으면 코루틴3, 코루틴1까지 예외가 전파되어서 모두 취소되지만 SupervisorJob은 무시하기 때문에 형제 코루틴인 5, 7도 전파되지 않아 취소되지 않는다

때문에 일반적으로 SupervisorJob은 다수의 코루틴을 시작하는 스코프로 사용된다

다음처럼 SuperVisorjob을 부모 코루틴의 인자로 사용하면 안된다.

```kotlin
fun main(): Unit = runBlocking {
    // 이렇게 작성하면 안된다 
  // 자식 코루틴 하나가 있고 부모 코루틴이 없는 잡은 일반 잡과 동일하게 작동한다. 
    launch(SupervisorJob()) { // 1
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will not be printed")
        }
    }

    delay(3000)
}
```

왜냐하면 `runBlocking`이 부모 코루틴이기 때문에, 이 코드 구조에서는 `SupervisorJob`이 제대로 작동하지 않게 된다. 그리고 이 경우 runBlocking의 잡을 사용하지 않기 때문에 예외는 runBlockingdㅡ로 전파되지 않는다.

```kotlin
fun main(): Unit = runBlocking {
    val job = SupervisorJob()

    launch(job) {
        delay(1000)
        throw Error("Some error")
    }

    launch(job) {
        delay(2000)
        println("Will not be printed")
    }

    delay(3000)
}
```

위 코드는 수퍼바이저잡이 되어서 

```kotlin
fun main(): Unit = runBlocking {  // 부모 코루틴, runBlocking 자체도 잡을 가지고 있음
    val job = SupervisorJob()     // SupervisorJob 생성 (자식 잡)

    launch(job) {  // 첫 번째 자식 코루틴, 부모 잡은 SupervisorJob
        delay(1000)
        throw Error("Some error")  // 1초 후 예외 발생, 하지만 다른 코루틴에 영향 없음
    }

    launch(job) {  // 두 번째 자식 코루틴, 부모 잡은 SupervisorJob
        delay(2000)
        println("Will not be printed")  // 2초 후 출력 시도 (출력되지 않음)
    }

    delay(3000)  // runBlocking이 자식 코루틴들이 끝날 때까지 기다림
}
```

이런식으로 설명할 수 있다. 

또다른 방법으로 supervisorScipe가 있다

### SupervisorScope

예외 전파를 막는 또다른 방법이다. 다른 코루틴에서 발생한 예외를 무시하고 부모와의 연결을 유지한다. 





## 11장 코루틴 스코프 함수
## 12장 디스패처
## 13장 코루틴 스코프 만들기
## 14장 공유 상태로 인한 문제
## 15장 코틀린 코루틴 테스트하기

# 3부 채널과 플로우
## 16장 채널
## 17장 셀렉트
## 18장 핫 데이터와 콜드 데이터 소스
## 19장 플로우란 무엇인가?
## 20장 플로우의 실제 구현
## 21장 플로우 만들기
## 22장 플로우 생명주기 함수
## 23장 플로우 처리
## 24장 공유플로우와 상태플로우
## 25장 플로우 테스트하기

# 4부 코틀린 코루틴 적용하기
## 26장 일반적인 사용 예제
## 27장 코루틴 활용 비법
## 28장 다른 언어에서의 코루틴 사용법
## 29장 코루틴을 시작하는 것과 중단 함수 중 어떤 것이 나을까?
## 30장 모범 사례