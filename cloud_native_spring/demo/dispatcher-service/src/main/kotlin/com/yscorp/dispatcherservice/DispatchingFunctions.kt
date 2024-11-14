package com.yscorp.dispatcherservice

import reactor.core.publisher.Flux
import java.util.function.Function

@org.springframework.context.annotation.Configuration
class DispatchingFunctions {

    @org.springframework.context.annotation.Bean
    fun pack(): Function<OrderAcceptedMessage, Long> {

        return Function<OrderAcceptedMessage, Long> { orderAcceptedMessage: OrderAcceptedMessage ->
            log.info("The order with id {} is packed.", orderAcceptedMessage.orderId)
            orderAcceptedMessage.orderId
        }
    }

    @org.springframework.context.annotation.Bean
    fun label(): Function<Flux<Long>, Flux<OrderDispatchedMessage>> {
        return Function<Flux<Long>, Flux<OrderDispatchedMessage>> { orderFlux: Flux<Long> ->
            orderFlux.map { orderId: Long ->
                log.info("The order with id {} is labeled.", orderId)
                OrderDispatchedMessage(orderId)
            }
        }
    }

    companion object {
        private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(DispatchingFunctions::class.java)
    }
}
