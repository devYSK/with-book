package com.yscorp.catalog.domain

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface BookRepository : CrudRepository<Book, Long> {

    fun findByIsbn(isbn: String): Optional<Book>

    fun existsByIsbn(isbn: String): Boolean

    @Modifying
    @Transactional
    @Query("delete from Book where isbn = :isbn")
    fun deleteByIsbn(isbn: String)
}