package com.example.openapi.service;

import com.example.openapi.entity.Book;
import com.example.openapi.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookValidationService {

    private final BookValidator bookValidator;

    public Book validateAndProcessBook(Book book) {
        if (!bookValidator.isValid(book)) {
            throw new IllegalArgumentException(bookValidator.validateAndGetMessage(book));
        }
        // Additional processing
        Book processed = new Book();
        processed.setId(book.getId());
        processed.setTitle(book.getTitle().trim());
        processed.setAuthor(book.getAuthor().trim());
        return processed;
    }
}

