package com.yscorp.catalog.domain

import jakarta.validation.constraints.*
import org.springframework.data.annotation.*
import java.time.Instant


data class Book(

    @field:Id val id: Long?,

    @field:Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
    @field:NotBlank(message = "The book ISBN must be defined.")
    val isbn: String,

    @field:NotBlank(message = "The book title must be defined.")
    val title: String,

    @field:NotBlank(message = "The book author must be defined.")
    val author: String,

    @field:Positive(message = "The book price must be greater than zero.")
    @field:NotNull(message = "The book price must be defined.")
    @param:Positive(message = "The book price must be greater than zero.")
    val price: Double?,
    val publisher: String?,

    @field:CreatedDate
    val createdDate: Instant?,

    @field:LastModifiedDate
    val lastModifiedDate: Instant?,

    @field:CreatedBy
    val createdBy: String?,

    @field:LastModifiedBy
    val lastModifiedBy: String?,

    @field:Version
    val version: Int

) {
    companion object {
        fun of(isbn: String, title: String, author: String, price: Double?, publisher: String?): Book {
            return Book(null, isbn, title, author, price, publisher, null, null, null, null, 0)
        }
    }
}
