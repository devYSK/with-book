# 스프링으로 시작하는 리액티브 프로그래밍

https://github.com/bjpublic/Spring-Reactive

# 목차

- [Chapter 01 리액티브 시스템과 리액티브 프로그래밍](#chapter-01-리액티브-시스템과-리액티브-프로그래밍)
- [Chapter 02 리액티브 스트림즈(Reactive Streams)](#chapter-02-리액티브-스트림즈reactive-streams)
- [Chapter 03 Blocking I/O와 Non-Blocking I/O](#chapter-03-blocking-io와-non-blocking-io)
- [Chapter 04 리액티브 프로그래밍을 위한 사전 지식](#chapter-04-리액티브-프로그래밍을-위한-사전-지식)
- [Chapter 05 Reactor 개요](#chapter-05-reactor-개요)
- [Chapter 06 마블 다이어그램(Marble Diagram)](#chapter-06-마블-다이어그램marble-diagram)
- [Chapter 07 Cold Sequence와 Hot Sequence](#chapter-07-cold-sequence와-hot-sequence)
- [Chapter 08 Backpressure](#chapter-08-backpressure)
- [Chapter 09 Sinks](#chapter-09-sinks)
- [Chapter 10 Scheduler](#chapter-10-scheduler)
- [Chapter 11 Context](#chapter-11-context)
- [Chapter 13 Testing](#chapter-13-testing)
- [Chapter 14 Operators](#chapter-14-operators)
- [Chapter 15 Spring WebFlux 개요](#chapter-15-spring-webflux-개요)
- [Chapter 16 애너테이션 기반 컨트롤러](#chapter-16-애너테이션-기반-컨트롤러)
- [Chapter 17 함수형 엔드포인트(Functional Endpoint)](#chapter-17-함수형-엔드포인트functional-endpoint)
- [Chapter 18 Spring Data R2DBC](#chapter-18-spring-data-r2dbc)
- [Chapter 19 예외 처리](#chapter-19-예외-처리)
- [Chapter 20 WebClient](#chapter-20-webclient)
- [Chapter 21 Reactive Streaming 데이터 처리](#chapter-21-reactive-streaming-데이터-처리)

# Chapter 01 리액티브 시스템과 리액티브 프로그래밍
## 리액티브 시스템이란?

리액티브의 사전적 의미 : 반응. 반응형

리액티브란, 특정 조건이나 이벤트에 반응하여 동작하는 방식을 의미하며, 클라이언트의 요청에 즉각적으로 응답하는것을 의미한다.

리액티브라는 용어의 의미를 올바르게 정의하기 위해 선언한 리액티브 선언문이 있다. 

<img src="./images//image-20240601000023433.png" width = 650>

크게 수평으로 두개의 점선과 세개의 영역으로 나누어져 있다.

* MEANs : 리액티브 시스템의 주요통신 수단. 비동기 기반의 메시지를 통하여 구성요소들간의 느슨한 결합, 격리성,위치 투명성을 보장
* FORM : 메시지 기반을 통해서 어떤 형태를 지닌 시스템으로 형성되는지를 나타냄. 탄력성과 회복성을 가져야 함을 의미한다.
* VALUE : 비동기 메시지 기반을 통해서 회복성과 예측가능한 규모 확장 알고리즘을 통해 시스템의 처리량을 자동으로 확장하고 축소하는 탄력성을 확보한다는것을 의미

추가적인 4가지 특징은 다음과 같.

* 응답성 (Responsive)**: 시스템은 사용자 요청에 빠르게 응답할 수 있어야 한다.

- **탄력성 (Resilient)**: 시스템은 실패에 대비하고, 자동으로 복구할 수 있어야 한다.
- **탄력적 확장성 (Elasticity)**: 부하 변화에 따라 유연하게 확장 및 축소가 가능해야 한다.
- **메시지 구동 (Message Driven)**: 비동기 메시지 패싱을 통해 구성 요소 간의 느슨한 결합을 유지해야 한다.



## 리액티브 프로그래밍

리액티브 시스템을 구축하는데 필요한 프로그래밍 ㅁ델.

리액티브 시스템에서는 NonBlocking I/O 기반의 비동기 메시지 통신을 한다.

* Blocking I/O : I/O 작업이 일어나면 응답이 오기전까지 해당 작업을 수행하던 스레드가 block 되는 작업
* NonBlockingI/O : 스레드가 block 되지 않고 다른 일을 이어서 함

## 리액티브 프로그래밍의 특징

### 1. declarative progamming

C, JAVA와 다른 명령형 프로그래밍이 아닌 선언형 프로그래밍이다.

실행할 동작을 구체적으로 명시하지 않고, 이러한 동작을 하겠다는 목표만 선언한다.

즉 어떻게가 아니라 무엇을 할 것인지 설명하는 방식.

문제의 최종 상태나 원하는 결과를 기술하고, 그 결과를 얻기 위한 방법을 추상화하는것이다. 

* "SELECT * FROM users WHERE age > 30;"는 데이터베이스에 원하는 결과를 기술하는 방식. 어떻게 데이터를 가져올지는 DBMS가 처리.

* ``"<h1>Hello World</h1>"``는 웹 페이지에서 "Hello World"를 보여주라는 명령이 아니라 결과를 기술

### 2. data streams와 propagation of change

데이터가 지속적으로 발생하며, 지속적으로 데이터가 발생할때마다 이것을 변화하는 이벤트로 보고 데이터를 전달하는것을 의미. 

## 리액티브 프로그래밍 구성

리액티브 코드는 크게 Publisher, Subscriber, Data Source, Operator로 구성된다

* Publisher: 발행자. 입력으로 들어오는 데이터들을 제공한다.
* Subscriber: 구독자. Publisher가 제공하는 데이터를 구독하고, 소비하는 주체이다. 
* Data Source : 원천 데이터. Publisher에 입력으로 들어오는 데이터를 의미한다. 
* Operator : Publisher로 부터 전달된 데이터가 Subscriber에게 도착하기 전까지 처리를 담당한다. 즉 Opreator(운영, 연산)을 통하여 데이터를 가공한다 

# Chapter 02 리액티브 스트림즈(Reactive Streams)

## 리액티브 스트림즈

리액티브 라이브러리가 있어야 의존성을 추가해서 코드를 작성할 수 있다.

이 라이브러리들은 여러개이고, 이 라이브러리들을 위한 표준이 있는데 이것이 리액티브 스트림즈이다(인터페이스, 표준, 명세)

* 대표적인 구현체로 RxJava, Reactor, 등등

## 리액티브 스트림즈 구성 요소

아래 컴포넌트들은 외우고 있는것이 좋다. 

| 컴포넌트         | 설명                                                         |
| ---------------- | ------------------------------------------------------------ |
| **Publisher**    | 데이터를 생성하고 통지(발행, 게시, 방출)하는 역할을 한다.    |
| **Subscriber**   | 구독한 Publisher로부터 통지(발행, 게시, 방출)된 데이터를 전달받아서 처리하는 역할을 한다. |
| **Subscription** | Publisher에 요청할 데이터의 개수를 지정하고, 데이터의 구독을 취소하는 역할을 한다. |
| **Processor**    | Publisher와 Subscriber의 기능을 모두 가지고 있다. 즉, Subscriber로서 다른 Publisher를 구독할 수 있고, Publisher로서 다른 Subscriber가 구독할 수 있다. |

![image-20240601001350385](./images//image-20240601001350385.png)

이들 컴포넌트들은 다음처럼 동작한다

1. Subscriber가 전달받을 데이터를 구독한다 (subscribe)
2. Publisher는 데이터를 발행할 준비가 되었음을 Subscribe에게 알린다 (onSubscribe)
3. 통지를 받은 Subscriber는 원하는 데이터 수를 publisher에게 요청한다 (Subscrition.request). 백프레셔가 뭘까?
4. Publishr는 요청받은 데이터 만큼 발행한다 (onNext)
5. 이렇게 반복하다가. 모든 데이터를 발행하게 되면 완료 라는 의미의 onComplete으로 알리고, 에러가 발생하면 Subscriber에게onError로 에러를 알린다.

왜 원하는 데이터 수를 요청할까?

이는, 퍼블리셔가 섭스크라이버의 처리량을 알지못하고 무한정 데이터를 보내게 되면 섭스크라이버가 부하가 걸릴수도 있기 때문이다. 



### Publisher Interface

```java
public interface Publisher<T> {
  void subscribe(Subscriber<? super T> s);
}
```

퍼블리셔가 섭스크라이버를 의존해서 구조가 약간 의아할 수도 있는데, 

리액티브에서는 개념상 subscriber가 구독하는것이 맞지만, 코드상에서는 Publisher가 Subcriber를 전달받아 등록하는 형태로 구독이 이루어진다. 

### Subscriber

```java
public interface Subscriber<T> {

    /**
     * {@link Publisher#subscribe(Subscriber)} 호출 후, 데이터 요청을 위한 {@link Subscription}을 받습니다.
     */
    public void onSubscribe(Subscription s);

    /**
     * {@link Publisher}가 {@link Subscription#request(long)} 요청에 응답하여 데이터를 전송합니다.
     */
    public void onNext(T t);

    /**
     * 오류 발생 시 호출되며, 이후 이벤트는 전송되지 않습니다.
     * 
     * @param t 전달된 오류
     */
    public void onError(Throwable t);

    /**
     * 성공적으로 완료되었을 때 호출되며, 이후 이벤트는 전송되지 않습니다.
     */
    public void onComplete();
}
```

* onSubscribe : 구독 시점에 publisher에게 요청할 데이터의 개수를 지정하거나, 구독을 해지함. Subscription 객체 이용
* onNext : 데이터를 처리
* onError : 에러 발생시에러 처리
* onComplete: Publisher가 데이터를 전송하다가 끝났음을 Subscriber에게 전달하는 역할

### Subscription

```java
public interface Subscription {

    /**
     * 이 메서드를 통해 요청 신호를 보낼 때까지 {@link Publisher}는 이벤트를 전송하지 않습니다.
     * 
     * @param n 요청할 요소의 양 (양의 정수)
     */
    public void request(long n);

    /**
     * {@link Publisher}에게 데이터 전송을 중지하고 자원을 정리하도록 요청합니다.
     */
    public void cancel();
}
```

* request를 통한 데이터 갯수 전달과, cancel 메서드를 통한 구독 해지를 지원한다.

Java의 익명 인터페이스의 특성을 잘 이용해서 Publisher와 Subscriber 간에 데이터를 주고 받을 수 있다

### Processor

```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}

```

publisher와 Subscriber를 상속하기 때문에 그 둘의 기능을 모두 갖고 체이닝을 하면서 처리할 수 있게 된다. 

## 리액티브 스트림즈 용어

### Signal

신호. 구독자와 발행자간의 상호작용을 신호라고 한다.

* onSubscribe, onNext... 등등 Signal이라고 표현

publisher가 subscriber의 메소드를 이용한다면 Publisher가 subscriber에게 시그널을 보낸다고 할 수 있는것이다.

* onSubscribe, onNext등을 사용하면

### Demand

Demands는 Subscriber가 Publisher에게 요청하는 데이터를 의미한다. 

이는 `Subscriber`가 소비할 수 있는 데이터의 양을 제어하기 위해 사용된다. 

### Emit

Publisher가 Subscriber에게 데이터를 발행할때를 의미한다

즉 데이터를 내보내는 행위를 Emit 이라고 한다. (ex sse emitter)

### Upstream / downstream

데이터가 흐르는 방향이다.

서버대 서버 간의 데이터 흐름과 비슷하다

업스트림은 클라 -> 서버로 데이터를 보내는 방향이다. 반대로 다운스트림은 서버 -> 클라로 데이터가 흐르는 방향이다

리액티브에서의 의미도 비슷하다.

업스트림은 퍼블리셔(발행자) -> 섭스크라이버(구독자)로 데이터가 흐르는 것이다. 

Publisher는 업스트림이라고하며 중간 연산자 역할을 하는 Processor가 데이터를 받아들일떄도 업스트림이라고 한다.

반대로 Subscriber는 다운스트림이며, Processor도 데이터를 전달할 때 다운스트림 역할을 한다.

* 즉 중간에 있는 Publisher나 Operator는 Downstream consumer도 될 수 있다. 

### Sequence( 중요!)

시퀀스는 순차 라는 의미이다. 데이터의 흐름과 처리 순서를 의미한다.

* Flux를 통해서 데이터 생성, emit 하고 map을 통해 변환하는 과정 자체가 sequence

즉 Operator의 데이터의 연속적인 흐름을 정의한것이라고도 말할 수 있다. 

## 리액티브 스트림즈의 구현 규칙

### Publisher 구현에 대한 규칙

1. **요청된 데이터 개수 준수**:
   - `Publisher`가 `Subscriber`에게 보내는 `onNext` signal의 총 개수는 항상 해당 `Subscriber`의 구독을 통해 요청된 데이터의 총 개수보다 더 작거나 같아야 한다.
   - 즉 Subscriber가 요청한 갯수를 초과하여 Publisher는 발행할 수 없다. 
2. **요청보다 적은 데이터 및 구독 종료**:
   - `Publisher`는 요청된 것보다 적은 수의 `onNext` signal을 보내고 `onComplete` 또는 `onError`를 호출하여 구독을 종료할 수 있다.
   - Publisher가 무한히 발생하는 무한 스트림인경우 처리 중 에러가 발생하기 전까지는 종료가 없다. 때문에 이경우 규치은 예외이다. 
3. **데이터 처리 실패 시**:
   - `Publisher`의 데이터 처리가 실패하면 `onError` signal을 보내야 한다.
4. **데이터 처리 성공 시**:
   - `Publisher`의 데이터 처리가 성공적으로 종료되면 `onComplete` signal을 보내야 한다.
5. **종료 신호 후 구독 취소**:
   - `Publisher`가 `Subscriber`에게 `onError` 또는 `onComplete` signal을 보내는 경우 해당 `Subscriber`의 구독은 취소된 것으로 간주되어야 한다.
6. **종료 상태 신호 후**:
   - 일단 종료 상태 signal을 받으면(`onError`, `onComplete`) 더 이상 signal이 발생되지 않아야 한다.
7. **구독 취소 시**:
   - 구독이 취소되면 `Subscriber`는 결국 signal을 받는 것을 중지해야 한다.

이것들은 Publisher가 구현될떄 고려해야 하는 규칙들이다.

### Subscriber

1. **데이터 요청**:
   - `Subscriber`는 `Publisher`로부터 `onNext` signal을 수신하기 위해 `Subscription.request(n)`를 통해 Demand signal을 `Publisher`에게 보내야 한다.
   - 즉 데이터를 얼마나 수신할 수 있는지 결정하는 책임은 Subscriber에게 있다는 의미. 
   
2. **종료 신호 후 메서드 호출 금지**:
   - `Subscriber.onComplete()` 및 `Subscriber.onError(Throwable t)`는 `Subscription` 또는 `Publisher`의 메서드를 호출해서는 안 된다.
   - 상호 재귀 호출이나, 레이스 컨디션 등이 발생할 수 있다.
   
3. **종료 신호 후 구독 취소**:
   - `Subscriber.onComplete()` 및 `Subscriber.onError(Throwable t)`는 signal을 수신한 후 구독이 취소된 것으로 간주해야 한다.

4. **구독 취소**:
   - 구독이 더 이상 필요하지 않은 경우 `Subscriber`는 `Subscription.cancel()`을 호출해야 한다.
   - 이렇게 해야 리소스를 적절하게 해제할 수 있다. 
   
5. **최대 한 번의 구독**:
   - `Subscriber.onSubscribe()`는 지정된 `Subscriber`에 대해 최대 한 번만 호출되어야 한다.
   - 동일한 구독자는 최대 한번만 구독할 수 있다. 

이는 Subscriber가 구현될떄 고려되야 하는 규칙이다

### Subscription

1. **동기적 요청 허용**:
   - 구독은 `Subscriber`가 `onNext` 또는 `onSubscribe` 내에서 동기적으로 `Subscription.request`를 호출하도록 허용해야 한다.
   - Subscription.request와 Subscriber.onNext 사이의 상호 재귀로 인해 스택 오버플로가 발생할 수 있으므로 주의해야 한다. 
   
2. **구독 취소 후 요청 무효**:
   - 구독이 취소된 후 추가적으로 호출되는 `Subscription.request(long n)`는 효력이 없어야 한다.

4. **구독 취소 후 취소 요청 무효**:
   - 구독이 취소된 후 추가적으로 호출되는 `Subscription.cancel()`은 효력이 없어야 한다.

5. **잘못된 요청 처리**:
   - 구독이 취소되지 않은 동안 `Subscription.request(long n)`의 매개변수가 0보다 작거나 같으면 `java.lang.IllegalArgumentException`과 함께 `onError` signal을 보내야 한다.

6. **신호 중지 요청**:
   - 구독이 취소되지 않은 동안 `Subscription.cancel()`은 `Publisher`가 `Subscriber`에게 보내는 signal을 결국 중지하도록 요청해야 한다.

7. **참조 삭제 요청**:
   - 구독이 취소되지 않은 동안 `Subscription.cancel()`은 `Publisher`에게 해당 구독자에 대한 참조를 결국 삭제하도록 요청해야 한다.
   - 이래야 signal 전송 중지뿐만아니라 GC가 구독자의 객체를 수집하여 메모리를 확보할 수 있다. 
   
8. **예외 허용 불가**:
   - `Subscription.cancel()`, `Subscription.request()` 호출에 대한 응답으로 예외를 던지는 것을 허용하지 않는다.

9. **무제한 요청 지원**:
   - 구독은 무제한 수의 `request` 호출을 지원해야 하고 최대 \(2^{63}-1\) 개의 Demand를 지원해야 한다.



https://github.com/reactive-streams/reactive-streams-jvm 에 더 많은 내용이 있다. 

## 요약

- ﻿﻿리액티브 스트림즈는 데이터 스트림을 Non-Blocking이면서 비동기적인 방식으로 처 리하기 위한 리액티브 라이브러리의 표준 사양이다.
- ﻿﻿리액티브 스트림즈는 Publisher, Subscriber, Subscription, Processor라는 네 개의 컴 포넌트로 구성되어 있다. 리액티브 스트림즈의 구현체는 이 네 개의 컴포넌트를 사양 과 규칙에 맞게 구현해야 한다.
- ﻿﻿Publisher와 Subscriber의 동작 과정과 리액티브 스트림즈 컴포넌트 의 구현 규칙은 리액티브 프로그래밍을 큰 틀에서 이해하고 올바르게 사용하기 위해 기억해야 되는 중요한 내용이다.
- ﻿﻿리액티브 스트림즈의 구현체 중에서 어떤 구현체를 학습하든지 핵심 동작 원리는 같다.



# Chapter 03 Blocking I/O와 Non-Blocking I/O

## BlockingI/O

I/O는 프로그램이 외부의 입출력 장치들과 데이터를 주고받는것을 의미한다.

* DB I/O,네트워크 I/O 

프로그램 코드가 실행되면 스레드가 해당 코드를 실행하는데, 외부 I/O 요청을 보냈을떄 쓰레드가 일을 하지 못하고 멈춰 있다가,

응답이 돌아오면 그제서야 일을 하는것을 Blocking I/O라고 한다.

이 단점을 보완하기 위해 멀티 스레드를 매우 많이 만들어서 사용한다.

* 해당 애플리케이션 스레드가 블락당한거지, 다른 스레드들은 일할 수 있다. 운영체제의 스레드가 다른 애플리케이션 스레드를 실행시킬 수 있기 때문이다.

그러나 이런 방식은 컨텍스트 스위칭으로 인해 스레드 전환 비용이 발생한다.

* 기존에 실행되고 있던 스레드의 정보들을 PCB에 저장하고, 실행시켜야 할 프로세스 정보를 PCB에서 읽어오는 것을 컨텍스트 스위칭이라고 한다. 매우 비싸다
  * 왜?
  * 컨텍스트 스위칭 하는 동안에 CPU는 일을 하지 못하고 스위칭 해야하기 때문에 그만큼 놀게된다. 
  * 컨텍스트스위칭이 발생하면 CPU 캐시에있는 데이터가 새로운 프로세스의 데이터로 바뀌므로 히트도 낮아지고 메모리 접근도 해야하니 느려진다.
  * 페이지 테이블도 전환해야 한다. 새로운 프로세스의 페이지를 가리켜야 하기 때문이다. 
  * PCB, TCB등 접근하고 업데이트해야하므로 비싸다

또한 자바에서의 멀티스레드는 메모리를 많이 사용한다

* 일반적으로 한 스레드당 윈도우가 1MB, 리눅스/유닉스가 2MB 정도 사용됌. 그래서 최소 스프링 뜨면 몇백메가를 잡아먹는다
* 버츄얼 스레드로도 완벽히 해결은 못함. 버츄얼 스레드를 실행시키는 캐리어 스레드가 기존 플랫폼 스레드와 스펙이 비슷함

이러한 이유로 블로킹 I/O와 멀티스레드는 완벽한 대안은 아니다

## NonBlocking I/O

NonBlocking I/O의 경우,  요청한 스레드는 차단되지 않는다.

I/O 작업의 응답을 기다리지 않고 스레드가 다른 일을 하기 때문이다. 

때문에 하나의 스레드로 많은 요청 등을 처리할 수 있다.

그러나 다음과 같은 작업에는 NonBlocking I/O도 좋지 않다

* CPU Bound 작업. 결국 스레드가 CPU를 태우는것에 집중해야 하기 떄문에 다른 요청을 처리하지 못한다
* 전체 과정에 Blocking I/O가 단 1개라도 있는 경우 NonBlocking이 의미가 없다.
  * 왜? 아니 스레드가 Block됐는데 다른 NonBlocking일을 어떻게 합니까..
  * 그래서 JPA, MyBatis와 WebFlux는 맞지 않다.  (JDBC는 기본적으로 Blocking임)

## Non-Blocking V/0 방식의 통신이 적합한 시스템

WebFlux가 무조건 좋다고 빠르다고 도입할 수는 없다.

왜그럴까? - 학습 난이도가 너무 높다. 리액티브라는 새로운 표준을 공부해야 한다. 

제대로 알지도 못하고 사용하면 결국 문제가 될 수 있다.

그럼 어떤것이 적합할까?

### 대량의 요청 트래픽이 발생하는 시스템

스케일아웃으로 해결하기 어려울 정도의트래픽을 받는경우 Webflux로 전환을 고려할만하다. 

서버 컴퓨팅 비용이 기하급수적으로 늘 수 있기 때문이다. webflux는 저비용으로 고수준을 이끌어낼 수 있다. 

### 마이크로서비스

마이크로서비스는 내부적으로 통신을 어마어마하게 많이하기 때문에 (I/O 발생) 블로킹 통신보다는 논블로킹 통신이 적합하다.

한 서비스가 블로킹으로 인해 지연된다면 다른서비스에도 영향을 미칠 수 있기 때문에다

### 스트리밍 또는 실시간 시스템

Webflux를 이용하면 무한 데이터 스트림을 만들기에 적합하기 떄문에 스트리밍 또는 실시간 시스템을 쉽게 구축할 수 있다. 



# Chapter 04 리액티브 프로그래밍을 위한 사전 지식

## 함수형 인터페이스

함수형 인터페이스는 단 하나의 추상메서드만 있는 인터페이스이다.

자바는 함수(메서드)를 값으로 취급하지 못한다.

그런데, 함수형 인터페이스를 통하면 값으로 취급해서 변수로, 파라미터로 다를 수 있게 된다.

## 람다 표현식

메서드의 인자로 인터페이스를 받게되면 익명 클래스를 받을 수 있지만, 그렇게되면 코드가 너무 길어지고 지저분하게 된다

이를 자바8부터 람다 표현식으로 간결하게 지원한다.

```java
public class LambdaComparisonExample {
    public static void main(String[] args) {
        String[] names = {"John", "Jane", "Max", "Alex"};

        // 익명 클래스를 사용한 정렬
        Arrays.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });

        // 람다 표현식을 사용한 정렬
        Arrays.sort(names, (s1, s2) -> s1.compareTo(s2));

    }
}
```

* 아래 람다표현식을 보면 얼마나 깔끔한가..

한가지 혼동하지 말아야 할것이 있다.

함수형 인터페이스의 추상 메서드를 람다로 작성해서 전달한다는 의미는, 

메서드 자체를 전달하는것이 아닌 함수형 인터페이스를 구현한 인스턴스를 람다 표현식으로 작성해서 전달하는것이다. 

## 함수 디스크립터.

자바에서 함수 디스크립터(Function Descriptor)는 람다 표현식이나 메서드 참조가 어떤 함수형 인터페이스를 구현하는지에 대한 설명을 의미한다. 이는 자바 8에서 도입된 개념으로, 함수형 인터페이스의 추상 메서드의 시그니처를 기반으로한다.

자주 사용하는 함수 디스크립터는 다음과 같다 

| 함수형 인터페이스   | 함수 디스크립터   | 설명                                                         |
| ------------------- | ----------------- | ------------------------------------------------------------ |
| Predicate<T>        | T -> boolean      | T -> Boolean: T 타입의 람다 파라미터와 boolean 타입의 값을 리턴. |
| Consumer<T>         | T -> void         | T -> void: T를 받아서 소비하고 리턴값이 없다.                |
| Function<T, R>      | T -> R            | T -> R: T 타입의 입력을 받아 R 타입의 결과를 리턴.           |
| Supplier<T>         | () -> T           | () -> T: 입력 없이 T 타입의 값을 리턴.                       |
| BiPredicate<L, R>   | (L, R) -> boolean | (L, R) -> boolean: L, R 타입의 두 개의 입력을 받아 boolean 값을 리턴. |
| BiConsumer<T, U>    | (T, U) -> void    | (T, U) -> void: T와 U 타입의 두 개의 입력을 받아 소비하고 리턴값이 없다. |
| BiFunction<T, U, R> | (T, U) -> R       | (T, U) -> R: T와 U 타입의 두 개의 입력을 받아 R 타입의 결과를 리턴. |

리액티브 프로그래밍에서는, 비동기로 처리하기 때문에 함수형 인터페이스가 적합하며,

간결하고, 함수를 합성하기 때문에 이런 지식들은 필요하다. 

# Chapter 05 Reactor 개요

## Reactor

Reactor란 라이브러리로, 스프링 팀의 주도하에 개발된 구현체로서 스프링5부터 리액티브 스택에 포함되어 Webflux 기반의 코어를 담당한다.

리액터의 특징은 다음과 같다

1. 리액티브 스트림즈를 구현한 리액티브 라이브러리다
2. JVM위에서 실행되는 Nonblocking의 핵심 기술이다. 
3. 자바의 함수형 프로그래밍 API를 통해 이뤄진다
4. FLUX (0~N개의 데이터 제공), Mono (0~1개의 데이터 제공)을 제공한다
5. 백프레셔를 지원한다. 즉 부하가 걸리지 않게 데이터 흐름을 제어할 수 있도록 한다. 

## 코드로 보는 리액터의 구성요소

```java
@Slf4j
public class Example5_1 {
    public static void main(String[] args) {
        Flux<String> sequence = Flux.just("Hello", "Reactor");
        sequence.map(data -> data.toLowerCase())
                .subscribe(data -> System.out.println(data));
    }
}

```

* Flux는 Publisher의 역할을 한다. 즉 입력으로 들어오는 데이터를 제공한다. 
  * Publisher가 최초로 제공하는 가공되지 않는 "hello", "Reactor"는 데이터 소스라고 부른다.
* subscribe 메서드 파라미터로 전달된 람다 표현식이 Subscriber 역할을 한다. (소비)
* map 메서드는 Operator 메서드인데, 전달받은 데이터를 가공하는 역할을 한다. 
  * Operator는 반환값으로 Mono 또는 Flux를 반환하기 때문에 Operator 메서드 체이닝을 형성한다.

# Chapter 06 마블 다이어그램(Marble Diagram)

## Marble Diagram

마블 다이어그램(Marble Diagram)은 리액티브 프로그래밍에서 데이터 스트림의 동작을 시각적으로 표현한 다이어그램.

리액티브 연산자들이 데이터 스트림을 어떻게 변환하고 처리하는지 이해하는 데 도움을 주는 그림이다.

### 주요 요소

마블 다이어그램은 다음과 같은 요소로 구성됩니다:

1. **데이터 스트림**: 일련의 시간 축을 따라 이동하는 원(마블)들로 표시됩니다. 각 원은 스트림의 데이터 요소를 나타낸다.
2. **시간 축**: 왼쪽에서 오른쪽으로 진행되는 수평선으로 표시되며. 데이터 요소가 시간의 흐름에 따라 스트림을 통해 이동하는 것을 나타낸다.
3. **연산자**: 스트림을 변환하는 연산자는 다양한 기호와 형태로 표시됩니다. 예를 들어, `map`, `filter`, `flatMap` 등의 연산자가 있다.
4. **완료 신호**: 스트림의 끝을 나타내는 기호로, 일반적으로 수직선 또는 `|` 기호로 표시된다.
5. **오류 신호**: 오류를 나타내는 기호로, 일반적으로 `X` 기호로 표시된다.

### 예시

#### 1. 기본 스트림

```
--1--2--3--|
```

- 데이터 스트림: `1`, `2`, `3`
- 완료 신호: `|`

#### 2. `map` 연산자

`map` 연산자는 각 데이터 요소를 변환한다.

```
--1--2--3--|
  map(x => x * 2)
--2--4--6--|
```

- 원래 데이터 스트림: `1`, `2`, `3`
- 변환된 데이터 스트림: `2`, `4`, `6`

#### 3. `filter` 연산자

`filter` 연산자는 조건을 만족하는 데이터 요소만 통과시킨다. 

```
--1--2--3--4--|
  filter(x => x % 2 == 0)
-----2-----4--|
```

- 원래 데이터 스트림: `1`, `2`, `3`, `4`
- 필터링된 데이터 스트림: `2`, `4`

#### 4. `flatMap` 연산자

`flatMap` 연산자는 각 데이터 요소를 스트림으로 변환하고, 이 스트림들을 하나의 스트림으로 평탄화한다. 

```
--A--B--|
  flatMap(x => [x1, x2])
--A1-A2-B1-B2--|
```

- 원래 데이터 스트림: `A`, `B`
- 변환된 데이터 스트림: `A1`, `A2`, `B1`, `B2`

![image-20240601151709783](./images//image-20240601151709783.png)

다이어그램에는 두개의 타임라인이 존재한다. (시간 흐름 순)

1. 1번(작은 동그라미)의 Publisher가 데이터를 emit(발행, 푸시)하는 타임라인 
2. 2번(작은동그라미)는 Publisher가 emit하는 데이터
3. 3번((작은동그라미)는 데이터의 emit이 끝남을 의미하며, onComplete 상태이다. 
4. 4와 같이 점선 화살표는 Opertator 함수로 입력이 전달되는것을 의미한다
5. 5는 전달받은 emit 데이터를 연산하는 Opertator 함수이다.
6. 6은 Operator를 통해 가공 처리한 후 출력으로 보낸것을 의미한다. 즉 다운스트림으로 흐르는것 
7. 7의 타임라인은 Operator를 통해 가공된후 내보내진 데이터의 타임라인이다. 1번과 다르다
8. 9와 같은 X 표시는 에러가 발생하여 onError 시그널이 발행된것을 의미한다.

 

주석으로 마블 다이어그램이 포함되어 있는 경우가 많으니 마블 다이어그램은 읽을줄 알아야 한다.



## 마블 다이어그램으로 Publisher 이해하기

### Mono

![image-20240601152353497](./images//image-20240601152353497.png)

Mono는 0개 또는 1개의 데이터를 emit하는 Publisher이기때문에 하나의 데이터만 표현한다.

```java
public class Example6_1 {
    public static void main(String[] args) {
        Mono.just("Hello Reactor")
                .subscribe(System.out::println);
    }
}
```

* Mono.just로 데이터소스를 생성하고 구독하고 나서, sout으로 출력 연산을 한다.

```java
public class Example6_2 {
    public static void main(String[] args) {
        Mono
            .empty()
            .subscribe(
                    none -> System.out.println("# emitted onNext signal"),// consumer
                    error -> {},
                    () -> System.out.println("# emitted onComplete signal") // completeConsumer
            );
    }
}
```

* Mono.empty는 데이터를 1건도 emit하지 않는다. 바로 onComplete Signal을 전송한다.
* none->은 onNext 핸들러이다.  
* () -> 은 complete handler이다. 때문에 맨 마지막 () -> 가 실행된다. 

### Flux

![image-20240601154256318](./images//image-20240601154256318.png)

Flux는 0개 또는 N개의 데이터를 emit하는 publiser이다

```java
public class Example6_4 {
    public static void main(String[] args) {
        Flux.just(6, 9, 13)
                .map(num -> num % 2)
                .subscribe(System.out::println);
    }
}
```

* Flux.just로 데이터 소스를 생성하고, map 오퍼레이터 연산을 한 후 구독하여 onComplete 시그널을 받으면 sout으로 출력한다.

또한 Flux는 Mono의 범위를 포함하기 때문에, 두개의 Mono를 연결해서 Flux를 생성할 수 있다.

```java
public class Example6_6 {
    public static void main(String[] args) {
        Flux<String> flux =
                Mono.justOrEmpty("Steve")
                        .concatWith(Mono.justOrEmpty("Jobs"));
        flux.subscribe(System.out::println);
    }
}
```

just() Operator의 경우 null을 허용하지 않지만, justOrEmpty()는 null을 허용한다.

* 내부적으로 null을 전달받으면 empty() Operator가 호출된다.

concatWith()는 Publisher(Mono.justOrEmpty("Steve"))와 concatWith의 파라미터로 전달되는 Publiser(Mono.justOrEmpty("Jobs"))가 각각 emit하느 데이터들을 하나로 연결해서 새로운 Publisher의 데이터 소스로 만들어주는 오퍼레이터다.

<img src="./images//image-20240601154652370.png" width = 650>

concatWith의 마블 다이어그램이다.

위쪽에있는 Publisher 데이터소스와 concatWith 내부에 있는 Publisher 데이터 소스를 연결해 새 데이터 소스를 만든다(가장 아래)

또한 Flux는 또다른 Flux들로 데이터 소스 스트림을 만들 수 있다.

```java
public class Example6_7 {
    public static void main(String[] args) {
        Flux.concat(
                        Flux.just("Mercury", "Venus", "Earth"),
                        Flux.just("Mars", "Jupiter", "Saturn"),
                        Flux.just("Uranus", "Neptune", "Pluto"))
                .collectList()
                .subscribe(planets -> System.out.println(planets));
    }
}
```

* `Flux.concat` 연산자는 여러 개의 `Flux`를 순서대로 연결하여 하나의 `Flux`로 만들어주는데, 정의된 순서로 발행되므로 순서가 보장된다. 
* concat에서 리턴하는 publisher는 Flux다. 여러개를 연결해서 하나의 Flux를 생성한다.
* collectList()는 Mono이다 하나의 List<...>로 수집한 후 List를 발행하는 Mono를 출력한다. 
* 그러므로 마지막 subscribe로 출력하는 데이터는 `List<String>`이 된다 

# Chapter 07 Cold Sequence와 Hot Sequence

Hot Swap : 컴퓨터가 커진 상태에서 디스크 등 교체하더라도 시스템 재시작 없이 바로 장치 인식

Hot Deploy : 서버를 재시작하지않고 변경사항 적용

> Cold는 무언가를 새로 시작하고, Hot은 무언가를 새로 시작하지 않는다.

## Cold Sequence

Cold Sequence는 스트림에 구독자가 구독할 때마다 새롭게 시작되는 데이터 시퀀스를 의미한다.

구독시점이 달라도, 데이터를 emit하는 과정을 처음부터 재시작하여 구독자들은 모두 동일한 데이터를 전달받는것을 Cold Sequence라고 한다.  

* 모든 구독자는 독립적인 데이터 시퀀스를 받는다.. 즉, 구독자가 스트림을 구독할 때마다 데이터가 처음부터 시작된다.

![image-20240601170742397](./images//image-20240601170742397.png)

* 그림을 보면, Subscriber A,B가 구독시점이 다르지만 같은 4개의 data를 emit하는것을 볼 수 있다. 즉 동일한 데이터를 전달받는다.

``````java
@Slf4j
public class Example7_1 {
    public static void main(String[] args) throws InterruptedException {
        Flux<String> coldFlux = Flux.fromIterable(Arrays.asList("KOREA", "JAPAN", "CHINESE"))
                    .map(String::toLowerCase);

        coldFlux.subscribe(country -> log.info("# Subscriber1: {}", country));
        System.out.println("----------------------------------------------------------------------");
        Thread.sleep(2000L);
        coldFlux.subscribe(country -> log.info("# Subscriber2: {}", country));
    }
}
``````

* 두 구독자가 발행자를 구독하는 시점이 다르다. 그래도 같은 데이터결과가 출력된다. 

Cold sequence는 데이터 재사용 및 반복 가능성을 제공한다

이는 데이터의 무결성을 보장하며, 테스트와 디버깅에서 매우 유용하며, 동일한 스트림을 다른 구독자가 각각 독립적으로 처리할 수 있다. 

즉 구독자가 각각 다른 시점에 같은 데이터를 다른 방식으로 처리하는경우에 유용하다 

이를 통해 데이터의 재사용성, 무결성, 리소스 효율성, 구독 관리의 유연성을 확보할 수 있다. 

* 예시로, 같은 파일에서 json과 xml을 각각 만들 수 있는것이 있겠다. 

## Hot Sequence

Hot Sequence는 스트림이 시작되면 구독자와 상관없이 데이터를 계속 발행하는 시퀀스를 의미한다.

Cold와 반대로, 데이터 스트림이 새로 시작되지 않고 중간에 구독에 끼여들면 구독 시점 이전에 데이터는 전달받지 못하고 시점부터의 데이터만 전달받게 된다.

![image-20240601171302085](./images//image-20240601171302085.png)

* 세번의 구독이 발생했지만, 타임라인은 1개밖에 생성되지 않았다.  

```java
@Slf4j
public class Example7_2 {
    public static void main(String[] args) throws InterruptedException {
        String[] singers = {"Singer A", "Singer B", "Singer C", "Singer D", "Singer E"};

        log.info("# Begin concert:");
        Flux<String> concertFlux =
                Flux
                    .fromArray(singers)
                    .delayElements(Duration.ofSeconds(1))
                    .share();

        concertFlux.subscribe(
                singer -> log.info("# Subscriber1 is watching {}'s song", singer)
        );

        Thread.sleep(2500);

        concertFlux.subscribe(
                singer -> log.info("# Subscriber2 is watching {}'s song", singer)
        );

        Thread.sleep(3000);
    }
}

```

* 초당 1개의 엘리먼트를 반환하는 Flux이고, 3초쯤부터 두번째 구독자가 구독하므로 C부터 받을 수 있게 된다

Hot Sequence는 실시간 데이터 처리에 유용하다. 또한 하나의 데이터 소스를 여러 구독자가 동시에 사용할 때 리소스를 절약할 수 있다.

share() Operator는 Cold Sequence를 Hot Sequence로 동작하게 해주는 Operator인데 

원본 Flux(전혀 아무것도 가공되지 않은 처음의 소스)를 여러 Subscriber가 공유해서 사용할 수 있게 된다. 

* 예시로 센서 데이터를 실시간으로 처리하는 스트림을 생성하고, 여러 구독자가 실시간으로 데이터를 받을 수 있겠다. 

## HTTP 요청 응답에서 Cold Sequence와 Hot Sequence의 동작 흐름

```java
@Slf4j
public class Example7_3 {
    public static void main(String[] args) throws InterruptedException {
        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                .host("worldtimeapi.org")
                .port(80)
                .path("/api/timezone/Asia/Seoul")
                .build()
                .encode()
                .toUri();

        Mono<String> mono = getWorldTime(worldTimeUri); // 주목 
        mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
        Thread.sleep(2000);
        mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));

        Thread.sleep(2000);
    }

}
```

* getWorldTime내부에서는 그냥 webclient로 요청후 Mono를 반환한다.
* 아무 Operator가 없어 Cold Sequence로 동작해서, 두번째 구독이 일어나면 새로 다시 요청을 보내게 된다. 

```java
@Slf4j
public class Example7_4 {
    public static void main(String[] args) throws InterruptedException {
        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                .host("worldtimeapi.org")
                .port(80)
                .path("/api/timezone/Asia/Seoul")
                .build()
                .encode()
                .toUri();

        Mono<String> mono = getWorldTime(worldTimeUri).cache(); // here
        mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
        Thread.sleep(2000);
        mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));

        Thread.sleep(2000);
    }
}
// 결과 - 이 둘이 완전히 같다. 
[reactor-http-nio-2] INFO - # dateTime 1: 2024-06-01T17:22:07.792904+09:00
[main] INFO -               # dateTime 2: 2024-06-01T17:22:07.792904+09:00
```

* cache() Operator를 Mono에서 추가했다. 이는 Cold Sequence가 HotSequence로 동작하게 한다.

* 결과적으로 emit된 데이터를 캐시한 뒤, 구독이 발생하면 캐시된 데이터를 전달한다. 

cache() Operator를 활용할 수 있는 예로 인증 토큰이 필요한 경우를 들 수 있다.

매번 인증서버로부터 토큰을 받아오기에 부담일 수 있으므로 캐시된것을 재사용 할 수 있다.

* cache() 오퍼레이터의 파라미터에 ttl 등을 설정할 수 있다. 



# Chapter 08 Backpressure

backpressure : Publisher로부터 전달받은 데이터를 안정적으로 처리하기 위한 수단

## Backpressure란?

배압, 또는 억압이라고 한다. 

리액티브 프로그래밍에서는 데이터가 스트림형태로 끊임없이 흐르는데 

Backpressure는 publisher가 emit하는 데이터스트림의 처리량을 조절하여 과부하가 걸리지 않도록 하는것이다.

생산속도가 소비속도보다 빠르면 과부하가 걸릴 수 있기 때문에, 소비자가 처리할 수 있는 데이터 양을 제어하여 안정성을 유지하고 메모리 부족이나 과부하를 방지하도록 한다. 

## Reactor에서 Backpressure 처리 방식

## 1. 데이터 개수 제어

Subscriber가 request() 메소드를 통해서 적절한 데이터 개수를 요청하는 방식이 있따. 

```java
@Slf4j
public class Example8_1 {
    public static void main(String[] args) {
        Flux.range(1, 5)
            .doOnRequest(data -> log.info("# doOnRequest: {}", data))
            .subscribe(new BaseSubscriber<>() {
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					request(1);
				}

				@SneakyThrows
				@Override
				protected void hookOnNext(Integer value) {
					Thread.sleep(2000L);
					log.info("# hookOnNext: {}", value);
					request(1);
				}
			});
    }
}


```

* 예제. publisher 데이터 emit 속도보다 subscriber 처리속도가 느린것을 재현하기 위해 sleep 추가.

1. **`hookOnSubscribe`**: 구독 시점에 호출되며, 첫 번째 데이터를 요청 (`request(1)`).

2. **`hookOnNext`**: 데이터를 받을 때마다 호출되며, 각 데이터를 처리한 후 다음 데이터를 하나씩 요청 (`request(1)`).

데이터 개수를 다음처럼 제어한다

- **초기 요청**: `request(1)`로 시작하여 하나의 데이터를 요청.
- **후속 요청**: 각 데이터 처리 후 (`hookOnNext`에서), 다음 데이터를 하나씩 추가로 요청.

이 방식으로 한 번에 하나의 데이터만 요청하고 처리하여, 소비자가 처리할 수 있는 속도로 데이터를 받아오게 된다 .

데이터 요청개수를 제안하고 싶다면 BaseSubscriber의 request(n)를 사용해서 데이터 요청 갯수를 제한할 수 있다.

## 2. BackPressure 전략 사용

### Reactor에서 제공하는 Backpressure 전략 종류

| 종류        | 설명                                                         |
| ----------- | ------------------------------------------------------------ |
| IGNORE 전략 | Backpressure를 적용하지 않는다.                              |
| ERROR 전략  | Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우, Exception을 발생시키는 전략. |
| DROP 전략   | Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기하는 먼저 emit된 데이터부터 Drop시키는 전략. |
| LATEST 전략 | Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기하는 가장 최근에(나중에) emit된 데이터부터 버퍼에 채우는 전략. |
| BUFFER 전략 | Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우, 버퍼 안에 있는 데이터부터 Drop시키는 전략. |

* IGNORE 사용시 Downstream(Subscriber 또는 중간 Operator)이 데이터를 처리할 수 없을 때 무시되므로, 버퍼가 가득 차거나 초과하여 `IllegalStateException`이 발생할 수 있다.

```java
Flux<Integer> flux = Flux.range(1, 10)
  .onBackpressureIgnore() // 이렇게 설정 
  .doOnNext(data -> System.out.println("Emitting: " + data));

```



ERROR 전략은 Downstream의 처리 속도가 느려서 Upstream을 따라가지 못할 경우 `IllegalStateException`이 발생할 수 있다. 이경우 Error Signal을 Subscriber에게 전송하고 삭제한 데이터는 **폐기한다**

```java

/**
 * Unbounded request 일 경우, Downstream 에 Backpressure Error 전략을 적용하는 예제
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, Exception을 발생 시키는 전략
 */
@Slf4j
public class Example8_2 {
    public static void main(String[] args) throws InterruptedException {
        Flux
            .interval(Duration.ofMillis(1L))
            .onBackpressureError() // 전략 
            .doOnNext(data -> log.info("# doOnNext: {}", data)) // 처리하다가 터져버림 
            .publishOn(Schedulers.parallel())
            .subscribe(data -> {
                        try {
                            Thread.sleep(5L);
                        } catch (InterruptedException e) {}
                        log.info("# onNext: {}", data);
                    },
                    error -> log.error("# onError", error));

        Thread.sleep(2000L);
    }
}
```

* interval Operator로 0.001초마다 대량 데이터 emit
* Subscriber는 처리하는데 0.005초 걸림. 즉 결국에 과부하 발생 

Drop 전략은, Publisher가 전달한 데이터가 버퍼에 가득 찬 경우, 버퍼 밖에서 대기중인 먼저 Emit된 데이터부터 삭제하는 전략이며 페기된다.

![image-20240602223745867](./images//image-20240602223745867.png)

* 그림을 보면 스텝3에서 대기중인 11, 12,13이 버퍼가 가득 차서 버려지고 그사이에 스텝5에서 버퍼가 비워져서 버퍼로 옮겨간다. 

```java
Flux.range(1, 100)
    .onBackpressureDrop()
    .subscribe(System.out::println);
```



LATEST 전략은 데이터가 버퍼에 가득 찬 경우, 새로운 데이터가 들어 오는 시점에 가장 최근의 데이터만 남겨 두고 나머지 데이터를 폐기한다. 즉 가장 뒤 데이터만 남겨두고, 앞에 버퍼 앞에서 대기하던 애들은 폐기한다. 

![image-20240602224306624](./images//image-20240602224306624.png)

```java
Flux.range(1, 100)
    .onBackpressureLatest()
    .subscribe(System.out::println);
```



Buffer 전략은 버퍼가 가득 차면 버퍼 안의 데이터를 폐기한다. 즉 이전까지 전략과는 다르게, 버퍼 밖이 아닌 버퍼 내부를 폐기하는 것을 의미한다. 전략은 2가지로 나뉜다

1. DROP_LATEST전략

DROP_LATEST 전략은 가장 나중에 버퍼에 채워진(가장 바깥쪽 즉 최신) 데이터부터 DROP하면 폐기한 후, 이 공간에 emit된 데이터를 채우는 전략이다.

```java
Flux
    .interval(Duration.ofMillis(300L))
    .doOnNext(data -> log.info("# emitted by original Flux: {}", data))
    .onBackpressureBuffer(2,
            dropped -> log.info("** Overflow & Dropped: {} **", dropped),
            BufferOverflowStrategy.DROP_LATEST) // here
```

2. DROP_ORDEST

DROP_ORDEST전략은, 가장 오래된 데이터를 폐기한 후 이 공간에 emit된 새 데이터를 채우는 전략이다. 

```java
Flux
    .interval(Duration.ofMillis(300L))
    .doOnNext(data -> log.info("# emitted by original Flux: {}", data))
    .onBackpressureBuffer(2,
            dropped -> log.info("** Overflow & Dropped: {} **", dropped),
            BufferOverflowStrategy.DROP_OLDEST)
```



### 각 전략은 언제 적절할까? - 적절한 때와 부적절한 때 정리

- **IGNORE**: Downstream이 빠르게 처리할 수 있을 때 적절, 느릴 때 부적절.
- **ERROR**: 오류를 통해 Backpressure를 처리하고자 할 때 적절, 오류를 피하고 싶을 때 부적절.
- **DROP**: 최신 데이터만 중요한 경우 적절, 모든 데이터가 중요한 경우 부적절.
- **LATEST**: 최신 데이터만 중요하고 이전 데이터가 덜 중요한 경우 적절, 모든 데이터가 중요한 경우 부적절.
- **BUFFER**: 데이터 손실을 최소화하고자 할 때 적절, 버퍼를 넘어서면 데이터 손실이 발생할 때 부적절.



# Chapter 09 Sinks

# Chapter 10 Scheduler
# Chapter 11 Context
# Chapter 13 Testing
# Chapter 14 Operators
# Chapter 15 Spring WebFlux 개요
# Chapter 16 애너테이션 기반 컨트롤러
# Chapter 17 함수형 엔드포인트(Functional Endpoint)
# Chapter 18 Spring Data R2DBC
# Chapter 19 예외 처리
# Chapter 20 WebClient
# Chapter 21 Reactive Streaming 데이터 처리