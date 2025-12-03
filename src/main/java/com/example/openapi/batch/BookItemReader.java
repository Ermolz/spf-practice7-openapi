package com.example.openapi.batch;

import com.example.openapi.entity.Book;
import com.example.openapi.service.BookService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@StepScope
public class BookItemReader implements ItemReader<Book> {

    @Autowired
    private BookService bookService;
    private Iterator<Book> bookIterator;

    @Override
    public Book read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (bookIterator == null) {
            List<Book> books = bookService.getAllBooks();
            bookIterator = books.iterator();
        }

        if (bookIterator.hasNext()) {
            return bookIterator.next();
        }

        return null;
    }
}

