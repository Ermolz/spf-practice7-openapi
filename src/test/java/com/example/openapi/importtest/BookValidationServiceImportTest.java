package com.example.openapi.importtest;

import com.example.openapi.entity.Book;
import com.example.openapi.service.BookValidationService;
import com.example.openapi.validator.BookValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BookValidationService.class, BookValidator.class})
@Import({BookValidationService.class, BookValidator.class})
@TestPropertySource(properties = "spring.main.web-application-type=none")
class BookValidationServiceImportTest {

    @Autowired
    private BookValidationService bookValidationService;

    @Autowired
    private BookValidator bookValidator;

    @Test
    void testValidatorBeanIsLoaded() {
        // Перевірка, що валідатор був імпортований як бін
        assertNotNull(bookValidator);
    }

    @Test
    void testValidationServiceBeanIsLoaded() {
        // Перевірка, що сервіс валідації був імпортований як бін
        assertNotNull(bookValidationService);
    }

    @Test
    void testValidateAndProcessBook_ValidBook() {
        // Arrange
        Book validBook = new Book(1L, "Valid Title", "Valid Author");

        // Act
        Book processed = bookValidationService.validateAndProcessBook(validBook);

        // Assert
        assertNotNull(processed);
        assertEquals(1L, processed.getId());
        assertEquals("Valid Title", processed.getTitle());
        assertEquals("Valid Author", processed.getAuthor());
    }

    @Test
    void testValidateAndProcessBook_TrimsWhitespace() {
        // Arrange
        Book bookWithWhitespace = new Book(1L, "  Title With Spaces  ", "  Author With Spaces  ");

        // Act
        Book processed = bookValidationService.validateAndProcessBook(bookWithWhitespace);

        // Assert
        assertNotNull(processed);
        assertEquals("Title With Spaces", processed.getTitle());
        assertEquals("Author With Spaces", processed.getAuthor());
    }

    @Test
    void testValidateAndProcessBook_NullBook() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookValidationService.validateAndProcessBook(null)
        );
        assertEquals("Book cannot be null", exception.getMessage());
    }

    @Test
    void testValidateAndProcessBook_EmptyTitle() {
        // Arrange
        Book bookWithEmptyTitle = new Book(1L, "", "Author");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookValidationService.validateAndProcessBook(bookWithEmptyTitle)
        );
        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateAndProcessBook_EmptyAuthor() {
        // Arrange
        Book bookWithEmptyAuthor = new Book(1L, "Title", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookValidationService.validateAndProcessBook(bookWithEmptyAuthor)
        );
        assertEquals("Author cannot be empty", exception.getMessage());
    }

    @Test
    void testValidatorDirectly_ValidBook() {
        // Arrange
        Book validBook = new Book(1L, "Valid Title", "Valid Author");

        // Act
        boolean isValid = bookValidator.isValid(validBook);
        String message = bookValidator.validateAndGetMessage(validBook);

        // Assert
        assertTrue(isValid);
        assertEquals("Valid", message);
    }

    @Test
    void testValidatorDirectly_InvalidBook() {
        // Arrange
        Book invalidBook = new Book(1L, null, "Author");

        // Act
        boolean isValid = bookValidator.isValid(invalidBook);
        String message = bookValidator.validateAndGetMessage(invalidBook);

        // Assert
        assertFalse(isValid);
        assertEquals("Title cannot be empty", message);
    }
}

