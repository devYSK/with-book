package com.yscorp.orderservice.web

import com.yscorp.orderservice.config.SecurityConfig
import com.yscorp.orderservice.order.Order
import com.yscorp.orderservice.order.OrderService
import com.yscorp.orderservice.order.OrderStatus
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(OrderController::class)
@Import(SecurityConfig::class)
internal class OrderControllerWebFluxTests {
    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var orderService: OrderService

    @Test
    fun whenBookNotAvailableThenRejectOrder() {
        val orderRequest = OrderRequest("1234567890", 3)
        val expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn, orderRequest.quantity)
        BDDMockito.given(orderService.submitOrder(orderRequest.isbn, orderRequest.quantity))
            .willReturn(Mono.just(expectedOrder))

        webClient
            .mutateWith(
                SecurityMockServerConfigurers.mockJwt()
                    .authorities(SimpleGrantedAuthority("ROLE_customer"))
            )
            .post()
            .uri("/orders")
            .bodyValue(orderRequest)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(Order::class.java).value { actualOrder ->
                assertThat(actualOrder).isNotNull()
                assertThat(actualOrder.status).isEqualTo(OrderStatus.REJECTED)
            }
    }
}
