package com.example.openapi.batch;

import com.example.openapi.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@Slf4j
public class BookItemProcessor implements ItemProcessor<Book, Book> {

    @Override
    public Book process(Book book) throws Exception {
        if (book == null) {
            log.warn("Skipping null book");
            return null;
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            log.warn("Skipping book with empty title: {}", book);
            return null;
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            log.warn("Skipping book with empty author: {}", book);
            return null;
        }

        Book processedBook = new Book();
        processedBook.setId(book.getId());
        processedBook.setTitle(book.getTitle().trim());
        processedBook.setAuthor(book.getAuthor().trim());

        log.debug("Processed book: {} -> {}", book, processedBook);
        return processedBook;
    }
}

