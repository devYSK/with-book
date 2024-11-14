package com.yscorp.orderservice.order.event

import com.yscorp.orderservice.order.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Consumer

@Configuration
class OrderFunctions {

    @Bean
    fun dispatchOrder(orderService: OrderService): Consumer<Flux<OrderDispatchedMessage>> {
        return Consumer { flux: Flux<OrderDispatchedMessage> ->
            orderService.consumeOrderDispatchedEvent(flux)
                .doOnNext { order ->
                    log.info(
                        "The order with id {} is dispatched",
                        order.id
                    )
                }
                .subscribe()
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(OrderFunctions::class.java)
    }
}
