# 이펙티브 코틀린

[toc]

* https://github.com/VitekKlugi/Effective-Kotlin-Examples/blob/master/src/goodcode/readability/item11/CognitiveLoad.kt

목차


- [1부: 좋은 코드](#1부-좋은-코드)
- [1장 안정성](#1장-안정성)
  - [아이템 1: 가변성을 제한하라](#아이템-1-가변성을-제한하라)
  - [아이템 2: 변수의 스코프를 최소화하라](#아이템-2-변수의-스코프를-최소화하라)
  - [아이템 3: 최대한 플랫폼 타입을 사용하지 말라](#아이템-3-최대한-플랫폼-타입을-사용하지-말라)
  - [아이템 4: inferred 타입으로 리턴하지 말라](#아이템-4-inferred-타입으로-리턴하지-말라)
  - [아이템 5: 예외를 활용해 코드에 제한을 걸어라](#아이템-5-예외를-활용해-코드에-제한을-걸어라)
  - [아이템 6: 사용자 정의 오류보다는 표준 오류를 사용하라](#아이템-6-사용자-정의-오류보다는-표준-오류를-사용하라)
  - [아이템 7: 결과 부족이 발생할 경우 null과 Failure를 사용하라](#아이템-7-결과-부족이-발생할-경우-null과-Failure를-사용하라)
  - [아이템 8: 적절하게 null을 처리하라](#아이템-8-적절하게-null을-처리하라)
  - [아이템 9: use를 사용하여 리소스를 닫아라](#아이템-9-use를-사용하여-리소스를-닫아라)
  - [아이템 10: 단위 테스트를 만들어라](#아이템-10-단위-테스트를-만들어라)
- [2장 가독성](#2장-가독성)
  - [아이템 11: 가독성을 목표로 설계하라](#아이템-11-가독성을-목표로-설계하라)
  - [아이템 12: 연산자 오버로드를 할 때는 의미에 맞게 사용하라](#아이템-12-연산자-오버로드를-할-때는-의미에-맞게-사용하라)
  - [아이템 13: Unit?을 리턴하지 말라](#아이템-13-Unit?을-리턴하지-말라)
  - [아이템 14: 변수 타입이 명확하게 보이지 않는 경우 확실하게 지정하라](#아이템-14-변수-타입이-명확하게-보이지-않는-경우-확실하게-지정하라)
  - [아이템 15: 리시버를 명시적으로 참조하라](#아이템-15-리시버를-명시적으로-참조하라)
  - [아이템 16: 프로퍼티는 동작이 아니라 상태를 나타내야 한다](#아이템-16-프로퍼티는-동작이-아니라-상태를-나타내야-한다)
  - [아이템 17: 이름 있는 아규먼트를 사용하라](#아이템-17-이름-있는-아규먼트를-사용하라)
  - [아이템 18: 코딩 컨벤션 지켜라](#아이템-18-코딩-컨벤션-지켜라)
- [2부: 코드 설계](#2부-코드-설계)
- [3장 재사용성](#3장-재사용성)
  - [아이템 19: knowledge를 반복하지 말라](#아이템-19-knowledge를-반복하지-말라)
  - [아이템 20: 일반적인 알고리즘을 반복해서 구현하지 말라](#아이템-20-일반적인-알고리즘을-반복해서-구현하지-말라)
  - [아이템 21: 일반적인 프로퍼티 패턴은 프로퍼티 위임으로 만들어라](#아이템-21-일반적인-프로퍼티-패턴은-프로퍼티-위임으로-만들어라)
  - [아이템 22: 일반적인 알고리즘을 구현할 때 제네릭을 사용하라](#아이템-22-일반적인-알고리즘을-구현할-때-제네릭을-사용하라)
  - [아이템 23: 타입 파라미터의 섀도잉을 피하라](#아이템-23-타입-파라미터의-섀도잉을-피하라)
  - [아이템 24: 제네렉 타입과 variance 한정자를 활용하라](#아이템-24-제네렉-타입과-variance-한정자를-활용하라)
  - [아이템 25: 공통 모듈을 추출해서 여러 플랫폼에서 재사용하라](#아이템-25-공통-모듈을-추출해서-여러-플랫폼에서-재사용하라)
- [4장 추상화 설계](#4장-추상화-설계)
  - [아이템 26: 함수 내부의 추상화 레벨을 통일하라](#아이템-26-함수-내부의-추상화-레벨을-통일하라)
  - [아이템 27: 변화로부터 코드를 보호하려면 추상화를 사용하라](#아이템-27-변화로부터-코드를-보호하려면-추상화를-사용하라)
  - [아이템 28: API 안정성을 확인하라](#아이템-28-API-안정성을-확인하라)
  - [아이템 29: 외부 API를 랩(wrap)해서 사용하라](#아이템-29-외부-API를-랩(wrap)해서-사용하라)
  - [아이템 30: 요소의 가시성을 최소화하라](#아이템-30-요소의-가시성을-최소화하라)
  - [아이템 31: 문서로 규약을 정의하라](#아이템-31-문서로-규약을-정의하라)
  - [아이템 32: 추상화 규약을 지켜라](#아이템-32-추상화-규약을-지켜라)
- [5장 객체 생성](#5장-객체-생성)
  - [아이템 33: 생성자 대신 팩토리 함수를 사용하라](#아이템-33-생성자-대신-팩토리-함수를-사용하라)
  - [아이템 34: 기본 생성자에 이름 있는 옵션 아규먼트를 사용하라](#아이템-34-기본-생성자에-이름-있는-옵션-아규먼트를-사용하라)
  - [아이템 35: 복잡한 객체를 생성하기 위한 DSL을 만들어라](#아이템-35-복잡한-객체를-생성하기-위한-DSL을-만들어라)
- [6장 클래스 설계](#6장-클래스-설계)
  - [아이템 36: 상속보다는 컴포지션을 사용하라](#아이템-36-상속보다는-컴포지션을-사용하라)
  - [아이템 37: 데이터 집합 표현에 data 한정자를 사용하라](#아이템-37-데이터-집합-표현에-data-한정자를-사용하라)
  - [아이템 38: 연산 또는 액션을 전달할 때 인터페이스 대신 함수 타입을 사용하라](#아이템-38-연산-또는-액션을-전달할-때-인터페이스-대신-함수-타입을-사용하라)
  - [아이템 39: 태그 클래스보다는 클래스 계층을 사용하라](#아이템-39-태그-클래스보다는-클래스-계층을-사용하라)
  - [아이템 40: equals의 규약을 지켜라](#아이템-40-equals의-규약을-지켜라)
  - [아이템 41: hashCode의 규약을 지켜라](#아이템-41-hashCode의-규약을-지켜라)
  - [아이템 42: compareTo의 규약을 지켜라](#아이템-42-compareTo의-규약을-지켜라)
  - [아이템 43: API의 필수적이지 않는 부분을 확장 함수로 추출하라](#아이템-43-API의-필수적이지-않는-부분을-확장-함수로-추출하라)
  - [아이템 44: 멤버 확장 함수의 사용을 피하라](#아이템-44-멤버-확장-함수의-사용을-피하라)
- [3부 효율성](#3부-효율성)
- [7장 비용 줄이기](#7장-비용-줄이기)
  - [아이템 45: 불필요한 객체 생성을 피하라](#아이템-45-불필요한-객체-생성을-피하라)
  - [아이템 46: 함수 타입 파라미터를 갖는 함수에 inline 한정자를 붙여라](#아이템-46-함수-타입-파라미터를-갖는-함수에-inline-한정자를-붙여라)
  - [아이템 47: 인라인 클래스의 사용을 고려하라](#아이템-47-인라인-클래스의-사용을-고려하라)
  - [아이템 48: 더 이상 사용하지 않는 객체의 레퍼런스를 제거하라](#아이템-48-더-이상-사용하지-않는-객체의-레퍼런스를-제거하라)
- [8장 효율적인 컬렉션 처리](#8장-효율적인-컬렉션-처리)
  - [아이템 49: 하나 이상의 처리 단계를 가진 경우에는 시퀀스를 사용하라](#아이템-49-하나-이상의-처리-단계를-가진-경우에는-시퀀스를-사용하라)
  - [아이템 50: 컬렉션 처리 단계 수를 제한하라](#아이템-50-컬렉션-처리-단계-수를-제한하라)
  - [아이템 51: 성능이 중요한 부분에는 기본 자료형 배열을 사용하라](#아이템-51-성능이-중요한-부분에는-기본-자료형-배열을-사용하라)
  - [아이템 52: mutable 컬렉션 사용을 고려하라](#아이템-52-mutable-컬렉션-사용을-고려하라)

---

# 1부: 좋은 코드


# 1장 안정성

코틀린은 안전성이 뛰어나다.

코틀린을 안전하게 사용하기 위한 방법들에 대해서 코틀린이 제공하는 기능과 올바르게 사용하는 방법을 알아보자.

## 아이템 1: 가변성을 제한하라

가변성이 발생하는 코드는 프로그래밈을 이해하고 디버그하기가 힘들어진다.

var를 사용하거나 mutable 객체를 사용하면 상태를 가질 수 있다. 

* 그러나 상태를 갖는 부분들의 관계를 이해해야 하기 때문에 상태 변경이 많아지면 이를 추적하는것이 힘들어진다.

* 가변성(mutability)이 있으면 시점에 따라 값이 달라질 수 있으므로 코드의 실행을 추론이나 예측하기 어렵다.

* 모든 상태와 조합을 테스트해야하므로 테스트하기 어렵다. 

때문에 코틀린에서는 가변성을 제한할 수 있다

- 읽기 전용 프로퍼티 val (val은 불변을 의미하는것은 아니다)
- 가변 컬렉션(mutable)과 읽기 전용(immutable) 컬렉션 구분하기
- data class의 copy



**val는 읽기 전용 프로퍼티지만, 변경할 수 없음을 의미하는 것은 아니다.** 

만약 완전히 변경할 필요가 없다면, final 프로퍼티를 사용하는 것이 좋다

* 참조에 대한 불변성(immutable reference)만을 보장하며, 참조하는 객체의 내부 상태에 대한 불변성(immutable state)은 보장하지 않는다는 의미이다.

```kotlin
data class MutableData(var data: Int)

fun main() {
    val myData = MutableData(1)

    println(myData.data)  // 출력: 1

    // val은 재할당할 수 없지만,
    myData = MutableData(999) // 에러 
    // myData = MutableData(2)  // 이런 코드는 컴파일 에러 발생

    // 객체의 내부 상태는 변경 가능
    myData.data = 2
    println(myData.data)  // 출력: 2
}
```

**가변 컬렉션(mutable)과 읽기 전용(immutable) 컬렉션 구분하기**

프로퍼티도 읽고 쓸 수 있는 프로퍼티와 읽기 전용 프로퍼티로 구분되며, 컬렉션도 읽고 쓸 수 있는 컬렉션과 읽기 전 컬렉션으로 구분된다.

* Collection과 MutableCollection,  List와 MutableList

하지만 "읽기 전용"이라고 해서 그 내부의 값이 절대 변경될 수 없는 것은 아니다. 

컬렉션 내부의 실제 객체는 가변적일 수 있으며, 참조를 통해 접근해서 변경할 수 있다. 

`"읽기 전용"`이라는 것은 해당 `컬렉션 인터페이스가 변경 연산을 지원하지 않는다`는 것만을 의미한다.

```kotlin
예를 들면, Iterables<T>.map과 Iterables<T>.filter와 같은 함수들은 내부적으로 ArrayList를 반환

public inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {
    return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), transform)
}
```

* 내부적으로 인터페이스를 사용하므로 실제 컬렉션을 리턴할 수 있으며, 플랫폼 고유의 컬렉션을 사용할 수 있다. 이는 코틀린이 내부적으로 immutable하지 않은 컬렉션을 외부적으로 immutable하게 보이게 만들어서 얻어지는 안정성이다. 

이말은 즉슨, 코틀린은 `자바와 호환성을 유지하기 위해 자바의 컬렉션과 연동`되는데 `자바 컬렉션은 가변적(mutable)`이다. 

그런데 `읽기 전용 컬렉션 인터페이스 (List 등)을 제공`함으로써, <u>실제 내부적으로 컬렉션은 가변적일 수 있지만</u>,

 '읽기 전용' 인터페이스를 통해 접근하게 되면 해당 컬렉션에 변경을 가하는 메서드를 사용할 수 없게되는것이다! 

때문에 이러한 방식으로 코틀린은 (자바의)변경 가능한 컬렉션을 안전하게(불변으로) 사용하도록 도와준다.

* 자바와의 호환성도 지키고, 불변으로도 제공할 수 있게 도와주는것이다. 

 `그러나 이는 개발자가 강제로 다운캐스팅을 할 때 문제가 된다`

```kotlin
@Test
internal fun `down casting 위반`() {
    val list = listOf(1, 2, 3) // 불변 List. 자바의 Array.ArrayList 인스턴스를 리턴한다. 
    
    if (list is MutableList) { // 강제로 다운캐스팅 
        list.add(1) // java.lang.UnsupportedOperationException 오류 발생
    }
}
```

* 읽기 전용이라는 규약을 무시하고 추상화를 무시하는 행위이다. 
* 변경은 가능하지만 Arrays.ArrayList는 이러한 연산을 구현하고 있지 않아 위와 같은 오류가 발생한다. 

만약 읽기 전용(immutable)에서 mutable로 변경해야 한다면 list.toMutableList를 활용 해야 한다.(copy 를 통해서) 

**데이터 클래스의 Copy**

String이나, Int 처럼 내부적인 상태를 변경하지 않는 immutable 객체를 많이 사용하는데 이유가 있다

1. 한 번 정의된 상태가 유지되므로, <u>코드를 이해하기 쉽습니</u>다.
2. immutable 객체는 공유했을 때도 <u>충돌이 따로 이루어지지 않으므로, 병렬 처리를 안전하게 할 수 있</u>습니다.
3. immutable 객체에 대한 <u>참조는 변경되지 않으므로, 쉽게 캐시할 수 있습니다.</u>
4. immutable 객체는 방어적 복사본을 만들 필요가 없습니다. 또한 객체를 복사할 때 깊은 복사를 따로 하지 않아도 됩니다.
5. immutable 객체는 다른 객체를 만들 때 활용하기 좋습니다. <u>immutable 객체는 실행을 더 쉽게 예측할 수 있습니다.</u>
6. immutable 객체는 s<u>et, map의 키로 사용할 수 있습니</u>다. 이는 set, map은 내부적으로 해시 테이블을 사용하고, 해시 테이블은 처음 요소를 넣을 때 요소의 값을 기반으로 버킷을 결정하기 때문입니다.

data 한정자의 copy 메서드는 모든 기본 생성자 프로퍼티가 같은 새로운 객체를 만들어 낼 수 있다.

```kotlin
data class User(
        val name: String,
        val surname: String,
) {
  fun withSurname(surname: String) = User(name, surname)
    
  override fun toString(): String {   
    return "User(name='$name', surname='$surname')"
  }
  
}

var user = User("Maja", "Markiewicz")
user = user.copy(surname = "Moskla")
println(user) // User(name='Maja', surname='Moskla')
```

**변경 가능한 리스트**

방법 1. mutable 컬렉션

방법 2. var로 선언

``` kotlin
val list1: MutableList<Int> = mutableListOf()
var list2: List<Int> = listOf


list1.add(1) // 이건 
list1 +=1 // 위와 같음 -> list1.plusAssign(1)로 변경됌

list2 = list2 + 1 //이건
list2 += 1 // -> list2 = list1.plus(1)로 변경됌 

```

**변경 가능 지점 노출하지 말기**

mutable 객체를 외부에 노출하지 말자.

```kotlin
data class User(val name: String)

  
class UserRepository {
  private val storedUsers: MutableMap<Int, String> = mutableMapOf()

  fun loadAll(): MutableMap<Int, String> {
    return storedUsers
  }
}

val userRepository = UserRepository()
val storedUsers = userRepository.loadAll()
storedUsers[4] = "Kirill" 
```

loadAll을 사용해서 private 상태인 UserRepositroy를 수정할 수 있게 되버린다.

때문에 읽기 전용 객체로 업캐스트 하여 가변성을 제한하자.

```kotlin
class UserRepository {
  private val storedUsers: MutableMap<Int, String> = mutableMapOf()

  fun loadAll(): Map<Int, String> { // 업캐스트 
    return storedUsers
  }
}
```



## 아이템 2: 변수의 스코프를 최소화하라

프로퍼티보다는 지역변수를 사용하는것이 좋다

특히 mutable 프로퍼티는 좁은 스코프에 있는것이 좋다. 그래야 추적하기도 쉽고 이해하고 변경하기도 쉽다 

## 아이템 3: 최대한 플랫폼 타입을 사용하지 말라

플랫폼 타입이란, 다른 프로그래밍언어 (ex java)에서 넘어온 타입으로, nullable인지 아닌지 알수 없는 타입을 말한다.

자바를 코틀린과 함께 사용할 때, 가능하다면 jetbrain의 @Nullable과 @NotNull어노테이션을 붙여 사용하면 알려줄 수 있어서 좋다. 

* JSR 305, jetbrain

## 아이템 4: inferred 타입으로 리턴하지 말라

코틀린에서 **inferred 타입**(유추된 타입)은 컴파일러가 변수의 타입을 자동으로 추론하여 결정하는 것을 의미한다.

inferred 타입은 할당때 정확히 오른쪽에 있는 피연산자에 맞게 설정된다

```kotlin
open class Animal

class Zebra: Animal()

var animal = Zebra() // Zebra 타입
animal = Animal() // 오류 
```

리턴타입은 확실하게 명시적으로 지정해주는것이 좋다. 



## 아이템 5: 예외를 활용해 코드에 제한을 걸어라

코틀린에서 제공하는 다양한 함수로 예외를 이용할 수 있다.

*  require 블록: 아규먼트를 제한할 수 있습니다.  // IllegalArgumentException
*  check 블록: 상태와 관련된 동작을 제한할 수 있습니다.  // IllegalStateException
* assert 블록: 어떤 것이 true인지 확인할 수 있습니다. assert 블록은 테스트 모드에서만 작동합니다. 
*  return 또는 throw와 함께 활용하는 Elvis 연산자

```kotlin
fun divide(a: Int, b: Int): Int {
    require(b != 0) { "Divider must not be zero." }
    return a / b
}

fun withdraw(amount: Double) {
    val balance = 100.0
    check(amount <= balance) { "Insufficient funds." }
    // withdrawal logic
}

fun calculateSquareRoot(value: Double): Double {
    assert(value >= 0) { "Value must be non-negative." }
    return Math.sqrt(value)
}

```

메소드 아규먼트로 넘어온 값은 주로 require를 이용하고 지연 메시지를 전달한다.

check는 구체적인 조건을 만족할 때만 함수를 사용할 수 있게 한다.

예를 들어 다음과 같은 경우

- ﻿﻿어떤 객체가 미리 초기화되어 있어야만 처리를 하게 하고 싶은 함수
- ﻿﻿사용자가 로그인했을 때만 처리를 하게 하고 싶은 함수
- ﻿﻿객체를 사용할 수 있는 시점에 사용하고 싶은 함수

## 아이템 6: 사용자 정의 오류보다는 표준 오류를 사용하라

표준 라이브러리의 오 류는 많은 개발자가 알고 있으므로, 이를 재사용하는 것이 좋습니다. 

잘 만들 어진 규약을 가진 널리 알려진 요소를 재사용하면, 다른 사람들이 API를 더 쉽 게 배우고 이해할 수 있습니다.

## 아이템 7: 결과 부족이 발생할 경우 null과 Failure를 사용하라

함수가 원하는 결과를 만들어 낼 수 없는 경우 아래를 고려하자.

- ﻿﻿nuLL 또는 '실패를 나타내는 sealed 클래스(일반적으로 Failure라는 이름을 붙입니다)를 리턴한다.
- ﻿﻿예외를 throw한다.

예외는 정보 전달 방법이 아니고 특별한 상황을 나타내는것이므로 예외적인 상황이 발생했을때 사용하는것이 좋다. 

* 이유는 - 추적 어려울 수 있음
* 코틀린 모든 예외는 unchecked임 
* try-catch 블록 내에 코드 배치시, 컴파일러가 할 수 있는 최적화가 제한됌

Result 객체와 Failure를 사용하자

```kotlin
internal class Failure(
    @JvmField
    val exception: Throwable
) : Serializable {
    override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
    override fun hashCode(): Int = exception.hashCode()
    override fun toString(): String = "Failure($exception)"
}

@SinceKotlin("1.3")
@JvmInline
public value class Result<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) : Serializable {
}


class Success<out T>(val result: T): Result<T>()
```



```kotlin
val person = userText.readObjectOrNull<Person>()

val age = when(person) {
  is Success -> person.age
  is Failure -> -1
}
```



일반적으로 아래 형태의 함수를 사용한다.

* get
* getOrNull
* getOrDefault

코틀린의 `Result` 객체는 성공적인 결과와 실패한 결과를 모두 처리할 수 있는 간단한 방법을 제공합니다. 이는 주로 예외 처리 대신 사용되며, 함수의 반환값으로 성공 또는 실패를 나타내는 데 유용합니다.

### 1. 기본 구조
`Result` 객체는 두 가지 상태를 가집니다:
- **Success**: 작업이 성공적으로 수행된 경우 결과 값을 포함합니다.
- **Failure**: 작업이 실패한 경우 예외를 포함합니다.

### 2. 사용 예시
다양한 사용 방법을 통해 `Result` 객체를 활용하는 방법을 설명하겠습니다.

#### 1. 결과 생성
`Result.success`와 `Result.failure`를 사용하여 결과를 생성할 수 있습니다.

```kotlin
val successResult: Result<Int> = Result.success(42)
val failureResult: Result<Int> = Result.failure(Exception("Error occurred"))
```

#### 2. 결과 처리
`Result` 객체의 `fold` 함수를 사용하여 성공과 실패를 각각 처리할 수 있습니다.

```kotlin
fun processResult(result: Result<Int>) {
    val output = result.fold(
        onSuccess = { value -> "Success: $value" },
        onFailure = { exception -> "Failure: ${exception.message}" }
    )
    println(output)
}

processResult(successResult)
processResult(failureResult)
```

#### 3. 안전한 호출
`getOrNull` 또는 `exceptionOrNull` 메서드를 사용하여 안전하게 결과를 호출할 수 있습니다.

```kotlin
val valueOrNull = successResult.getOrNull() // 42
val exceptionOrNull = failureResult.exceptionOrNull() // Exception("Error occurred")
```

#### 4. 변환
`map` 및 `flatMap` 메서드를 사용하여 결과를 변환할 수 있습니다.

```kotlin
val transformedResult = successResult.map { it * 2 } // Result.success(84)
val flatMappedResult = successResult.flatMap { Result.success(it * 3) } // Result.success(126)
```

#### 5. 예외 처리
`onFailure` 메서드를 사용하여 실패한 경우의 동작을 정의할 수 있습니다.

```kotlin
failureResult.onFailure { exception ->
    println("Handled exception: ${exception.message}")
}
```

### 3. 사용 예시
`Result` 객체를 활용한 간단한 함수 예시를 보겠습니다.

```kotlin
fun safeDivide(a: Int, b: Int): Result<Int> {
    return if (b != 0) {
        Result.success(a / b)
    } else {
        Result.failure(IllegalArgumentException("Divider must not be zero."))
    }
}

fun main() {
    val result1 = safeDivide(10, 2)
    val result2 = safeDivide(10, 0)

    processResult(result1) // "Success: 5"
    processResult(result2) // "Failure: Divider must not be zero."
}
```

### 결론
`Result` 객체는 예외 처리를 간소화하고, 명확하게 성공과 실패를 다룰 수 있는 유용한 도구입니다. 다양한 메서드를 활용하여 결과를 쉽게 관리하고 변환할 수 있으므로, 코드의 가독성을 높이고 오류 처리를 보다 효과적으로 수행할 수 있습니다.

## 아이템 8: 적절하게 null을 처리하라



not-null assertsion!! 은 사용하기 쉽지만 좋은 해결방법은 아니다

lateInit이나 Delegates.notNull이 더좋다.

또한 의미없이 ? 로 nullable하게 짜면 위험한!!를 사용하게 되어 의미 없이 코드를 더럽힐 수 있다.

```kotlin
private lateinit var controller: UserController

@Test
fun test() {
  controller.doSome()
}
```

lateinit은 프로퍼티를 사용하기 전 반드시 초기화될거라고 예상되는 상황에 활용한다.



Int, Long, Double, Boolean 같이 기본 타입으로 초기화해야하는경우, lateinit보단 약ㄱ간 느리지만 Delegates.notnull을 사용한다

```kotlin
private var fromNotification: Boolean by Delegates.notnull()
```

프로퍼티 위임도 가능

```kotlin
private var fromNotification: Boolean by arg(FROM_NTOFICATION_ARG)
```

## 아이템 9: use를 사용하여 리소스를 닫아라

try-resource구문 대신 use 확장함수 사용시 모든 Closeable 객체에 사용가능하다

```kotlin
val reader = BufferedReader(file)

reader.use {
  return reader.read()
}
```

use를 사용하면 Closeable/Autocloseable을 구현한 객체를 쉽고 안전하게 처리할 수 있습니다. 또한 파일을 처리할 때는 파일을 한 줄씩 읽어 들이는 useLines를 사용하는 것이 좋습니다.

## 아이템 10: 단위 테스트를 만들어라



- ﻿﻿복잡한 부분
- ﻿﻿계속해서 수정이 일어나고 리팩터링이 일어날 수 있는 부분
- ﻿﻿비즈니스 로직 부분
- ﻿﻿공용 API 부분
- ﻿문제가 자주 발생하는 부분
- ﻿수정해야 하는 프로덕션 버그


# 2장 가독성

## 아이템 11: 가독성을 목표로 설계하라

```kotlin
fun main() {
    val person: Person? = Person()
    val view = View()

    // Implementation A
    if (person != null && person.isAdult) {
        view.showPerson(person)
    } else {
        view.showError()
    }

    // Implementation B - Kotlin idiomatic
    person?.takeIf { it.isAdult }   // safe call, takeIf
        ?.let(view::showPerson)     // bounded function reference
        ?: view.showError()         // Elvis operator
}
```



## 아이템 12: 연산자 오버로드를 할 때는 의미에 맞게 사용하라

쓸데없이 연산자 오버로딩을 하지말자. 예를들어 ! 는 not으로 쓰이지 factorial로 쓰면 안된다

예를들어 * 로 여러번 호출한다면

```kotlin
val tripledHello = 3 * {print("hello")}

operator fun Int.times(operation: () -> Unit) {
  repeat(this) { operation() }
}
```

누구는 이해하고 누구는 이해 못할수도 있으므로, infix를 활용한 확장함수를 사용하는것이 좋다

```kotlin 
infix fun Int.timesRepeated(operation: () -> Unit) = {
  repeat(this) {operation() }
}

val tripledHello = 3 timesRepeated{} print("hello") }
```



이 규칙을 무시해도 되는 경우는 DSL을 설계할때다



## 아이템 13: Unit?을 리턴하지 말라

Unit?을 리턴한다면 이유는 무엇일까?

마치 Boolean이 true,false갖는것처럼 Unit?은 Unit 또는 null이라는 값을 가질 수 있다.



이것을 읽기 힘드므로 사용하지 마라.

```kotlin
fun keyIsCorrect(key: String): Boolean = false
fun verifyKey(key: String): Unit? = null    // isomorphic to Boolean but misleading and confusing


fun main() {
    if (!keyIsCorrect("key")) return

    verifyKey("key") ?: return // 이게 뭐야 
}
```

## 아이템 14: 변수 타입이 명확하게 보이지 않는 경우 확실하게 지정하라

일반 변수

```kotlin
val num = 10
```

말고, 특정 메소드 등에 의한 반환타입은 명확하게 지정하는게 좋다

```kotlin
val userData: UserData = someMethod()
```



## 아이템 15: 리시버를 명시적으로 참조하라

스코프 내부에 둘 이상의 리시버가 있는 경우 명시적으로 나타내자.

apply, with, run 등

nullable 값을 처리할때는 also나 let을 사용하자

```kotlin
class Node(val name: String) {
  fun makeChild(childName: String)
   = create("$name.$childName")
  	.also { print("Created ${it?.name}") }
}
```

외부에 있는 리시버를 사용하려면 label을 사용해야 한다. 

```kotlin
class Node(val name: String) {
  fun makeChild(childName: String)
   = create("$name.$childName")
  	.apply { print("Created ${it?.name}" 
                  + " ${this@Node.name}"
                  ) }
}
```

짧게 적을 수 있따는 이유만으로 리시버를 제거하면 가독성이 떨어지므로 명시적으로 적어주자 

## 아이템 16: 프로퍼티는 동작이 아니라 상태를 나타내야 한다

프로퍼티랑 자바의 필드는 둘다 데이터를 저장한다는 점은 같다.

그러나 프로퍼티는 더 많은 기능을 가진다. 사용자 정의 커스텀 세터,게터이다.

또한 백킹 필드를 이용해 구현에도 사용되며 백킹 필드는 fieldfㅏ는 식별자가 있다

```kotlin
var name: String ? = null
	get() = field?.toUpperCase()
	set(value) {
    if (!value.isNullOrBlank()) {
      field = value 
    }
  }
```

val을 사용해서 읽기 전용으로 만들면 field가 만들어지지 않는다

```kotlin
val fullName: String
	get() = "$name $surname"
```

이처럼 코틀린의 모든 프로퍼티는 디폴트로 캡슐화 되어 있으며

프로퍼티는 필드가 필요 없다. 개념적으로는 getter, setter를 나타내어서 인터페이스에도 프로퍼티를 정의할 수 있는것이다.

```kotlin
interface Person {
  val name: String
}
```

이는 게터를 가질거라는것을 의미한다. 즉 함수라는 것이다.

원칙적으로 프로퍼티는 상태를 나타내거나 설정하기 위한 목적으로만 사용하는것이 좋고 다른 로직 등을 포함하지 않아야 한다.

접두사로 get 또는 set을 붙일것인가?가 아니라면 프로퍼티로 만드는것은 좋지 않다. 

경험적으로 많은 사람들은 프로퍼티는 상태, 함수는 행동을 나타낸다고 생각한다. 

## 아이템 17: 이름 있는 아규먼트를 사용하라

코드가 길어지지만 두가지 장점이 있다.

* 이름을 기반으로 값이 무엇을 나타내는지 알 수 있다.
* 파라미터 입력 순서와 상관 없으므로 안전하다. 

이름 있는 아규먼트는 디폴트 값들을 생략할때만 유용한것이 아닌 읽을때도 편리하며 코드 안정성도 향상시킬 수 있다. 

## 아이템 18: 코딩 컨벤션 지켜라



# 2부: 코드 설계


# 3장 재사용성



## 아이템 19: knowledge를 반복하지 말라

프로젝트에서 이미 있던 코드를 복사해서 붙여넣고 있다면, 무언가가 잘못된것이다.

knowledge는 프로그래밍에서 넓은 의미로 의도적인 정보를 뜻한다. 

종류로는, 알고리즘 작동 방식, ui 형태, 원하는 결과 등이다.

우리 프로그램에서 중요한 knowledge를 크게 두 가지 뽑는다면, 다음과 같 습니다.

1. ﻿﻿﻿로직(logic): 프로그램이 어떠한 식으로 동작하는지와 프로그램이 어떻게 보이는지
2. ﻿﻿﻿공통 알고리즘(common algorithm): 원하는 동작을 하기 위한 알고리즘

둘의 차이점은 시간에 따른 변화다. 비즈니스 로직은 시간이 지나면서 변하지만 공통 알고리즘은 크게 변하지 않는다. 최적화는 하겠지만. 

요즘 대부분 모든것은 변한다. 이에 대비해야 한다. 변화할때 가장 큰 적은 knowledge가 반복되어 있는 부분이다. 변경의 전파를 조심해야 한다. 

knowledge 반복은 확장성을 막고 쉽게 깨지게 많든다. 

단일 책임 원칙은 잘못된 knowledge 중복을 막아준다.

구분을 잘 해서 다른 knowledge는 분리하는것이 좋다 그렇지 않으면 재사용해서는 안되는 부분을 재사용하려는 유혹이 발생할 수 있다. 



## 아이템 20: 일반적인 알고리즘을 반복해서 구현하지 말라



## 아이템 21: 일반적인 프로퍼티 패턴은 프로퍼티 위임으로 만들어라



## 아이템 22: 일반적인 알고리즘을 구현할 때 제네릭을 사용하라



## 아이템 23: 타입 파라미터의 섀도잉을 피하라



## 아이템 24: 제네렉 타입과 variance 한정자를 활용하라



## 아이템 25: 공통 모듈을 추출해서 여러 플랫폼에서 재사용하라



# 4장 추상화 설계



## 아이템 26: 함수 내부의 추상화 레벨을 통일하라



## 아이템 27: 변화로부터 코드를 보호하려면 추상화를 사용하라



## 아이템 28: API 안정성을 확인하라



## 아이템 29: 외부 API를 랩(wrap)해서 사용하라



## 아이템 30: 요소의 가시성을 최소화하라



## 아이템 31: 문서로 규약을 정의하라



## 아이템 32: 추상화 규약을 지켜라



# 5장 객체 생성



## 아이템 33: 생성자 대신 팩토리 함수를 사용하라



## 아이템 34: 기본 생성자에 이름 있는 옵션 아규먼트를 사용하라



## 아이템 35: 복잡한 객체를 생성하기 위한 DSL을 만들어라



# 6장 클래스 설계



## 아이템 36: 상속보다는 컴포지션을 사용하라



## 아이템 37: 데이터 집합 표현에 data 한정자를 사용하라



## 아이템 38: 연산 또는 액션을 전달할 때 인터페이스 대신 함수 타입을 사용하라



## 아이템 39: 태그 클래스보다는 클래스 계층을 사용하라



## 아이템 40: equals의 규약을 지켜라



## 아이템 41: hashCode의 규약을 지켜라



## 아이템 42: compareTo의 규약을 지켜라



## 아이템 43: API의 필수적이지 않는 부분을 확장 함수로 추출하라



## 아이템 44: 멤버 확장 함수의 사용을 피하라



# 3부 효율성



# 7장 비용 줄이기



## 아이템 45: 불필요한 객체 생성을 피하라



## 아이템 46: 함수 타입 파라미터를 갖는 함수에 inline 한정자를 붙여라



## 아이템 47: 인라인 클래스의 사용을 고려하라



## 아이템 48: 더 이상 사용하지 않는 객체의 레퍼런스를 제거하라



# 8장 효율적인 컬렉션 처리



## 아이템 49: 하나 이상의 처리 단계를 가진 경우에는 시퀀스를 사용하라



## 아이템 50: 컬렉션 처리 단계 수를 제한하라



## 아이템 51: 성능이 중요한 부분에는 기본 자료형 배열을 사용하라



## 아이템 52: mutable 컬렉션 사용을 고려하라





