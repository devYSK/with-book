package com.yscorp.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import org.springframework.messaging.support.GenericMessage
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.function.Function
import java.util.function.Predicate


@FunctionalSpringBootTest
//@Disabled("이러한 테스트는 함수를 단독으로 사용할 때만 필요합니다(바인딩 없음).")
internal class DispatchingFunctionsIntegrationTests {

    @Autowired
    lateinit var catalog: FunctionCatalog

    @Test
    fun packOrder() {
        val pack = catalog!!.lookup<Function<OrderAcceptedMessage, Long>>(
            Function::class.java, "pack"
        )
        val orderId: Long = 121
        Assertions.assertThat(pack.apply(OrderAcceptedMessage(orderId))).isEqualTo(orderId)
    }

    @Test
    fun labelOrder() {
        val label = catalog!!.lookup<Function<Flux<Long>, Flux<OrderDispatchedMessage>>>(
            Function::class.java, "label"
        )
        val orderId = Flux.just(121L)

        StepVerifier.create(label.apply(orderId))
            .expectNextMatches { dispatchedOrder ->
                dispatchedOrder == OrderDispatchedMessage(
                    121L
                )
            }
            .verifyComplete()
    }

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun packAndLabelOrder() {
        val packAndLabel =
            catalog!!.lookup<Function<OrderAcceptedMessage, Flux<GenericMessage<ByteArray>>>>(
                Function::class.java,
                "pack|label"
            )
        val orderId: Long = 121

        StepVerifier.create(packAndLabel.apply(OrderAcceptedMessage(orderId)))
            .expectNextMatches { genericMessage ->
                // `payload`를 `ByteArray`로 받아서 Jackson을 사용해 역직렬화
                val payloadBytes = genericMessage.payload

                println(String(payloadBytes))
                val dispatchedOrder = objectMapper.readValue(String(payloadBytes), OrderDispatchedMessage::class.java)

                println(dispatchedOrder)

                // 메시지가 올바르게 변환되었는지 확인
                dispatchedOrder.orderId == orderId
            }
            .verifyComplete()
    }
}