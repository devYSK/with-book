package com.yscorp.orderservice.order

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("orders")
class Order(
    @field:Id
    val id: Long?,

    val bookIsbn: String,
    val bookName: String?,
    val bookPrice: Double?,
    val quantity: Int,
    status: OrderStatus,

    @CreatedDate
    val createdDate: Instant?,

    @LastModifiedDate
    val lastModifiedDate: Instant?,

    @CreatedBy
    val createdBy: String?,

    @LastModifiedBy
    val lastModifiedBy: String?,

    @Version
    val version: Int
) {
    val status: OrderStatus = status

    companion object {
        fun of(bookIsbn: String, bookName: String?, bookPrice: Double?, quantity: Int, status: OrderStatus): Order {
            return Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, null, null, 0)
        }
    }
}
