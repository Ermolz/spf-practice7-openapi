package com.example.openapi.scheduler;

import com.example.openapi.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookStatisticsScheduler {

    private final BookService bookService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void generateBookStatistics() {
        log.info("Starting scheduled book statistics generation...");
        
        try {
            List<com.example.openapi.entity.Book> allBooks = bookService.getAllBooks();
            
            int totalBooks = allBooks.size();
            
            Map<String, Long> authorCount = allBooks.stream()
                    .collect(Collectors.groupingBy(
                            com.example.openapi.entity.Book::getAuthor,
                            Collectors.counting()
                    ));
            
            String mostPopularAuthor = authorCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            
            log.info("=== Book Statistics Report ===");
            log.info("Total books: {}", totalBooks);
            log.info("Number of unique authors: {}", authorCount.size());
            log.info("Most popular author: {} ({} books)", 
                    mostPopularAuthor, 
                    authorCount.getOrDefault(mostPopularAuthor, 0L));
            log.info("Author distribution: {}", authorCount);
            log.info("=== End of Statistics Report ===");
            
        } catch (Exception e) {
            log.error("Error generating book statistics", e);
        }
    }
}

