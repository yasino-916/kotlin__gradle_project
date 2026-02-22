package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.model.Patron

interface LibraryService {

    fun addBook(book: Book): Boolean

    fun borrowBook(patronId: String, isbn: String): BorrowResult

    fun returnBook(patronId: String, isbn: String): Boolean

    fun search(query: String): List<Book>
    fun findBook(isbn: String): Book?
    fun getAllPatrons(): List<Patron>
}