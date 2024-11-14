package com.yscorp.orderservice.web

import com.yscorp.orderservice.order.Order
import com.yscorp.orderservice.order.OrderService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("orders")
class OrderController(orderService: OrderService) {
    private val orderService: OrderService = orderService

    @GetMapping
    fun getAllOrders(@AuthenticationPrincipal jwt: Jwt): Flux<Order> {
        log.info("Fetching all orders")
        return orderService.getAllOrders(jwt.subject)
    }

    @PostMapping
    fun submitOrder(@RequestBody @Valid orderRequest: OrderRequest): Mono<Order> {
        log.info("Order for {} copies of the book with ISBN {}", orderRequest.quantity, orderRequest.isbn)
        return orderService.submitOrder(orderRequest.isbn, orderRequest.quantity)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderController::class.java)
    }
}
