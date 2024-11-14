package com.yscorp.orderservice.web

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class OrderRequest(

    @field:NotBlank(message = "The book ISBN must be defined.")
    val isbn: String,

    @field:Max(value = 5, message = "You cannot order more than 5 items.") @field:Min(
        value = 1,
        message = "You must order at least 1 item."
    ) @field:NotNull(message = "The book quantity must be defined.")
    val quantity: Int

)
