package com.yscorp.orderservice.order.event

@JvmRecord
data class OrderAcceptedMessage(
    val orderId: Long
)
