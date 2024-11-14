package com.yscorp.catalog.web

import com.yscorp.catalog.domain.Book
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import java.time.Instant

@JsonTest
internal class BookJsonTests {

    @Autowired
    lateinit var json: JacksonTester<Book>

    @Test
    @Throws(Exception::class)
    fun testSerialize() {
        val now = Instant.now()
        val book = Book(394L, "1234567890", "Title", "Author", 9.90, "Polarsophia", now, now, "jenny", "eline", 21)
        val jsonContent = json!!.write(book)

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
            .isEqualTo(book.id!!.toInt())
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
            .isEqualTo(book.isbn)
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.title")
            .isEqualTo(book.title)
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.author")
            .isEqualTo(book.author)
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
            .isEqualTo(book.price)
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.publisher")
            .isEqualTo(book.publisher)
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
            .isEqualTo(book.createdDate.toString())
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
            .isEqualTo(book.lastModifiedDate.toString())
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.createdBy")
            .isEqualTo(book.createdBy)
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedBy")
            .isEqualTo(book.lastModifiedBy)
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
            .isEqualTo(book.version)
    }

    @Test
    @Throws(Exception::class)
    fun testDeserialize() {
        val instant = Instant.parse("2021-09-07T22:50:37.135029Z")
        val content = """
                {
                    "id": 394,
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "price": 9.90,
                    "publisher": "Polarsophia",
                    "createdDate": "2021-09-07T22:50:37.135029Z",
                    "lastModifiedDate": "2021-09-07T22:50:37.135029Z",
                    "createdBy": "jenny",
                    "lastModifiedBy": "eline",
                    "version": 21
                }
                
                """.trimIndent()
        Assertions.assertThat(
            json!!.parse(content)
        )
            .usingRecursiveComparison()
            .isEqualTo(
                Book(
                    394L,
                    "1234567890",
                    "Title",
                    "Author",
                    9.90,
                    "Polarsophia",
                    instant,
                    instant,
                    "jenny",
                    "eline",
                    21
                )
            )
    }
}
