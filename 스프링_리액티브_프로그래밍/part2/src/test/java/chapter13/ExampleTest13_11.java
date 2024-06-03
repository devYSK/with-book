package chapter13;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * StepVerifier Backpressure 테스트 예제
 */
public class ExampleTest13_11 {
    @Test
    public void generateNumberTest() {
        StepVerifier
            .create(BackpressureTestExample.generateNumber(), 1L) // 초기 요청량을 1로 설정
            .thenRequest(99) // 추가로 99개의 아이템을 요청하여 총 100개를 요청
            .expectNextCount(100) // 100개의 아이템이 올 것을 기대
            .verifyComplete(); // onComplete 신호가 올 것을 기대
    }
}
