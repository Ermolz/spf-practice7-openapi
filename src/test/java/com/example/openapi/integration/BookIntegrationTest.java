package com.example.openapi.integration;

import com.example.openapi.entity.Book;
import com.example.openapi.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookIntegrationTest {

    @Autowired
    private BookService bookService;

    @Test
    void testGetAllBooks() {
        // Act
        List<Book> books = bookService.getAllBooks();

        // Assert
        assertNotNull(books);
        assertTrue(books.size() >= 3); // Initial data has 3 books
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("The Lord of the Rings")));
    }

    @Test
    void testFindBookById() {
        // Act
        Book book = bookService.findBookById(1L);

        // Assert
        assertNotNull(book);
        assertEquals(1L, book.getId());
        assertEquals("The Lord of the Rings", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
    }

    @Test
    void testFindBookByIdNotFound() {
        // Act
        Book book = bookService.findBookById(999L);

        // Assert
        assertNull(book);
    }

    @Test
    void testCreateBook() {
        // Arrange
        Book newBook = new Book(null, "Integration Test Book", "Integration Author");

        // Act
        Book created = bookService.createBook(newBook);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Integration Test Book", created.getTitle());
        assertEquals("Integration Author", created.getAuthor());

        // Verify it's in the store
        Book found = bookService.findBookById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void testUpdateBook() {
        // Arrange
        Book book = bookService.createBook(new Book(null, "Original Title", "Original Author"));
        Long bookId = book.getId();
        Book updatedBook = new Book(null, "Updated Title", "Updated Author");

        // Act
        Book result = bookService.updateBook(bookId, updatedBook);

        // Assert
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());

        // Verify the update persisted
        Book found = bookService.findBookById(bookId);
        assertNotNull(found);
        assertEquals("Updated Title", found.getTitle());
    }

    @Test
    void testUpdateBookNotFound() {
        // Arrange
        Book updatedBook = new Book(null, "Updated Title", "Updated Author");

        // Act
        Book result = bookService.updateBook(999L, updatedBook);

        // Assert
        assertNull(result);
    }

    @Test
    void testDeleteBook() {
        // Arrange
        Book book = bookService.createBook(new Book(null, "To Delete", "Author"));
        Long bookId = book.getId();

        // Verify it exists
        assertNotNull(bookService.findBookById(bookId));

        // Act
        bookService.deleteBook(bookId);

        // Assert
        assertNull(bookService.findBookById(bookId));
    }

    @Test
    void testFullCrudCycle() {
        // Create
        Book newBook = bookService.createBook(new Book(null, "CRUD Test", "CRUD Author"));
        Long id = newBook.getId();
        assertNotNull(id);

        // Read
        Book found = bookService.findBookById(id);
        assertNotNull(found);
        assertEquals("CRUD Test", found.getTitle());

        // Update
        Book updated = bookService.updateBook(id, new Book(null, "CRUD Updated", "CRUD Author Updated"));
        assertNotNull(updated);
        assertEquals("CRUD Updated", updated.getTitle());

        // Delete
        bookService.deleteBook(id);
        assertNull(bookService.findBookById(id));
    }
}

