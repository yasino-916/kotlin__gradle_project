package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository

class DefaultLibraryService(
    private val repository: LibraryRepository
) : LibraryService {

    override fun addBook(book: Book): Boolean {
        return repository.addBook(book)
    }

    override fun borrowBook(patronId: String, isbn: String): BorrowResult {
        val book = repository.findBook(isbn) ?: return BorrowResult.BOOK_NOT_FOUND
        val patron = repository.findPatron(patronId) ?: return BorrowResult.PATRON_NOT_FOUND

        if (!book.isAvailable) return BorrowResult.NOT_AVAILABLE
        if (patron.borrowedBooks.size >= 3) return BorrowResult.LIMIT_REACHED

        val updatedBook = book.copy(isAvailable = false)
        val updatedPatron = patron.copy(borrowedBooks = patron.borrowedBooks + isbn)

        repository.updateBook(updatedBook)
        repository.updatePatron(updatedPatron)

        return BorrowResult.SUCCESS
    }

    override fun returnBook(patronId: String, isbn: String): Boolean {
        val book = repository.findBook(isbn) ?: return false
        val patron = repository.findPatron(patronId) ?: return false

        if (isbn !in patron.borrowedBooks) return false

        val updatedBook = book.copy(isAvailable = true)
        val updatedPatron = patron.copy(borrowedBooks = patron.borrowedBooks - isbn)

        repository.updateBook(updatedBook)
        repository.updatePatron(updatedPatron)

        return true
    }

    override fun search(query: String): List<Book> {
        return repository.getAllBooks().filter {
            it.title.contains(query, ignoreCase = true) || 
            it.author.contains(query, ignoreCase = true)
        }
    }

    override fun findBook(isbn: String): Book? = repository.findBook(isbn)

    override fun getAllPatrons(): List<Patron> = repository.getAllPatrons()
}