package com.example.openapi.validator;

import com.example.openapi.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {

    public boolean isValid(Book book) {
        if (book == null) {
            return false;
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return false;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return false;
        }
        if (book.getTitle().length() > 200) {
            return false;
        }
        if (book.getAuthor().length() > 100) {
            return false;
        }
        return true;
    }

    public String validateAndGetMessage(Book book) {
        if (!isValid(book)) {
            if (book == null) {
                return "Book cannot be null";
            }
            if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
                return "Title cannot be empty";
            }
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                return "Author cannot be empty";
            }
            if (book.getTitle().length() > 200) {
                return "Title cannot exceed 200 characters";
            }
            if (book.getAuthor().length() > 100) {
                return "Author name cannot exceed 100 characters";
            }
        }
        return "Valid";
    }
}

