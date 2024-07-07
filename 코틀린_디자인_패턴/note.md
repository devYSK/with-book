

# 코틀린 디자인 패턴

* https://github.com/AcornPublishing/kotlin-design-patterns-2e*

[toc]



알겠습니다! 주어진 목차에서 1개(#)와 2개(##) 짜리 목차만 Markdown 형식으로 정리하겠습니다:

# 2장. 생성 패턴 사용하기
생성 패턴이란, 언제 어떻게 객체를 생성할지 관한 디자인 패턴이다. 

## 기술적 요구 사항

## 싱글톤 패턴
싱글톤 디자인 패턴의 요구사항 두 가지는 다음과 같다.

1. 시스템에 인스턴스가 딱 하나만 존재해야 한다
2. 시스템의 모든 부분에서 인스턴스에 접근할 수 있어야 한다.

싱글톤 인스턴스 생성은 lazy하고 스레드 세이프하며 성능을 저해하지 않도록 해야한다.

- ﻿﻿게으른 인스턴스 생성: 프로그램이 시작되자마자 싱글톤 인스턴스가 만들어지면 안 된다. 인스턴스 생성에 많은 비용이 들 수 있기 때문이다. 인스턴스 생성은 필요한 첫 순간에 이뤄져야 한다.
- ﻿﻿스레드 안전한 인스턴스 생성: 두 스레드가 동시에 싱글톤 객체를 생성하려고 할 때 두 스레드가 같은 인스턴스를 획득해야 한다. 
- ﻿﻿고성능의 인스턴스 생성: 많은 스레드가 동시에 싱글톤 객체를 생성하려고 할 때 스레 드를 너무 오래 기다리게 하면 안 된다. 잘못하면 실행이 중단될 수 있다.



코틀린에서는 싱글톤 객체 생성을 쉽게 할 수 있도록 object 키워드가 도입됐다.

싱글톤 객체는 일반적인 클래스와 동일한 방법으로 선언하되 생성자는 정의하지 않는다. 직접 인스턴스화할 수 없기 때문이다.

싱글톤 객체는 다음과 같이 초기화가 필요하다면 init 블록을 사용할 수 있다.

```kotlin
object Logger {
    init {
        println("I was accessed for the first time")

        // Initialization logic goes here
    }

    fun log(message: String) {
        println("Logging $message")
    }
    // More code goes here
}
```

만약 싱글톤 객체에 한 번도 접근하지 않는다면 초기화 로직은 실행되지 않고, 따라서 자원이 절약된다. 

이를 게으른 초기화 lazy intialization라고 부른다.

## 팩토리 메서드 패턴

객체를 생성하는 메서드에 관한 디자인 패턴이다. 

```kotlin
fun createPiece(notation: String): ChessPiece {
    val (type, file, rank) = notation.toCharArray()
    return when (type) {
        'q' -> Queen(file, rank)
        'p' -> Pawn(file, rank)
        // ...
        else -> throw RuntimeException("Unknown piece: $type")
    }
}
```

### 정적 팩토리 메서드

자바에서는 흔히 static 메소드로 구현하곤 한다.

생성자 대신 정적 팩토리 메서드를 사용하는 이유는? 장점은?

- ﻿﻿다양한 생성자에 명시적인 이름을 붙일 수 있다. 클래스에 생성자가 많은 경우에 특히 유용하다.
- ﻿﻿일반적으로 생성자에서는 예외가 발생하지 않으리라는 기대가 있다. 그러나 클래스 인스턴스 생성이 절대 실패하지 않는 것은 아니다. 예외가 불가피하다면 생성자보다 는 일반적인 메서드에서 발생하는 편이 훨씬 낫다.
- ﻿﻿생성자에 기대하는 것이 한 가지 더 있다면 빠르다는 것이다. 그러나 생성하는 데에 시간이 오래 걸릴 수밖에 없는 객체도 있다. 그런 경우 생성자 대신 정적 팩토리 에서 드를 고려하라.

기술적인 장점은 어떤것이 있을까?

* 캐시 : 특정 객체.EMPTY나 숫자(Long, Int) 처럼 캐싱이 가능하다
* 하위 클래스 생성 : 정적 팩토리 메서드에서는 하위 클래스의 인스턴스도 생성 가능하다.

코틀린에서는 companion object를 이용해 만들 수 있다.

```kotlin
companion object {
    fun server(propertyStrings: List<String>): ServerConfiguration {
        val parsedProperties = mutableListOf<Property>()
        for (p in propertyStrings) {
            parsedProperties += property(p)
        }
        return ServerConfigurationImpl(parsedProperties)
    }
}
```

인스턴스가 정적 팩토리 메서드를 통해서만 생성하고 싶다면, 특정 클래스의 기본 생성자를 private으로 선언하면 된다

```kotlin
class Server private constructor(port: Long)
```

## 추상 팩토리 패턴

추상 팩토리랑 팩토리를 만들어내는 팩토리다. 

즉 여러 팩토리 메서드를 감싸는 클래스다. 

```kotlin
class Parser {

    companion object {
        fun server(propertyStrings: List<String>): ServerConfiguration {
            val parsedProperties = mutableListOf<Property>()
            for (p in propertyStrings) {
                parsedProperties += property(p)
            }

            return ServerConfigurationImpl(parsedProperties)
        }

        fun property(prop: String): Property {
            val (name, value) = prop.split(":")

            return when (name) {
                "port" -> IntProperty(name, value.trim().toInt())
                "environment" -> StringProperty(name, value.trim())
                else -> throw RuntimeException("Unknown property: $name")
            }
        }
    }
}
```

## 빌더 패턴

빌더 패턴을 사용하면 복잡한 객체를 보다 쉽게 만들 수 있다.

코틀린에서는 named argument가 있어서 빌드 패턴을 거의 사용할 일이 없다. 

## 프로토타입 패턴

유사하면서도 조금 다른 객체를 그때그때 목적에 맞게 생성하기 위해 사용하는 패턴. 

핵심 아이디어는 객체를 쉽게 복사할 수 있도록 하는것. 적어도 다음 두가지 경우 프로토타입 패턴이 필요하다

* 객체 생성에 많은 비용이 드는 경우(DB를 조회해야하는경우)
* 비슷하지만 조금씩 다른 객체를 생성하느라 비슷한 코드를 매번 반복하고 싶지 않은 경우

코틀린의 copy() 메서드는 다른 데이터 클래스의 인스턴스를 받아 복제본을 생성하며 원한다면 과정에서 속성을 변경할 수 있다.

## 질문

1. ﻿﻿﻿2장에서 배운 object 키워드의 쓰임새 두 가지를 말해 보라.

 싱글턴 객체를 생성시 사용한다.

무명 객체를 만들시 사용한다. (익명객체)

2. ﻿﻿﻿apply() 함수는 언제 사용하는가?

객체를 초기화하면서 동일한 객체 반환시 사용. 주로 객체의 설정을 간결하게 작성하기 위해 사용. 



3. ﻿﻿﻿정적 팩토리 메서드의 예를 하나 들어 보라.

companion ojbect 내에 정의한다. 

# 3장. 구조 패턴 이해하기

구조패턴은 객체간 관계를 다루는 디자인 패턴이다. 복잡한 상속 및 계층을만들지 않고도 객체의 기능을 확장할 수 있다.

* 데코레이터 패턴
* 어댑터 패턴
* 브리지 패턴
* 합성 패턴
* 퍼사드 패턴
* 경량 패턴
* 프록시 패턴 

## 데코레이터 패턴
속성이 아닌 동작이 조금씩 다른 클래스를 여럿 만들어야 한다면?

코틀린에서는 함수가 일급 객체이기 때문에 프로토타입 디자인 패턴을 사용해서 이 목적을 달성할 수 있다. 데코레이터 디자인 패턴을 구현하면 코드 사용자가 어떤 기능을 추가할지 자유롭게 선택할 수 있다.

```kotlin
interface StarTrekRepository {
    operator fun get(starshipName: String): String
    operator fun set(starshipName: String, captainName: String)
} // 인터페이스 


class LoggingGetCaptain(private val repository: StarTrekRepository) 
    : StarTrekRepository by repository {
    override fun get(starshipName: String): String {
        println("Getting captain for $starshipName")
        return repository[starshipName]
    }
}

```

by 키워드는 인터페이스 구현을 다른 객체한테 위임한다. 그래서 인터페이스에 선언된 get, set 모두를 구현할 필요가 없다. 인스턴스가 감싸고 있는 다른 객체가 모든 구현을 대신한다.

여기서는 클래스의 시그니처(signature)가 어떤 의미인지 주의 깊게 살펴봐야 한다. 데코레이터 패턴을 구현할 때 필요한 요소는 다음과 같다.

- ﻿﻿데코레이션(새로운 동작>을 추가할 대상 객체를 입력으로 받는다.
- ﻿﻿대상 객체에 대한 참조를 계속 유지한다.

- ﻿﻿데코레이터 클래스의 메서드가 호출되면 들고 있는 대상 객체의 동작을 변경할지 또는 처리를 위임할지 결정한다.
- ﻿﻿대상 객체에서 인터페이스를 추출하거나 또는 해당 클래스가 이미 구현하고 있는 인터페이스를 사용한다.



데코레이터의 메서드에서는 더이상 super 사용하지 않고 들고있는 대상 객체의 인터페이스 참조를 사용한다. 

### 데코레이터 패턴 사용시 주의할점

데코레이터 디자인 패턴은 즉석에서 객체를 만들어 낼 수 있기 때문에 **강력하다.**

그러나 한계가 있다.

데코레이터 객체의 속을 볼 수 없다. 속에 들어있는 객체가 (인터페이스로 들어오기 때문에) 무엇인지 알 수 없다. 때문에 캐스팅이나 타입검사시 주의해야 한다. 



## 어댑터 패턴

어떤 인터페이스를 다른 인터페이스로 변환하고자 할 때 사용한다. 

코틀린에서는 어뎁터를 구현할때 확장함수를 사용할 수 있다. 

USA타입 콘센트에 EU타입 플러그로 변환하려면?

```kotlin
fun USPlug.toEUPlug(): EUPlug {
    val hasPower = if (this.hasPower == 1) "TRUE" else "FALSE"
    return object : EUPlug {
        // Transfer power
        override val hasPower = hasPower
    }
}
```

* 확장함수 내부의 this는 확장 대상 객체.
* object:는 익명 객체 



## 브리지 패턴

상속을 남용하는 것을 막아주는 디자인 패턴이다. 

브리지 패턴의 동작 원리는 간단하다. 

클래스 계층 구조를 얉게 만들어 시스템에서 콘크리트 클래스의 수를 줄이는것이다. 

그렇게 되면 부모 클래스를 수정했을 때 자식 클래스에서 발견하기 어려운 버그가 발생하는 깨지기 쉬운 베이스 클래스 문제를 예방하는데에도 도움이 된다. 



```kotlin
interface Infantry {
  fun move()
  fun attackRebel()
  fun shout()
}
```

을 구현하는 대신, 속성을 인터페이스로 받는다.

```kotlin
typealias PointsOfDamage = Long
typealias Meters = Int

interface Weapon {
    fun attack(x: Long, y: Long): PointsOfDamage
}

interface Legs {
    fun move(x: Long, y: Long): Meters
}

data class StormTrooper(
    private val weapon: Weapon,
    private val legs: Legs
) : Trooper {
    override fun move(x: Long, y: Long) {
        legs.move(x, y)
    }

    override fun attackRebel(x: Long, y: Long) {
        println("Attacking")
        weapon.attack(x, y)
    }
}
// 아래처럼 사용한다
val stormTrooper = StormTrooper(Rifle(), RegularLegs())
val flameTrooper = StormTrooper(Flamethrower(), RegularLegs())
val scoutTrooper = StormTrooper(Rifle(), AthleticLegs())
```



typeAlias (타입 별칭)

새로운 타입을 만든것이 아님. 컴파일러는 컴파일 과정에서 해당 별칭을 특정 타입으로 해석한다.

다음과 같은 이점이 있다.

* 코드의 의미가 더 잘 드러난다.
* 코드가 더 간결해진다. 복잡한 제네릭 표현식을 숨길 수 있다.

## 합성 패턴

## 퍼사드 패턴

퍼사드는 지저분한 구현 세부사항을 감추는데 사용한다.

목적은 서로 관련된 여러 클래스나 인터페이스를 더 깔끔하고 간단하게 다룰 수 있도록 하는 것이다.

추상 팩토리는 객체 생성을 목적으로 하지만, 퍼사드는 사용하는데에 초점을 둔다. 

## 경량 패턴

매우 가벼운 객체라는 뜻으로 상태를 갖지 않는 객체를 말한다.

무거운 객체(예제는 이미지 파일)를 제한적으로 생성하고, 경량 객체들이 무거운 객체를 공유하도록 하는것이다. 

## 프록시 패턴

데코레이터 패턴과 마찬가지로 객체의 기능을 확장한다. 

프록시는 어떤 동작을 요청했을 때 완전히 다른식으로 동작할 수 있다.

간혹 프록시 디자인 패턴을 3개의 하위 패턴으로 분류하기도 한다.

- ﻿﻿가상 프록시: 게으른 방식으로 결과를 캐시한다.
- ﻿﻿원격 프록시: 원격지의 자원에 접근한다.
- ﻿﻿보호 프록시 또는 접근 제어 프록시: 인가되지 않은 접근을 거부한다.

### lazy 위임 함수.

기본적으로 lazy()함수는 호출을 synchronize 하게 호출해서 두 스레드 중 한 스레드만 실행 가능하고, 나머지 스레드는 실행이 완료될때까지 기다려야 한다.  만약에 lazy 블록을 동시에 실행해도 괜찮다면(예를 들어 그다지 값비싼 동작이 아니라면)

lazy(LazyThreadsafetyhode . PUBLICATION)을 사용할 수 있다.

## 질문

1. ﻿﻿﻿데코레이터 디자인 패턴과 프록시 디자인 패턴의 구현상 차이점은 무엇인가?

**데코레이터 디자인 패턴 (Decorator Pattern)**과 **프록시 디자인 패턴 (Proxy Pattern)**은 둘 다 클래스 구조를 확장하기 위한 디자인 패턴이지만, 그 목적과 구현 방법에 차이가 있습니다.

**데코레이터 패턴**:

- **목적**: 객체에 새로운 행동이나 책임을 추가할 때 사용됩니다. 객체를 감싸고, 감싼 객체에 추가적인 기능을 제공할 수 있습니다.
- **구현**: 데코레이터 클래스는 실제 객체와 같은 인터페이스를 구현하고, 내부적으로 실제 객체를 포함합니다. 호출을 받은 메서드를 처리하고, 추가 기능을 더한 후 실제 객체의 메서드를 호출합니다.

**프록시 패턴**:

- **목적**: 접근 제어, 리소스 관리, 로깅 등의 기능을 제공할 때 사용됩니다. 프록시는 실제 객체에 대한 접근을 제어합니다.
- **구현**: 프록시 클래스는 실제 객체와 같은 인터페이스를 구현하고, 실제 객체에 대한 참조를 보유합니다. 호출을 받은 메서드를 처리하고, 필요에 따라 실제 객체의 메서드를 호출하거나 호출을 제어합니다.

2. 경량 디자인 패턴의 주요 목적은 무엇인가?

*경량 디자인 패턴 (Flyweight Pattern)**의 주요 목적은 **메모리 사용을 최소화**하면서 **효율적인 객체 관리를** 하는 것입니다. 이를 위해, 동일하거나 유사한 객체를 공유하여 메모리 낭비를 줄입니다. 많은 수의 작은 객체가 사용될 때, 공유를 통해 메모리 사용량을 줄이고 성능을 향상시킬 수 있습니다.



3. 퍼사드 디자인 패턴과 어댑터 디자인 패턴의 차이는 무엇인가?

**퍼사드 디자인 패턴 (Facade Pattern)**과 **어댑터 디자인 패턴 (Adapter Pattern)**은 모두 객체 간의 인터페이스를 단순화하고 통합하기 위해 사용되지만, 그 목적과 방법에 차이가 있습니다.

**퍼사드 패턴**:

- **목적**: 복잡한 서브시스템의 인터페이스를 단순화하고, 사용하기 쉬운 고수준 인터페이스를 제공합니다.
- **구현**: 하나 이상의 클래스의 복잡한 인터페이스를 감싸는 단순한 인터페이스를 제공하여 클라이언트가 서브시스템을 쉽게 사용할 수 있게 합니다.

**어댑터 패턴**:

- **목적**: 호환되지 않는 인터페이스를 가진 클래스를 클라이언트가 기대하는 인터페이스로 변환합니다. 즉, 기존 클래스의 인터페이스를 변경하여 사용하고자 하는 클래스와 호환되도록 합니다.
- **구현**: 어댑터 클래스는 기존 클래스의 인터페이스를 구현하고, 내부적으로 기존 클래스의 인스턴스를 포함하여 호출을 변환합니다.



# 4장. 동작 패턴과 친해지기

객체가 상황에 따라 다르게 동작하도록 하는 방법, 

여러 객체가 서로를 알지 못해도 통 신할 수 있도록 하는 방법, 

복잡한 구조에 대해 반복을 수행하는 방법을 배울 것이다. 

일부 패턴을 쉽게 이해하기 위해 코틀린에서의 함수형 프로그래밍도 맛볼 것이다.



목적은 결합도가 낮고 유연한 코드를 작성하는것이다. 

## 전략 패턴
전략 패턴의 목표는 객체의 동작을 런타임에 변경하는것이다.

코틀린에서는 변수에 함수를 할당할 수 있으므로 너무 간단하게 할 수 있다.



## 반복자 패턴

컬렉션(예: 리스트, 배열 등) 요소들에 접근하는 방법을 제공하는 디자인 패턴.

이 패턴은 컬렉션의 내부 구조를 노출하지 않고 순차적으로 접근할 수 있게 합니다. 반복자 패턴은 다양한 데이터 구조를 일관된 방식으로 순회할 수 있게 해줍니다.

Iterator 인터페이스를 구현함으로써 반복자 패턴을 쉽게 구현할 수 있다. 

## 상태 패턴

객체가 상태에 따라 행동을 변경할 수 있도록 해주는 디자인 패턴입니다. 이 패턴을 사용하면 객체의 내부 상태가 변함에 따라 객체의 행동이 동적으로 변경됩니다. 상태 패턴은 객체의 행동을 캡슐화하고, 상태 전환을 객체 외부에서 관리할 수 있도록 합니다.

상태 패턴은 전략패턴의 일종이라고 볼 수 있으며, 상태 패턴의 상태는 오로지 입력에 의해 내부적으로만 변경된다.

## 명령 패턴

명령 패턴을 이용하면 객체 내부에 동작을 캡슐화해서 넣어둔 뒤 어떻게 실행할지 마음것 결정할 수 있다. 

```kotlin
open class Trooper {
    private val orders = mutableListOf<Command>()

    fun addOrder(order: Command) {
        this.orders.add(order)
    }

    fun executeOrders() {
        while (orders.isNotEmpty()) {
            val order = orders.removeFirst()
            order() // Compile error for now
        }
    }
    // More code here

    fun move(x: Int, y: Int) {
        println("Moving to $x:$y")
    }
}

typealias Command = () -> Unit

fun main() {
    val t = Trooper()

    t.addOrder(moveGenerator(t, 1, 1))
    t.addOrder(moveGenerator(t, 2, 2))
    t.addOrder(moveGenerator(t, 3, 3))

    t.executeOrders()
}

```



## 책임 사슬 패턴

웹 서버에서 보편적으로 사용되는 개념이다.

복잡한 로직을 여러개의 작은 단계로 쪼개서 진행하는 디자인 패턴이다. 

## 해석기 패턴

특정한 언어를 번역할 때 사용하는 디자인 패턴이다. 

sql을 위한 도메인 특화 언어

```kotlin
fun main() {

    val sql = select("name, age") {
        from("users") {
            where("age > 25")
        } // Closes from
    } // Closes select

    println(sql) // "SELECT name, age FROM users WHERE age > 25"
}
```

## 관찰자 패턴
publisher, subscriber 개념. 발행자에게 어떤 일이 발생하면 모든 구독자가 그 사실을 알게 된다.

## 질문

1. ﻿﻿﻿중개인 패턴과 관찰자 패턴의 차이점은 무엇인가?

**중개인 패턴 (Mediator Pattern)**과 **관찰자 패턴 (Observer Pattern)**은 객체 간의 상호작용을 관리하는 디자인 패턴이지만, 그 목적과 구현 방식에 차이가 있습니다.

**중개인 패턴 (Mediator Pattern)**:

- **목적**: 객체들 간의 직접적인 상호작용을 피하고, 대신 중개자 객체를 통해 상호작용을 관리하여 객체 간의 결합도를 낮춥니다.
- **구현**: 중개인 클래스가 모든 상호작용을 관리하며, 객체들은 중개인을 통해서만 서로 통신합니다.

**관찰자 패턴 (Observer Pattern)**:

- **목적**: 한 객체의 상태 변화에 따라 다른 객체들이 그 변화를 통지받고 자동으로 갱신되도록 합니다.
- **구현**: 관찰자 인터페이스를 정의하고, 구독자들이 관찰자에 등록하여 상태 변화 시 알림을 받습니다.

2. ﻿﻿﻿도메인 특화 언어pSL, Domain-Specififc Language란 무엇인가?

**도메인 특화 언어 (DSL, Domain-Specific Language)**는 특정 도메인이나 문제 영역에 특화된 프로그래밍 언어입니다. DSL은 범용 프로그래밍 언어와 달리, 특정 문제를 해결하기 위해 설계된 언어로, 그 도메인에 맞춘 문법과 기능을 제공합니다.

**특징**:

- **전문화**: 특정 도메인에 맞춰져 있어, 해당 도메인의 문제를 쉽게 표현하고 해결할 수 있습니다.
- **표현력**: 도메인 특화 용어와 개념을 사용하여 코드가 도메인 전문가에게도 이해하기 쉽게 작성됩니다.
- **효율성**: 도메인 문제를 해결하는 데 필요한 작업을 간결하고 명확하게 표현할 수 있습니다.

**예시**:

- SQL: 데이터베이스 쿼리를 작성하기 위한 도메인 특화 언어
- HTML: 웹 페이지의 구조를 정의하기 위한 도메인 특화 언어
- Regular Expressions: 문자열 패턴 매칭을 위한 도메인 특화 언어

3. 봉인 클래스나 인터페이스를 사용하는 이유는 무엇인가?

**봉인 클래스(Sealed Class)**와 **봉인 인터페이스(Sealed Interface)**는 클래스 계층을 제한하고, 컴파일러가 클래스 계층을 완전히 알고 있도록 하는 기능을 제공합니다. 이는 더 안전하고 예측 가능한 코드 작성을 가능하게 합니다.

**사용 이유**:

1. **계층 제한**: 특정 계층구조 내에서만 확장을 허용하여, 예측 가능한 타입 계층을 제공합니다.
2. **패턴 매칭**: 모든 하위 클래스를 컴파일 타임에 알 수 있어, `when` 구문과 같은 패턴 매칭에서 누락된 경우를 컴파일러가 경고할 수 있습니다.
3. **안전성**: 클래스 계층이 외부에서 임의로 확장되지 않도록 하여, 라이브러리 설계 시 더 안전하게 사용할 수 있습니다.

# 5장. 함수형 프로그래밍 소개

## 함수형 프로그래밍을 사용하는 이유
기술의 발전보다 CPU 속도의 발전 정체로 인해 프로그램을 빠르게 만드려면 병렬화가 필요했다.

병렬화에는 불변한 함수형 프로그래밍 패러다임이 특별히 효과적이다. 

그러나 꼭 이런 이유 때문에 함수형 프로그래밍을 사용하는 것은 아니다. 다음과 같은 이유도 있다.

- ﻿﻿함수형 프로그래밍은 순수 함수를 선호하는데, 순수 함수는 대개 이해하고 테스트하 기 쉽다.
- ﻿﻿함수형으로 작성된 코드는 명령적imperatve이지 않고 선언적dedaratve이다. 즉 '어떻게보다 무엇을'에 초점을 맞추는데 이것은 장점이 될 수 있다.



## 불변성

### 순수 함수

순수 함수pure funotion란 부수 효과가 전혀 없는 함수다. 부수 효과란 외부 상태를 조회하거나 변경하는 모든 것을 말한다.

 외부 상태는 지역 변수가 아닌 변수(클로저에서 보이는 변수도 지역 변수로 볼 수는 없다)나 모든 종류의 I0(파일이나 각종 네트워크 위치에서 수행하는 읽기/쓰기 작업)일 수 있다.

순수하지 않은 함수impure function는 일반적으로 테스트하기도 어렵고 동작을 이해하기도 힘들다. 실행 순서나 네트워크 문제 등 통제하기 어려운 요소에 따라 함수의 반환값이 달라지기 때문이다.

한 가지 기억해야 할 부분은 로깅이나 콘솔 출력도 [O를 수행하기 때문에 같은 종류의

문제를 일으킨다는 점이다.

## 문 대신 식 사용하기
## 재귀 호출
재귀 호출의 단점인 스택 오버플로 오류를 코틀린의 꼬리 재귀 (tail recursion) 로 방지 및 최적화를 지원한다

## 질문

**고차 함수**: 함수를 인수로 받거나 함수를 반환하는 함수.

**`tailrec` 키워드**: 코틀린에서 꼬리 재귀 최적화를 위해 사용되는 키워드로, 컴파일러가 재귀 호출을 반복문으로 변환하여 스택 오버플로우를 방지.

**순수 함수**: 동일한 입력에 대해 항상 동일한 출력을 반환하고, 함수 외부의 상태를 변경하지 않는 함수.

1. **참조 투명성 (Referential Transparency)**: 동일한 입력에 대해 항상 동일한 출력을 반환합니다.
2. **부수효과 없음 (No Side Effects)**: 함수 외부의 상태를 변경하지 않습니다. 함수 내부에서 외부 변수의 값을 변경하거나, 입출력(IO) 연산을 수행하지 않습니다.

# 6장. 스레드와 코루틴

## 코루틴 소개
경량 스레드.

### 작업, job

비동기적으로 어떤 일을 시작했을 때 그 결과를 작업이라고 부른다. 

마치 Thread 객체가 실제 OS 스레드를 표현하는 것과 같이 J0b 객체는 실제 코루틴을 나타낸다.

작업의 생애 주기는 단순하다. 다음의 네 가지 상태를 가질 수 있다.

- ﻿﻿신규new: 생성됐으나 시작되지 않음
- ﻿﻿활성active; launch() 함수 등에 의해 시작됨. 기본 상태
- ﻿﻿완료completed: 모든 것이 순조롭게 진행됨
- ﻿﻿취소canceled; 무언가 잘못됨

자식이 있는 작업은 다음과 같은 두 가지 상태를 추가로 갖는다.

- ﻿﻿완료 중cmpleting: 완료하기 전에 자식이 실행되기를 기다리는 중
- ﻿﻿취소 중canceling: 취소하기 전에 자식이 실행되기를 기다리는 중

### 코루틴의 내부 동작 이해하기

지금까지 다음의 사실은 수 차례 언급했다.

- ﻿﻿코루티은 가벼운 스레드와도 같다. 스레드에 비해 적은 자원을 사용하기 때문에 더 많이 생성할 수 있다.
- ﻿﻿코루틴은 스레드를 통째로 멈추지 않고 자기 자신만 실행을 중단할 수 있다. 그동안 스레드는 다른 코드를 실행할 수 있다.



일반적으로 자식 코루틴이 실패하면 부모 코루틴의 실행을 실패시키는데,

하지만 만약 자식 코루틴에서 발생한 예외 때 문에 부모 코루틴이 종료되지 않도록 하려면 supervisorscope를 사용하면 된다.

supervisorscope를 사용하면 코루틴 중 하나가 실패하더라도 부모는 영향을 받지 않는다.

하지만 여전히 cancel() 함수를 통해 부모 코루틴을 취소하면 모든 자식 코루틴도 함께취소된다.

## 질문



구조화된 동시성에서는 동일한 부모 코루틴을 가지는 코루틴 중 하나가 실패하면, 다른 코루틴도 함께 취소됩니다. 이를 방지하려면 `supervisorScope`나 `SupervisorJob`을 사용하여 자식 코루틴이 독립적으로 작동하도록 할 수 있습니다.

#### `supervisorScope`

`supervisorScope`를 사용하면, 자식 코루틴이 실패해도 다른 자식 코루틴에 영향을 주지 않습니다.

#### `SupervisorJob`

`SupervisorJob`을 사용하여 코루틴을 생성하면, 그 자식 코루틴이 독립적으로 작동합니다.

```kotlin
fun main() = runBlocking {
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        val child1 = launch {
            try {
                delay(1000L)
                println("Child 1")
            } catch (e: Exception) {
                println("Child 1 failed: $e")
            }
        }
        
        val child2 = launch {
            delay(500L)
            throw RuntimeException("Child 2 failed")
        }

        joinAll(child1, child2)
    }
    println("Done")
}
```



### `yield()` 함수의 목적

`yield()` 함수는 현재 코루틴의 실행을 일시 중단하고, 다른 대기 중인 코루틴에게 실행 기회를 제공합니다. 이는 코루틴의 협력적 멀티태스킹을 가능하게 하여, 코루틴이 실행 중인 동안 다른 코루틴도 실행될 수 있게 합니다.

# 7장. 데이터 흐름 제어

## 동시성 자료 구조 소개
### Sequence

자바 8 스트림 API는 혁신이지만 두 가지 단점이 있었다.

1. 자바8로 버전 업그레이드를 해야함
2. 자료구조를 스트림으로 변환해야 한다는것. 이후 종결 함수 등인 collect로 호출해야 했다.

이를 위해 자바의 스트림과 이름충돌을 피하기 위한 Sequencerk 나왔다.

코틀린의 Sequence는 컬렉션을 lazy하게 평가하기 위한 클래스다. 

컬렉션의 요소들을 하나씩 순차적으로 평가하고, 필요할때만 연산하여 메모리와 성능을 최적화 할 수 있다. 

Lazy가 중요한 이유는 필요할때만 연산을 수행하기 때문에 실제 수행하지 않으면 연산을 지연시키므로 성능 최적화를 도모할 수 있다.

* 또한 모든 데이터를 한번에 메모리에 로드하지 않고 필요할때마다 하나씩 연산한다. 

### 채널

코루틴에서는 wait(), notify()가 없기 때문에 Channel을 사용해서 서로 통신한다.

또한 채널은 BlockingQueue와 비슷하지만 스레드를 블락하지 않고 코루틴만 블락한다. 

```kotlin
fun main() {
    runBlocking {
        val chan = Channel<Int>()

        launch {
            for (c in chan) {
                println(c)
            }
        }

        (1..10).forEach {
            chan.send(it)
        }
        chan.close()
    }
}

```

channel.close()를 호출하면, 이 채널에서 값을 읽던 코루틴은 할일을 수행하고 종료한다. 



채널에 값을 공급(publish)하는 코루틴이 필요하다면 produce() 함수를 사용해 생성한다. 

소비하는 코루틴에서는 consumeEach() 함수를 이용한다.

```kotlin
fun main() {
    runBlocking {
        val chan = produce {
            (1..10).forEach {
                send(it)
            }
        }

        launch {
            chan.consumeEach {
                println(it)
            }
        }
    }
}

```

### Flow

Flow는 Cold Stream이다.

각 메시지를 처리하는데 시간이 오래걸린다면 buffer를 사용한 채널이 좋다. 그렇지 않다면 메모리가 가득 차게 될것이다. 



## 질문

1. ﻿﻿﻿집합 자료 구조를 위한 고차 함수와 동시성 자료 구조를 위한 고차 함수의 차이는 무엇인가?

**집합 자료 구조를 위한 고차 함수**와 **동시성 자료 구조를 위한 고차 함수**는 목적과 사용되는 맥락이 다릅니다. 

#### 집합 자료 구조를 위한 고차 함수

집합 자료 구조는 리스트, 세트, 맵 등과 같은 컬렉션을 다루기 위한 함수들입니다.

#### 시성 자료 구조를 위한 고차 함수

동시성 자료 구조는 멀티스레드 환경에서 안전하게 사용할 수 있는 자료 구조를 다루기 위한 함수들입니다. 이러한 함수들은 스레드 간의 안전한 데이터 공유 및 동기화를 위해 사용됩니다.

- 예시
  - `ConcurrentHashMap`: 동시 접근을 지원하는 해시맵.
  - `CopyOnWriteArrayList`: 쓰기 시에 복사본을 만드는 리스트.
  - `synchronized`: 임계 구역을 보호하는 함수.



2. ﻿﻿﻿차가운 스트림과 뜨거운 스트림의 차이는 무엇인가?

#### 차가운 스트림 (Cold Stream)

차가운 스트림은 데이터 소비자가 구독을 시작할 때 데이터를 생산합니다. 즉, 구독이 발생하기 전에는 데이터가 생산되지 않습니다. 구독할 때마다 새로운 데이터 스트림이 시작됩니다.

- 특징
  - 구독자가 생길 때마다 데이터 스트림이 시작.
  - 각각의 구독자는 독립적인 데이터 스트림을 받음.

3. ﻿﻿﻿언제 채널을 뭉개야 하는가?



채널을 뭉개는 것 (cancelling a channel)**은 코루틴이 사용하는 채널의 작업을 취소하거나 중단해야 할 때 사용됩니다. 이는 주로 다음과 같은 상황에서 발생할 수 있습니다.

1. **작업 완료**: 더 이상 채널을 통해 데이터를 주고받을 필요가 없을 때.
2. **오류 발생**: 오류가 발생하여 정상적인 작업 진행이 불가능할 때.
3. **시간 초과**: 특정 시간 내에 작업이 완료되지 않았을 때.
4. **리소스 해제**: 더 이상 필요하지 않은 리소스를 해제하여 메모리를 절약해야 할 때.

# 8장. 동시성을 위한 설계

동시성 디자인 패턴을 잘 사용하면 자원 누수나 데드락 문제를 방지할 수 있다.

## 값 지연 패턴
deferred value 디자인 패턴은 비동기 계산 로직이 결과를 직접 반환하는 대신 결괏값을 가리키는 참조를 반환하도록 한다.

Futrue, Promise, Deferred가 모두 값 지연 패턴을 구현한다. 

## 장벽 패턴

barrier 디자인 패턴을 사용하면 잠시 프로그램을 멈추고 여러개의 동시성 작업이 완료되기를 기다릴 수 있다.

일반적으로 여러 곳에서 자료를 가져올 때(IO) 장벽 패턴을 사용한다. 

```kotlin
fun main() {
    runBlocking {
        println(measureTimeMillis {
            fetchFavoriteCharacterWrong("Inigo Montoya")
        })
        println(measureTimeMillis {
            fetchFavoriteCharacterCorrect("Inigo Montoya")
        })
 
      val (name, catchphrase, _) = fetchFavoriteCharacterCorrect("Inigo Montoya")
      
      println("$name says: $catchphrase")
      
      val characters: List<Deferred<FavoriteCharacter>> =
            listOf(
                Me.getFavoriteCharacter(),
                Taylor.getFavoriteCharacter(),
                Michael.getFavoriteCharacter()
            )

        println(characters.awaitAll())
    }
}
```

List<Deferred<>>를 사용해서 받아오고 한번에 awaitAll()을 호출한다. 

이처럼 같은 타입을 갖는 여러 비동기 작업을 사용하며 다음 단계로 넘어가기 전에 모 든 작업이 완료되기를 원한다면 awaitAll() 함수를 사용하라.

장벽 디자인 패턴은 여러 비동기 작업이 한 곳으로 모이도록 한다. 다음에 살펴볼 패턴 은 여러 작업의 실행을 추상화하는 것을 도와준다.

## 스케줄러 패턴

스케줄러 패턴의 목적은 실행의 대상(무엇)과 방법(어떻게)를 분리하고, 실행에 소요되는 자원 사용을 최적화하는것이다.

코틀린에서는 분배기(Dispatcher)가 스케줄러 디자인 패턴을 구현하고 있다.

코루틴(무엇)과 실행되는 스레드 풀(어떻게)를 분리한다. 

```kotlin
fun main() {
    runBlocking {

        // 부모 코루틴의 디스패처를 사용 (main)
        launch {
            // main을 출력
            println(Thread.currentThread().name)
        }
        launch(Dispatchers.Default) {
            // DefaultDispatcher-worker-1를 사용
            println(Thread.currentThread().name)
        }

        async(Dispatchers.IO) {
            for (i in 1..1000) {
                println(Thread.currentThread().name)
                yield()
            }
        }

        
    }
}
```

### 직접 Dispatcher 만들기

```kotlin
val myDispatcher = Executors
    .newFixedThreadPool(4)
    .asCoroutineDispatcher()

val forkJoinPool = ForkJoinPool(4).asCoroutineDispatcher()

repeat(1000) {
    launch(forkJoinPool) {
        println(Thread.currentThread().name)
    }
}
```

## 팬아웃 패턴
작업을 여러 동시성 프로세서로 배분하기 위한 패턴.

여러 워커들이 같은 채널을 구독하고, 일을 나눠서 처리한다

```kotlin
fun main() {
    runBlocking {
        val workChannel = generateWork()

        val workers = List(10) { id ->
            doWork(id, workChannel)
        }
    }
}

fun CoroutineScope.generateWork() = produce {
    for (i in 1..10_000) {
        send("page$i")
    }
    close()
}

private fun CoroutineScope.doWork(
    id: Int,
    channel: ReceiveChannel<String>
) = launch(Dispatchers.Default) {
    for (p in channel) {
        println("Worker $id processed $p")
    }
}
```

두 일꾼이 같은 메시지를 받는 일은 없다. 또한 전송 순서와 출력 순서가 일치하지 않 는다는 점도 유념하라. 팬아웃 패턴은 작업을 여러 코루틴, 스레드, CPU에게 효율적으 로 분배할 때 유용하게 사용할 수 있다.

## 팬인 패턴

여러 워커의 작업 결과를 하나로 모으기 위한 패턴이다.

팬아웃 패턴으로 결과를 생산한 뒤 하나로 모아야 한다면 팬인 패턴을 사용하면 된다.

여러 코루틴이 같은 채널을 읽는대신 여러 코루틴이 결과를 같은 채널로 보낸다.



## 경주 패턴

여러 작업이 동시에 실행한 뒤에 먼저 반환되는 승자의 결과만 사용하고 나머지 패자의 결과는 버리는 패턴이다.

예시로, 날씨 애플리케이션에서 날씨를 여러곳에서 받아올 시 둘중 하나만 먼저 사용할 수 있다.

```kotlin
fun main() {
    runBlocking {
        while (true) {
            val winner = select<Pair<String, String>> {
                preciseWeather().onReceive { preciseWeatherResult ->
                    preciseWeatherResult
                }
                weatherToday().onReceive { weatherTodayResult ->
                    weatherTodayResult
                }
            }
            println(winner)
            delay(1000)
        }
    }
}

fun CoroutineScope.preciseWeather() = produce {
    delay(Random.nextLong(100))
    send("Precise Weather" to "+25c")
}

fun CoroutineScope.weatherToday() = produce {
    delay(Random.nextLong(100))
    send("Weather Today" to "+24c")
}

```



select 구문을 사용할 때에는 순서가 중요하다. 

select는 편향적이기 때문에 두 이벤트가 동시에 발생하면 select는 첫번째 구문을 선택한다. 

selectUnbiased fmf 사용하면 선언 순서 관계없이 동시에 준비되면 임의로 하나를 택한다.

## 뮤텍스 패턴

뮤텍스는 여러 코루틴이 동시에 접근할 수 있는 공유 상태를 보호하기 위해 사용한다.

```kotlin
fun main() {
    runBlocking {
        var counter = 0
        val mutex = Mutex()
        val jobs = List(10) {
            async(Dispatchers.Default) {
                repeat(1000) {
                    mutex.withLock {
                        counter++
                    }
                }
            }
        }
        jobs.awaitAll()

        println(counter)
    }
}
```



## 사이드킥 채널 패턴

main 워커의 작업 일부를 조수 worker한테 넘길 수 있다.

## 질문

### 1. 코틀린의 select 식이 편향적이라는 것은 무슨 뜻인가?

코틀린의 `select` 식이 편향적이라는 것은, 여러 채널 또는 지연된 값 중에서 선택할 때 특정 채널이나 작업이 다른 것들보다 우선적으로 선택된다는 의미입니다. 이는 선택이 무작위로 이루어지지 않고, 고정된 순서에 따라 이루어지기 때문에 발생합니다.

### 2. 언제 채널 대신 뮤텍스를 사용해야 하는가?

채널과 뮤텍스는 각각 다른 동시성 문제를 해결하는 데 사용됩니다. 채널은 데이터의 생산자와 소비자 간의 통신을 관리하는 데 사용되고, 뮤텍스는 공유 자원의 동시 접근을 제어하는 데 사용됩니다.

#### 채널 대신 뮤텍스를 사용해야 하는 경우

1. **공유 상태 보호**: 여러 코루틴이 동일한 자원에 접근하고 수정해야 할 때, 자원 보호를 위해 뮤텍스를 사용합니다.
2. **상태 동기화**: 공유된 상태를 읽고 쓸 때 일관성을 유지하기 위해 뮤텍스를 사용합니다.
3. **작업 동기화**: 특정 코드 블록이 동시에 실행되지 않도록 보장해야 할 때 뮤텍스를 사용합니다.

### 3. 맵리듀스나 분할 정복 알고리듬을 효율적으로 구현하려면 어떤 동시성 디자인 패턴을 사용해야 하는가?

맵리듀스(MapReduce)와 분할 정복(Divide and Conquer) 알고리즘을 효율적으로 구현하려면 **워크 스틸링(Work Stealing)**과 **포크/조인(Fork/Join)** 패턴을 사용하는 것이 좋습니다.

#### 포크/조인(Fork/Join) 패턴

포크/조인 패턴은 작업을 재귀적으로 분할(fork)하고, 부분 작업을 병렬로 수행한 후, 결과를 합치는(join) 방식입니다. 이 패턴은 특히 분할 정복 알고리즘에서 유용합니다.

#### 예시: 포크/조인 패턴 (코틀린)

코틀린에서는 `async`와 `await`을 사용하여 포크/조인 패턴을 구현할 수 있습니다.

# 9장. 관용구와 안티 패턴

## 기술적 요구 사항
## 시야 지정 함수 사용하기
## 타입 검사와 캐스팅
## try-with-resources 문의 대안
## 인라인 함수
## 대수적 자료형 구현하기
## 제네릭에서 타입 실체화
## 상수 효율적으로 사용하기
## 생성자 오버로딩
## null 다루기
## 동시성을 명시적으로 나타내기
## 입력 유효성 검사하기
## 열거형 대신 봉인 클래스 사용하기
## 요약
## 질문

# 10장. Ktor를 이용한 동시성 마이크로서비스
## 기술적 요구 사항
## Ktor 시작하기
## 요청 라우팅
## 서비스 테스트하기
## 애플리케이션 모듈화
## 데이터베이스 연결하기
## 엔티티 생성하기
## 일관성 있는 테스트 만들기
## 엔티티 조회하기
## Ktor에서 라우팅 구조화하기
## Ktor의 동시성
## 요약
## 질문

# 11장. Vert.x를 이용한 반응형 마이크로서비스
## 기술적 요구 사항
## Vert.x 시작하기
## Vert.x에서 라우팅 구현하기
## 버티클
## 요청 처리하기
## Vert.x 애플리케이션 테스트하기
## 데이터베이스 다루기
## 이벤트 루프 이해하기
## 이벤트 버스와 통신하기
## 요약
## 질문

