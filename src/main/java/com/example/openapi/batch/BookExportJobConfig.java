package com.example.openapi.batch;

import com.example.openapi.entity.Book;
import com.example.openapi.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookExportJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BookItemReader bookItemReader;
    private final BookItemProcessor bookItemProcessor;
    private final BookItemWriter bookItemWriter;
    private final BookService bookService;

    @Bean
    public Job bookExportJob() {
        return new JobBuilder("bookExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(readBooksStep())
                .next(validateBooksStep())
                .next(writeBooksStep())
                .build();
    }

    @Bean
    public Step readBooksStep() {
        return new StepBuilder("readBooksStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        List<Book> books = bookService.getAllBooks();
                        log.info("Step 1: Read {} books from service", books.size());
                        chunkContext.getStepContext().getStepExecution().getJobExecution()
                                .getExecutionContext().put("totalBooks", books.size());
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step validateBooksStep() {
        return new StepBuilder("validateBooksStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        List<Book> books = bookService.getAllBooks();
                        int validCount = 0;
                        int invalidCount = 0;
                        
                        for (Book book : books) {
                            if (book != null && 
                                book.getTitle() != null && !book.getTitle().trim().isEmpty() &&
                                book.getAuthor() != null && !book.getAuthor().trim().isEmpty()) {
                                validCount++;
                            } else {
                                invalidCount++;
                            }
                        }
                        
                        log.info("Step 2: Validated {} books - {} valid, {} invalid", 
                                books.size(), validCount, invalidCount);
                        chunkContext.getStepContext().getStepExecution().getJobExecution()
                                .getExecutionContext().put("validBooks", validCount);
                        chunkContext.getStepContext().getStepExecution().getJobExecution()
                                .getExecutionContext().put("invalidBooks", invalidCount);
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step writeBooksStep() {
        return new StepBuilder("writeBooksStep", jobRepository)
                .<Book, Book>chunk(10, transactionManager)
                .reader(bookItemReader)
                .processor(bookItemProcessor)
                .writer(bookItemWriter)
                .build();
    }
}

