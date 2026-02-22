package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService

import org.dbu.library.util.display

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}

private fun addBook(service: LibraryService) {
    print("Enter ISBN: ")
    val isbn = readln()
    print("Enter Title: ")
    val title = readln()
    print("Enter Author: ")
    val author = readln()
    print("Enter Year: ")
    val year = readln().toIntOrNull() ?: 0

    val book = Book(isbn, title, author, year)
    if (service.addBook(book)) {
        println("✅ Book added successfully")
    } else {
        println("❌ Book with this ISBN already exists")
    }
}

private fun registerPatron(repository: LibraryRepository) {
    print("Enter Patron ID: ")
    val id = readln()
    print("Enter Name: ")
    val name = readln()

    val patron = Patron(id, name)
    if (repository.addPatron(patron)) {
        println("✅ Patron registered successfully")
    } else {
        println("❌ Patron with this ID already exists")
    }
}

private fun borrowBook(service: LibraryService) {
    print("Enter Patron ID: ")
    val patronId = readln()
    print("Enter ISBN: ")
    val isbn = readln()

    val result = service.borrowBook(patronId, isbn)
    when (result) {
        BorrowResult.SUCCESS -> println("✅ Book borrowed successfully")
        BorrowResult.BOOK_NOT_FOUND -> println("❌ Error: Book not found")
        BorrowResult.PATRON_NOT_FOUND -> println("❌ Error: Patron not found")
        BorrowResult.NOT_AVAILABLE -> println("❌ Error: Book is already borrowed")
        BorrowResult.LIMIT_REACHED -> println("❌ Error: Patron has reached borrowing limit (max 3)")
    }
}

private fun returnBook(service: LibraryService) {
    print("Enter Patron ID: ")
    val patronId = readln()
    print("Enter ISBN: ")
    val isbn = readln()

    if (service.returnBook(patronId, isbn)) {
        println("✅ Book returned successfully")
    } else {
        println("❌ Error: Failed to return book. Check ID and ISBN.")
    }
}

private fun search(service: LibraryService) {
    print("Enter title or author to search: ")
    val query = readln()
    val results = service.search(query)

    if (results.isEmpty()) {
        println("No books found matching '$query'")
    } else {
        println("\nFound ${results.size} books:")
        results.forEach { println(" - ${it.display()} [${if (it.isAvailable) "Available" else "Borrowed"}]") }
    }
}

private fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    if (books.isEmpty()) {
        println("The library is currently empty.")
    } else {
        println("\n--- Library Collection ---")
        books.forEach { book ->
            val status = if (book.isAvailable) "Available" else "Borrowed"
            println("[$status] ${book.display()}")
        }
    }
}
