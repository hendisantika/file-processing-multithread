package com.multithread.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multithread.domain.dto.ProcessingResult;
import com.multithread.domain.dto.TransactionDto;
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
public class TransactionFileProcessorService implements FileProcessingService {

    private static final Logger log = LoggerFactory.getLogger(TransactionFileProcessorService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public ProcessingResult processSingleThreaded(String filePattern) throws Exception {
        log.info("Starting single-threaded transaction processing for pattern: {}", filePattern);
        LocalDateTime startTime = LocalDateTime.now();
        long startMs = System.currentTimeMillis();

        List<TransactionDto> transactions = new ArrayList<>();
        int recordCount = 0;

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources(filePattern);

        for (Resource resource : resources) {
            log.debug("Processing file: {}", resource.getFilename());
            // Simulate processing time
            Thread.sleep(100L);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    TransactionDto transaction = objectMapper.readValue(line, TransactionDto.class);
                    recordCount++;
                    transactions.add(transaction);
                }
            }
        }

        long processingTime = System.currentTimeMillis() - startMs;
        LocalDateTime endTime = LocalDateTime.now();

        log.info("Single-threaded processing completed. Files: {}, Records: {}, Time: {}ms",
                resources.length, recordCount, processingTime);

        return ProcessingResult.builder()
                .processingType("Transaction Processing")
                .totalFiles(resources.length)
                .recordsProcessed(recordCount)
                .processingTimeMs(processingTime)
                .startTime(startTime)
                .endTime(endTime)
                .useMultithreading(false)
                .threadPoolSize(1)
                .status("SUCCESS")
                .message(String.format("Processed %d transaction records from %d files", recordCount, resources.length))
                .build();
    }

    @Override
    public ProcessingResult processMultiThreaded(String filePattern, int threadPoolSize) throws Exception {
        log.info("Starting multi-threaded transaction processing for pattern: {} with {} threads",
                filePattern, threadPoolSize);
        LocalDateTime startTime = LocalDateTime.now();
        long startMs = System.currentTimeMillis();

        List<TransactionDto> transactions = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger recordCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources(filePattern);

        for (Resource resource : resources) {
            executor.execute(() -> {
                try {
                    log.debug("Processing file: {} on thread: {}",
                            resource.getFilename(), Thread.currentThread().getName());
                    // Simulate processing time
                    Thread.sleep(100L);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        TransactionDto transaction = objectMapper.readValue(line, TransactionDto.class);
                        recordCount.incrementAndGet();
                        transactions.add(transaction);
                    }
                    reader.close();
                } catch (Exception e) {
                    log.error("Error processing file: {}", resource.getFilename(), e);
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long processingTime = System.currentTimeMillis() - startMs;
        LocalDateTime endTime = LocalDateTime.now();

        log.info("Multi-threaded processing completed. Files: {}, Records: {}, Time: {}ms, Threads: {}",
                resources.length, recordCount.get(), processingTime, threadPoolSize);

        return ProcessingResult.builder()
                .processingType("Transaction Processing")
                .totalFiles(resources.length)
                .recordsProcessed(recordCount.get())
                .processingTimeMs(processingTime)
                .startTime(startTime)
                .endTime(endTime)
                .useMultithreading(true)
                .threadPoolSize(threadPoolSize)
                .status("SUCCESS")
                .message(String.format("Processed %d transaction records from %d files using %d threads",
                        recordCount.get(), resources.length, threadPoolSize))
                .build();
    }
}
