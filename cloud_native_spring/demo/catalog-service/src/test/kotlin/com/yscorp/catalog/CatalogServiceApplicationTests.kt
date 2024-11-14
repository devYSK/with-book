package com.yscorp.catalog

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.yscorp.catalog.domain.Book
import dasniko.testcontainers.keycloak.KeycloakContainer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
internal class CatalogServiceApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun whenGetRequestWithIdThenBookReturned() {
        val bookIsbn = "1231231230"
        val bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia")
        val expectedBook: Book = webTestClient
            .post()
            .uri("/books")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.isabelleTokens.accessToken
                )
            }
            .bodyValue(bookToCreate)
            .exchange()
            .expectStatus().isCreated()
            .expectBody<Book>(Book::class.java).value { book: Book ->
                Assertions.assertThat(book).isNotNull()
            }
            .returnResult().responseBody as Book

        webTestClient
            .get()
            .uri("/books/$bookIsbn")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(Book::class.java).value { actualBook: Book ->
                Assertions.assertThat(actualBook)
                    .isNotNull()
                Assertions.assertThat(actualBook.isbn).isEqualTo(expectedBook!!.isbn)
            }
    }

    @Test
    fun whenPostRequestThenBookCreated() {
        val expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia")

        webTestClient
            .post()
            .uri("/books")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.isabelleTokens.accessToken
                )
            }
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isCreated()
            .expectBody<Book>(Book::class.java).value { actualBook: Book ->
                Assertions.assertThat(actualBook)
                    .isNotNull()
                Assertions.assertThat(actualBook.isbn).isEqualTo(expectedBook.isbn)
            }
    }

    @Test
    fun whenPostRequestUnauthenticatedThen401() {
        val expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia")

        webTestClient
            .post()
            .uri("/books")
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isUnauthorized()
    }

    @Test
    fun whenPostRequestUnauthorizedThen403() {
        val expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia")

        webTestClient
            .post()
            .uri("/books")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.bjornTokens.accessToken
                )
            }
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isForbidden()
    }

    @Test
    fun whenPutRequestThenBookUpdated() {
        val bookIsbn = "1231231232"
        val bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia")
        val createdBook: Book? = webTestClient
            .post()
            .uri("/books")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.isabelleTokens.accessToken
                )
            }
            .bodyValue(bookToCreate)
            .exchange()
            .expectStatus().isCreated()
            .expectBody<Book>(Book::class.java).value { book: Book ->
                Assertions.assertThat(
                    book
                ).isNotNull()
            }
            .returnResult().responseBody as Book?
        val bookToUpdate = Book(
            createdBook!!.id, createdBook.isbn, createdBook.title, createdBook.author, 7.95,
            createdBook.publisher, createdBook.createdDate, createdBook.lastModifiedDate,
            createdBook.createdBy, createdBook.lastModifiedBy, createdBook.version
        )

        webTestClient
            .put()
            .uri("/books/$bookIsbn")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.isabelleTokens.accessToken
                )
            }
            .bodyValue(bookToUpdate)
            .exchange()
            .expectStatus().isOk()
            .expectBody<Book>(Book::class.java).value { actualBook: Book ->
                Assertions.assertThat(actualBook)
                    .isNotNull()
                Assertions.assertThat(actualBook.price).isEqualTo(bookToUpdate.price)
            }
    }

    @Test
    fun whenDeleteRequestThenBookDeleted() {
        val bookIsbn = "1231231233"
        val bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia")
        webTestClient
            .post()
            .uri("/books")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.isabelleTokens.accessToken
                )
            }
            .bodyValue(bookToCreate)
            .exchange()
            .expectStatus().isCreated()

        webTestClient
            .delete()
            .uri("/books/$bookIsbn")
            .headers { headers: HttpHeaders ->
                headers.setBearerAuth(
                    CatalogServiceApplicationTests.Companion.isabelleTokens.accessToken
                )
            }
            .exchange()
            .expectStatus().isNoContent()

        webTestClient
            .get()
            .uri("/books/$bookIsbn")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String::class.java).value { errorMessage: String? ->
                Assertions.assertThat(
                    errorMessage
                ).isEqualTo("The book with ISBN $bookIsbn was not found.")
            }
    }

    data class KeycloakToken @JsonCreator private constructor(
        @param:JsonProperty(
            "access_token"
        ) val accessToken: String
    )

    companion object {
        // Customer
        private  lateinit var bjornTokens: CatalogServiceApplicationTests.KeycloakToken

        // Customer and employee
        lateinit var isabelleTokens: CatalogServiceApplicationTests.KeycloakToken

        @Container
        private val keycloakContainer: KeycloakContainer = KeycloakContainer("quay.io/keycloak/keycloak:24.0")
            .withRealmImportFile("test-realm-config.json")

        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri"
            ) { CatalogServiceApplicationTests.Companion.keycloakContainer.authServerUrl + "/realms/PolarBookshop" }
        }

        @JvmStatic
        @BeforeAll
        fun generateAccessTokens(): Unit {
            val webClient = WebClient.builder()
                .baseUrl(keycloakContainer.authServerUrl + "/realms/PolarBookshop/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()

            isabelleTokens =
                authenticateWith("isabelle", "password", webClient)
            bjornTokens =
                authenticateWith("bjorn", "password", webClient)
        }

        private fun authenticateWith(
            username: String,
            password: String,
            webClient: WebClient
        ): CatalogServiceApplicationTests.KeycloakToken {
            return webClient
                .post()
                .body(
                    BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono<KeycloakToken>()
                .block()!!
        }
    }
}
