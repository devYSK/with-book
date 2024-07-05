

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

## 기술적 요구 사항

## 데코레이터 패턴
## 어댑터 패턴
## 브리지 패턴
## 합성 패턴
## 퍼사드 패턴
## 경량 패턴
## 프록시 패턴
## 요약
## 질문

# 4장. 동작 패턴과 친해지기
## 기술적 요구 사항
## 전략 패턴
## 반복자 패턴
## 상태 패턴
## 명령 패턴
## 책임 사슬 패턴
## 해석기 패턴
## 중개인 패턴
## 기억 패턴
## 방문자 패턴
## 템플릿 메서드 패턴
## 관찰자 패턴
## 요약
## 질문

# 5장. 함수형 프로그래밍 소개
## 기술적 요구 사항
## 함수형 프로그래밍을 사용하는 이유
## 불변성
## 값으로서의 함수
## it 표기법
## 문 대신 식 사용하기
## 재귀 호출
## 요약
## 질문

# 6장. 스레드와 코루틴
## 기술적 요구 사항
## 스레드 심화
## 코루틴 소개
## 요약
## 질문

# 7장. 데이터 흐름 제어
## 기술적 요구 사항
## 반응형 프로그래밍의 원칙
## 집합 자료 구조를 위한 고차 함수
## 동시성 자료 구조 소개
## 요약
## 질문

# 8장. 동시성을 위한 설계
## 기술적 요구 사항
## 값 지연 패턴
## 장벽 패턴
## 스케줄러 패턴
## 파이프라인 패턴
## 팬아웃 패턴
## 팬인 패턴
## 경주 패턴
## 뮤텍스 패턴
## 사이드킥 채널 패턴
## 요약
## 질문

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

