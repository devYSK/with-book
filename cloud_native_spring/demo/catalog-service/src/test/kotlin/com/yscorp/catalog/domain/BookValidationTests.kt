package com.yscorp.catalog.domain

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat  // 이 부분만 남겨둡니다
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.stream.Collectors


internal class BookValidationTests {


    companion object {
        lateinit var validator: Validator

        @JvmStatic
        @BeforeAll
        fun setUp(): Unit {
            val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
            validator = factory.validator
        }

    }

    @Test
    fun whenAllFieldsCorrectThenValidationSucceeds() {
        val book = Book.of("1234567890", "Title", "Author", 9.90, "Polarsophia")
        val violations= validator.validate(book)


        assertThat(violations).isEmpty()
    }

    @Test
    fun whenIsbnNotDefinedThenValidationFails() {
        val book = Book.of("", "Title", "Author", 9.90, "Polarsophia")
        val violations = validator.validate(book)
        assertThat(violations).hasSize(2)
        Assertions.assertThat(violations.map { it.message })
            .contains("The book ISBN must be defined.")
            .contains("The ISBN format must be valid.")
    }

    @Test
    fun whenIsbnDefinedButIncorrectThenValidationFails() {
        val book = Book.of("a234567890", "Title", "Author", 9.90, "Polarsophia")
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The ISBN format must be valid.")
    }

    @Test
    fun whenTitleIsNotDefinedThenValidationFails() {
        val book = Book.of("1234567890", "", "Author", 9.90, "Polarsophia")
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book title must be defined.")
    }

    @Test
    fun whenAuthorIsNotDefinedThenValidationFails() {
        val book = Book.of("1234567890", "Title", "", 9.90, "Polarsophia")
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book author must be defined.")
    }

    @Test
    fun whenPriceIsNotDefinedThenValidationFails() {
        val book = Book.of("1234567890", "Title", "Author", null, "Polarsophia")
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book price must be defined.")
    }

    @Test
    fun whenPriceDefinedButZeroThenValidationFails() {
        val book = Book.of("1234567890", "Title", "Author", 0.0, "Polarsophia")
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book price must be greater than zero.")
    }

    @Test
    fun whenPriceDefinedButNegativeThenValidationFails() {
        val book = Book.of("1234567890", "Title", "Author", -9.90, "Polarsophia")
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).hasSize(1)
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book price must be greater than zero.")
    }

    @Test
    fun whenPublisherIsNotDefinedThenValidationSucceeds() {
        val book = Book.of("1234567890", "Title", "Author", 9.90, null)
        val violations: Set<ConstraintViolation<Book>> = validator.validate(book)
        Assertions.assertThat(violations).isEmpty()
    }

}
