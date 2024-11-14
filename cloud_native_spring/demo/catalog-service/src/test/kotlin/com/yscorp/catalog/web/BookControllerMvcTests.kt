package com.yscorp.catalog.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.yscorp.catalog.config.SecurityConfig
import com.yscorp.catalog.domain.Book
import com.yscorp.catalog.domain.BookNotFoundException
import com.yscorp.catalog.domain.BookService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(BookController::class)
@Import(
    SecurityConfig::class
)
internal class BookControllerMvcTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var bookService: BookService

    @Test
    @Throws(Exception::class)
    fun whenGetBookExistingAndAuthenticatedThenShouldReturn200() {
        val isbn = "7373731394"
        val expectedBook = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        BDDMockito.given(bookService!!.viewBookDetails(isbn)).willReturn(expectedBook)
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/books/$isbn")
                    .with(SecurityMockMvcRequestPostProcessors.jwt())
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @Throws(Exception::class)
    fun whenGetBookExistingAndNotAuthenticatedThenShouldReturn200() {
        val isbn = "7373731394"
        val expectedBook = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        BDDMockito.given(bookService!!.viewBookDetails(isbn)).willReturn(expectedBook)
        mockMvc
            .perform(MockMvcRequestBuilders.get("/books/$isbn"))
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @Throws(Exception::class)
    fun whenGetBookNotExistingAndAuthenticatedThenShouldReturn404() {
        val isbn = "7373731394"
        BDDMockito.given(bookService!!.viewBookDetails(isbn)).willThrow(BookNotFoundException::class.java)
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/books/$isbn")
                    .with(SecurityMockMvcRequestPostProcessors.jwt())
            )
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @Throws(Exception::class)
    fun whenGetBookNotExistingAndNotAuthenticatedThenShouldReturn404() {
        val isbn = "7373731394"
        BDDMockito.given(bookService!!.viewBookDetails(isbn)).willThrow(BookNotFoundException::class.java)
        mockMvc
            .perform(MockMvcRequestBuilders.get("/books/$isbn"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @Throws(Exception::class)
    fun whenDeleteBookWithEmployeeRoleThenShouldReturn204() {
        val isbn = "7373731394"
        mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/books/$isbn")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority(ROLE_EMPLOYEE)))
            )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    @Throws(Exception::class)
    fun whenDeleteBookWithCustomerRoleThenShouldReturn403() {
        val isbn = "7373731394"
        mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/books/$isbn")
                    .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority(ROLE_CUSTOMER)))
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    @Test
    @Throws(Exception::class)
    fun whenDeleteBookNotAuthenticatedThenShouldReturn401() {
        val isbn = "7373731394"
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/books/$isbn"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @Throws(Exception::class)
    fun whenPostBookWithEmployeeRoleThenShouldReturn201() {
        val isbn = "7373731394"
        val bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        BDDMockito.given(bookService!!.addBookToCatalog(bookToCreate)).willReturn(bookToCreate)
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper!!.writeValueAsString(bookToCreate))
                    .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority(ROLE_EMPLOYEE)))
            )
            .andExpect(MockMvcResultMatchers.status().isCreated())
    }

    @Test
    @Throws(Exception::class)
    fun whenPostBookWithCustomerRoleThenShouldReturn403() {
        val isbn = "7373731394"
        val bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        BDDMockito.given(bookService!!.addBookToCatalog(bookToCreate)).willReturn(bookToCreate)
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper!!.writeValueAsString(bookToCreate))
                    .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority(ROLE_CUSTOMER)))
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    @Test
    @Throws(Exception::class)
    fun whenPostBookAndNotAuthenticatedThenShouldReturn403() {
        val isbn = "7373731394"
        val bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper!!.writeValueAsString(bookToCreate))
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @Throws(Exception::class)
    fun whenPutBookWithEmployeeRoleThenShouldReturn200() {
        val isbn = "7373731394"
        val bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        BDDMockito.given(bookService!!.addBookToCatalog(bookToCreate)).willReturn(bookToCreate)
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/books/$isbn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper!!.writeValueAsString(bookToCreate))
                    .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority(ROLE_EMPLOYEE)))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @Throws(Exception::class)
    fun whenPutBookWithCustomerRoleThenShouldReturn403() {
        val isbn = "7373731394"
        val bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        BDDMockito.given(bookService!!.addBookToCatalog(bookToCreate)).willReturn(bookToCreate)
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/books/$isbn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper!!.writeValueAsString(bookToCreate))
                    .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(SimpleGrantedAuthority(ROLE_CUSTOMER)))
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    @Test
    @Throws(Exception::class)
    fun whenPutBookAndNotAuthenticatedThenShouldReturn401() {
        val isbn = "7373731394"
        val bookToCreate = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia")
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/books/$isbn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper!!.writeValueAsString(bookToCreate))
            )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    companion object {
        private const val ROLE_EMPLOYEE = "ROLE_employee"
        private const val ROLE_CUSTOMER = "ROLE_customer"
    }
}
