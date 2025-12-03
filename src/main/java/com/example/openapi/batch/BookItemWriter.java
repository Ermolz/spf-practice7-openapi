package com.example.openapi.batch;

import com.example.openapi.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@StepScope
@Slf4j
public class BookItemWriter implements ItemWriter<Book> {

    @Value("${batch.export.directory:exports}")
    private String exportDirectory;

    private FileWriter fileWriter;
    private String currentFileName;

    @Override
    public void write(Chunk<? extends Book> chunk) throws Exception {
        if (fileWriter == null) {
            initializeWriter();
        }

        for (Book book : chunk) {
            if (book != null) {
                String line = String.format("%d,%s,%s%n", 
                    book.getId(), 
                    escapeCsv(book.getTitle()), 
                    escapeCsv(book.getAuthor()));
                fileWriter.write(line);
            }
        }

        fileWriter.flush();
        log.info("Written {} books to {}", chunk.size(), currentFileName);
    }

    private void initializeWriter() throws IOException {
        Path exportPath = Paths.get(exportDirectory);
        if (!Files.exists(exportPath)) {
            Files.createDirectories(exportPath);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        currentFileName = exportDirectory + "/books_export_" + timestamp + ".csv";

        fileWriter = new FileWriter(currentFileName);
        fileWriter.write("ID,Title,Author\n");
        log.info("Initialized CSV writer: {}", currentFileName);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public void close() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
            log.info("Closed CSV writer: {}", currentFileName);
        }
    }

    public String getCurrentFileName() {
        return currentFileName;
    }
}

