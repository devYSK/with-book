package com.yscorp.catalog.domain

import com.yscorp.catalog.config.DataConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Collectors
import java.util.stream.StreamSupport


@DataJdbcTest
@Import(DataConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
internal class BookRepositoryJdbcTests {
    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var jdbcAggregateTemplate: JdbcAggregateTemplate

    @Test
    fun findAllBooks() {
        val book1 = Book.of("1234561235", "Title", "Author", 12.90, "Polarsophia")
        val book2 = Book.of("1234561236", "Another Title", "Author", 12.90, "Polarsophia")
        jdbcAggregateTemplate!!.insert(book1)
        jdbcAggregateTemplate.insert(book2)

        val actualBooks = bookRepository!!.findAll()

        Assertions.assertThat(StreamSupport.stream(actualBooks.spliterator(), true)
            .filter { book: Book -> book.isbn == book1.isbn || book.isbn == book2.isbn }
            .collect(Collectors.toList())).hasSize(2)
    }

    @Test
    fun findBookByIsbnWhenExisting() {
        val bookIsbn = "1234561237"
        val book = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia")
        jdbcAggregateTemplate!!.insert(book)

        val actualBook = bookRepository!!.findByIsbn(bookIsbn)

        Assertions.assertThat(actualBook).isPresent()
        Assertions.assertThat(actualBook.get().isbn).isEqualTo(book.isbn)
    }

    @Test
    fun findBookByIsbnWhenNotExisting() {
        val actualBook = bookRepository!!.findByIsbn("1234561238")
        Assertions.assertThat(actualBook).isEmpty()
    }

    @Test
    fun existsByIsbnWhenExisting() {
        val bookIsbn = "1234561239"
        val bookToCreate = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia")
        jdbcAggregateTemplate!!.insert(bookToCreate)

        val existing = bookRepository!!.existsByIsbn(bookIsbn)

        Assertions.assertThat(existing).isTrue()
    }

    @Test
    fun existsByIsbnWhenNotExisting() {
        val existing = bookRepository!!.existsByIsbn("1234561240")
        Assertions.assertThat(existing).isFalse()
    }

    @Test
    fun whenCreateBookNotAuthenticatedThenNoAuditMetadata() {
        val bookToCreate = Book.of("1232343456", "Title", "Author", 12.90, "Polarsophia")
        val createdBook = bookRepository!!.save(bookToCreate)

        Assertions.assertThat(createdBook.createdBy).isNull()
        Assertions.assertThat(createdBook.lastModifiedBy).isNull()
    }

    @Test
    @WithMockUser("john")
    fun whenCreateBookAuthenticatedThenAuditMetadata() {
        val bookToCreate = Book.of("1232343457", "Title", "Author", 12.90, "Polarsophia")
        val createdBook = bookRepository!!.save(bookToCreate)

        Assertions.assertThat(createdBook.createdBy).isEqualTo("john")
        Assertions.assertThat(createdBook.lastModifiedBy).isEqualTo("john")
    }

    @Test
    fun deleteByIsbn() {
        val bookIsbn = "1234561241"
        val bookToCreate = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia")
        val persistedBook = jdbcAggregateTemplate!!.insert(bookToCreate)

        bookRepository!!.deleteByIsbn(bookIsbn)

        Assertions.assertThat(
            jdbcAggregateTemplate.findById(
                persistedBook.id!!,
                Book::class.java
            )
        ).isNull()
    }
}