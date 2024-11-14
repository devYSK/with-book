package com.yscorp.orderservice.book

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.io.IOException
import java.util.function.Predicate

@TestMethodOrder(MethodOrderer.Random::class)
internal class BookClientTests {

    lateinit var mockWebServer: MockWebServer
    lateinit var bookClient: BookClient

    @BeforeEach
    @Throws(IOException::class)
    fun setup() {
        this.mockWebServer = MockWebServer()
        mockWebServer!!.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer!!.url("/").toUri().toString())
            .build()
        this.bookClient = BookClient(webClient)
    }

    @AfterEach
    @Throws(IOException::class)
    fun clean() {
        mockWebServer!!.shutdown()
    }

    @Test
    fun whenBookExistsThenReturnBook() {
        val bookIsbn = "1234567890"

        val mockResponse = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(
                """
							{
								"isbn": %s,
								"title": "Title",
								"author": "Author",
								"price": 9.90,
								"publisher": "Polarsophia"
							}
						
						""".trimIndent().formatted(bookIsbn)
            )

        mockWebServer!!.enqueue(mockResponse)

        val book = bookClient!!.getBookByIsbn(bookIsbn)

        StepVerifier.create(book)
            .expectNextMatches({ b -> b.isbn.equals(bookIsbn) })
            .verifyComplete()
    }

    @Test
    fun whenBookNotExistsThenReturnEmpty() {
        val bookIsbn = "1234567891"

        val mockResponse = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(404)

        mockWebServer!!.enqueue(mockResponse)

        StepVerifier.create(bookClient!!.getBookByIsbn(bookIsbn))
            .expectNextCount(0)
            .verifyComplete()
    }
}