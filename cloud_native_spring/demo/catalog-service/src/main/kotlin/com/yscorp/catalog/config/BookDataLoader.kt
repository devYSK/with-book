package com.yscorp.catalog.config

import com.yscorp.catalog.domain.Book
import com.yscorp.catalog.domain.BookRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
@Profile("testdata")
class BookDataLoader(private val bookRepository: BookRepository) {
    @EventListener(ApplicationReadyEvent::class)
    fun loadBookTestData() {
        bookRepository.deleteAll()
        val book1 = Book.of("1234567891", "Northern Lights", "Lyra Silverstar", 9.90, "Polarsophia")
        val book2 = Book.of("1234567892", "Polar Journey", "Iorek Polarson", 12.90, "Polarsophia")
        bookRepository.saveAll(listOf(book1, book2))
    }

}
