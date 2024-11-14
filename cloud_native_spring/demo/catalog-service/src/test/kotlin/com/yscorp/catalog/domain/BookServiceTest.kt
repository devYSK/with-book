package com.yscorp.catalog.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class BookServiceTest {

    @Mock
    lateinit var bookRepository: BookRepository

    @InjectMocks
    lateinit var bookService: BookService

    @Test
    fun whenBookToCreateAlreadyExistsThenThrows() {
        val bookIsbn = "1234561232"
        val bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia")
        Mockito.`when`(bookRepository!!.existsByIsbn(bookIsbn)).thenReturn(true)
        Assertions.assertThatThrownBy {
            bookService!!.addBookToCatalog(
                bookToCreate
            )
        }
            .isInstanceOf(BookAlreadyExistsException::class.java)
            .hasMessage("A book with ISBN $bookIsbn already exists.")
    }

    @Test
    fun whenBookToReadDoesNotExistThenThrows() {
        val bookIsbn = "1234561232"
        Mockito.`when`(bookRepository!!.findByIsbn(bookIsbn)).thenReturn(Optional.empty())
        Assertions.assertThatThrownBy {
            bookService!!.viewBookDetails(
                bookIsbn
            )
        }
            .isInstanceOf(BookNotFoundException::class.java)
            .hasMessage("The book with ISBN $bookIsbn was not found.")
    }
}
