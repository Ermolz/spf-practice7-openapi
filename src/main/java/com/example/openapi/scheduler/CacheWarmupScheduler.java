package com.example.openapi.scheduler;

import com.example.openapi.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupScheduler {

    private final BookService bookService;

    @Scheduled(fixedRate = 300000) // Every 5 minutes (300000 milliseconds)
    public void warmupCache() {
        log.info("Starting cache warmup...");
        
        try {
            // Preload all books into cache
            bookService.getAllBooks();
            log.info("Cache warmup: Loaded all books into cache");
            
            // Preload first few books by ID (assuming they exist)
            for (long i = 1; i <= 3; i++) {
                bookService.findBookById(i);
            }
            log.info("Cache warmup: Preloaded books with IDs 1-3");
            
            log.info("Cache warmup completed successfully");
            
        } catch (Exception e) {
            log.error("Error during cache warmup", e);
        }
    }
}

