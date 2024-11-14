package com.yscorp.orderservice.order

import com.yscorp.orderservice.book.Book
import com.yscorp.orderservice.book.BookClient
import com.yscorp.orderservice.order.event.OrderAcceptedMessage
import com.yscorp.orderservice.order.event.OrderDispatchedMessage
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class OrderService(
    private val bookClient: BookClient,
    private val streamBridge: StreamBridge,
    private val orderRepository: OrderRepository
) {

    @Transactional
    fun getAllOrders(userId: String): Flux<Order> {
        return orderRepository.findAllByCreatedBy(userId)
    }

    @Transactional
    fun submitOrder(isbn: String, quantity: Int): Mono<Order> {
        return bookClient.getBookByIsbn(isbn)
            .map { book -> buildAcceptedOrder(book, quantity) }
            .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
            .flatMap { orderRepository.save(it) }
            .doOnNext { publishOrderAcceptedEvent(it) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)

        fun buildAcceptedOrder(book: Book, quantity: Int): Order {
            return Order.of(
                book.isbn,
                "${book.title} - ${book.author}",
                book.price,
                quantity,
                OrderStatus.ACCEPTED
            )
        }

        fun buildRejectedOrder(bookIsbn: String, quantity: Int): Order {
            return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED)
        }
    }

    private fun publishOrderAcceptedEvent(order: Order) {
        if (order.status != OrderStatus.ACCEPTED) {
            return
        }
        val orderAcceptedMessage = OrderAcceptedMessage(order.id!!)
        log.info("Sending order accepted event with id: {}", order.id)
        val result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage)
        log.info("Result of sending data for order with id {}: {}", order.id, result)
    }

    fun consumeOrderDispatchedEvent(flux: Flux<OrderDispatchedMessage>): Flux<Order> {
        return flux
            .flatMap { message -> orderRepository.findById(message.orderId) }
            .map { buildDispatchedOrder(it) }
            .flatMap { orderRepository.save(it) }
    }

    private fun buildDispatchedOrder(existingOrder: Order): Order {
        return Order(
            existingOrder.id,
            existingOrder.bookIsbn,
            existingOrder.bookName,
            existingOrder.bookPrice,
            existingOrder.quantity,
            OrderStatus.DISPATCHED,
            existingOrder.createdDate,
            existingOrder.lastModifiedDate,
            existingOrder.createdBy,
            existingOrder.lastModifiedBy,
            existingOrder.version
        )
    }
}