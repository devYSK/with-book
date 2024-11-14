package com.yscorp.dispatcherservice

data class OrderDispatchedMessage(
    val orderId: Long
) {
    constructor() : this(0)
}
