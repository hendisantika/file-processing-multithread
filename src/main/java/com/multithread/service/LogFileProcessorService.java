package com.multithread.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multithread.domain.dto.LogEntryDto;
import com.multithread.domain.dto.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * Project : file-processing-multithread
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 12/10/25
 * Time: 10.30
 * To change this template use File | Settings | File Templates.
 */

@Service
public class LogFileProcessorService implements FileProcessingService {

    private static final Logger log = LoggerFactory.getLogger(LogFileProcessorService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public ProcessingResult processSingleThreaded(String filePattern) throws Exception {
        log.info("Starting single-threaded log processing for pattern: {}", filePattern);
        LocalDateTime startTime = LocalDateTime.now();
        long startMs = System.currentTimeMillis();

        List<LogEntryDto> logEntries = new ArrayList<>();
        int recordCount = 0;

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources(filePattern);

        for (Resource resource : resources) {
            log.debug("Processing log file: {}", resource.getFilename());
            // Simulate processing time for log analysis
            Thread.sleep(150L);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LogEntryDto logEntry = objectMapper.readValue(line, LogEntryDto.class);
                    recordCount++;
                    logEntries.add(logEntry);

                    // Simulate log analysis (e.g., detecting errors)
                    if ("ERROR".equals(logEntry.getLevel()) || "FATAL".equals(logEntry.getLevel())) {
                        log.debug("Found error in log: {}", logEntry.getMessage());
                    }
                }
            }
        }

        long processingTime = System.currentTimeMillis() - startMs;
        LocalDateTime endTime = LocalDateTime.now();

        log.info("Single-threaded log processing completed. Files: {}, Records: {}, Time: {}ms",
                resources.length, recordCount, processingTime);

        return ProcessingResult.builder()
                .processingType("Log Analysis")
                .totalFiles(resources.length)
                .recordsProcessed(recordCount)
                .processingTimeMs(processingTime)
                .startTime(startTime)
                .endTime(endTime)
                .useMultithreading(false)
                .threadPoolSize(1)
                .status("SUCCESS")
                .message(String.format("Analyzed %d log entries from %d files", recordCount, resources.length))
                .build();
    }

    @Override
    public ProcessingResult processMultiThreaded(String filePattern, int threadPoolSize) throws Exception {
        log.info("Starting multi-threaded log processing for pattern: {} with {} threads",
                filePattern, threadPoolSize);
        LocalDateTime startTime = LocalDateTime.now();
        long startMs = System.currentTimeMillis();

        List<LogEntryDto> logEntries = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger recordCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources(filePattern);

        for (Resource resource : resources) {
            executor.execute(() -> {
                try {
                    log.debug("Processing log file: {} on thread: {}",
                            resource.getFilename(), Thread.currentThread().getName());
                    // Simulate processing time for log analysis
                    Thread.sleep(150L);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        LogEntryDto logEntry = objectMapper.readValue(line, LogEntryDto.class);
                        recordCount.incrementAndGet();
                        logEntries.add(logEntry);

                        // Simulate log analysis (e.g., detecting errors)
                        if ("ERROR".equals(logEntry.getLevel()) || "FATAL".equals(logEntry.getLevel())) {
                            errorCount.incrementAndGet();
                            log.debug("Found error in log: {}", logEntry.getMessage());
                        }
                    }
                    reader.close();
                } catch (Exception e) {
                    log.error("Error processing log file: {}", resource.getFilename(), e);
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long processingTime = System.currentTimeMillis() - startMs;
        LocalDateTime endTime = LocalDateTime.now();

        log.info("Multi-threaded log processing completed. Files: {}, Records: {}, Errors: {}, Time: {}ms, Threads: {}",
                resources.length, recordCount.get(), errorCount.get(), processingTime, threadPoolSize);

        return ProcessingResult.builder()
                .processingType("Log Analysis")
                .totalFiles(resources.length)
                .recordsProcessed(recordCount.get())
                .processingTimeMs(processingTime)
                .startTime(startTime)
                .endTime(endTime)
                .useMultithreading(true)
                .threadPoolSize(threadPoolSize)
                .status("SUCCESS")
                .message(String.format("Analyzed %d log entries from %d files using %d threads (%d errors found)",
                        recordCount.get(), resources.length, threadPoolSize, errorCount.get()))
                .build();
    }
}
