package com.example.openapi.controller;

import com.example.openapi.entity.Book;
import com.example.openapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllBooks() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(
                new Book(1L, "Book 1", "Author 1"),
                new Book(2L, "Book 2", "Author 2")
        );
        when(bookService.getAllBooks()).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[0].author").value("Author 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Book 2"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetBookById() throws Exception {
        // Arrange
        Book book = new Book(1L, "Test Book", "Test Author");
        when(bookService.findBookById(1L)).thenReturn(book);

        // Act & Assert
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));

        verify(bookService, times(1)).findBookById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetBookByIdNotFound() throws Exception {
        // Arrange
        when(bookService.findBookById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).findBookById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateBook() throws Exception {
        // Arrange
        Book newBook = new Book(null, "New Book", "New Author");
        Book createdBook = new Book(4L, "New Book", "New Author");
        when(bookService.createBook(any(Book.class))).thenReturn(createdBook);

        // Act & Assert
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author").value("New Author"));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBook() throws Exception {
        // Arrange
        Book updatedBook = new Book(1L, "Updated Book", "Updated Author");
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        // Act & Assert
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author").value("Updated Author"));

        verify(bookService, times(1)).updateBook(eq(1L), any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookNotFound() throws Exception {
        // Arrange
        Book updatedBook = new Book(999L, "Updated Book", "Updated Author");
        when(bookService.updateBook(eq(999L), any(Book.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(eq(999L), any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBook() throws Exception {
        // Arrange
        doNothing().when(bookService).deleteBook(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }
}

