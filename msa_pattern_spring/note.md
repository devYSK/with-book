# 실용적인 마이크로서비스 아키텍처 패턴

* https://github.com/Apress/practical-microservices-architectural-patterns





## 메시지 기반 마이크로 서비스

마이크로서비스간 동기식 호출을 사용해서는 안된다는 강제적인 규칙은 없다.

REST같은 SOA 인터페이스를 사용하는데 SOA 조차 마이크로서비스끼리 의존하는 관계가 생기고 트랜잭셩니 실패할 수 있다.

비동기식 혹은 전송 후 무시 (fire-and-forget)방식의 통신은 서로간 의존성에서 분리할 수 있다.

