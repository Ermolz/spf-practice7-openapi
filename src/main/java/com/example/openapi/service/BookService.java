package com.example.openapi.service;

import com.example.openapi.entity.Book;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookService {
    private final Map<Long, Book> bookStore = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(3);

    // Start data
    public BookService() {
        bookStore.put(1L, new Book(1L, "The Lord of the Rings", "J.R.R. Tolkien"));
        bookStore.put(2L, new Book(2L, "Dune", "Frank Herbert"));
        bookStore.put(3L, new Book(3L, "1984", "George Orwell"));
    }

    @Cacheable(value = "booksList")
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookStore.values());
    }

    @Cacheable(value = "books", key = "#id")
    public Book findBookById(Long id) {
        return bookStore.get(id);
    }

    @CacheEvict(value = {"books", "booksList"}, allEntries = true)
    public Book createBook(Book book) {
        long newId = sequence.incrementAndGet();
        book.setId(newId);
        bookStore.put(newId, book);
        return book;
    }

    @CacheEvict(value = {"books", "booksList"}, allEntries = true)
    public Book updateBook(Long id, Book updatedBook) {
        if (!bookStore.containsKey(id)) {
            return null;
        }
        updatedBook.setId(id);
        bookStore.put(id, updatedBook);
        return updatedBook;
    }

    @CacheEvict(value = {"books", "booksList"}, allEntries = true)
    public void deleteBook(Long id) {
        bookStore.remove(id);
    }
}