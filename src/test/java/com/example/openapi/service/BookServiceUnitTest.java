package com.example.openapi.service;

import com.example.openapi.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceUnitTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
    }

    @Test
    void testGetAllBooks_ReturnsInitialBooks() {
        // Act
        List<Book> books = bookService.getAllBooks();

        // Assert
        assertNotNull(books);
        assertEquals(3, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("The Lord of the Rings")));
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Dune")));
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("1984")));
    }

    @Test
    void testFindBookById_ExistingBook() {
        // Act
        Book book = bookService.findBookById(1L);

        // Assert
        assertNotNull(book);
        assertEquals(1L, book.getId());
        assertEquals("The Lord of the Rings", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
    }

    @Test
    void testFindBookById_NonExistingBook() {
        // Act
        Book book = bookService.findBookById(999L);

        // Assert
        assertNull(book);
    }

    @Test
    void testCreateBook_AssignsNewId() {
        // Arrange
        Book newBook = new Book(null, "New Book", "New Author");

        // Act
        Book created = bookService.createBook(newBook);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getId());
        assertTrue(created.getId() > 3); // Should be greater than initial sequence
        assertEquals("New Book", created.getTitle());
        assertEquals("New Author", created.getAuthor());

        // Verify it's in the store
        Book found = bookService.findBookById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void testCreateBook_IncrementsSequence() {
        // Arrange
        Book book1 = new Book(null, "Book 1", "Author 1");
        Book book2 = new Book(null, "Book 2", "Author 2");

        // Act
        Book created1 = bookService.createBook(book1);
        Book created2 = bookService.createBook(book2);

        // Assert
        assertNotNull(created1.getId());
        assertNotNull(created2.getId());
        assertEquals(created1.getId() + 1, created2.getId());
    }

    @Test
    void testUpdateBook_ExistingBook() {
        // Arrange
        Book updatedBook = new Book(null, "Updated Title", "Updated Author");

        // Act
        Book result = bookService.updateBook(1L, updatedBook);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());

        // Verify the update persisted
        Book found = bookService.findBookById(1L);
        assertNotNull(found);
        assertEquals("Updated Title", found.getTitle());
        assertEquals("Updated Author", found.getAuthor());
    }

    @Test
    void testUpdateBook_NonExistingBook() {
        // Arrange
        Book updatedBook = new Book(null, "Updated Title", "Updated Author");

        // Act
        Book result = bookService.updateBook(999L, updatedBook);

        // Assert
        assertNull(result);
    }

    @Test
    void testDeleteBook_ExistingBook() {
        // Verify book exists
        assertNotNull(bookService.findBookById(1L));

        // Act
        bookService.deleteBook(1L);

        // Assert
        assertNull(bookService.findBookById(1L));
    }

    @Test
    void testDeleteBook_NonExistingBook() {
        // Act - should not throw exception
        assertDoesNotThrow(() -> bookService.deleteBook(999L));
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

