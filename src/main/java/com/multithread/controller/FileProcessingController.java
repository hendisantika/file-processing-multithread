package com.multithread.controller;

import com.multithread.config.ProcessingConfiguration;
import com.multithread.domain.dto.ProcessingResult;
import com.multithread.service.LogFileProcessorService;
import com.multithread.service.TransactionFileProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

@RestController
@RequestMapping("/api/processing")
public class FileProcessingController {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingController.class);

    @Autowired
    private TransactionFileProcessorService transactionProcessor;

    @Autowired
    private LogFileProcessorService logProcessor;

    @Autowired
    private ProcessingConfiguration config;

    /**
     * Process transaction files using single-threaded approach
     */
    @PostMapping("/transactions/single")
    public ResponseEntity<ProcessingResult> processTransactionsSingleThreaded() {
        try {
            log.info("Received request to process transactions (single-threaded)");
            ProcessingResult result = transactionProcessor.processSingleThreaded("transactions/**.ndjson");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing transactions", e);
            return ResponseEntity.internalServerError()
                    .body(ProcessingResult.builder()
                            .status("ERROR")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Process transaction files using multi-threaded approach
     */
    @PostMapping("/transactions/multi")
    public ResponseEntity<ProcessingResult> processTransactionsMultiThreaded(
            @RequestParam(defaultValue = "5") int threads) {
        try {
            log.info("Received request to process transactions (multi-threaded) with {} threads", threads);

            // Validate thread pool size
            if (threads < config.getThreadPool().getMinSize() || threads > config.getThreadPool().getMaxSize()) {
                return ResponseEntity.badRequest()
                        .body(ProcessingResult.builder()
                                .status("ERROR")
                                .message(String.format("Thread pool size must be between %d and %d",
                                        config.getThreadPool().getMinSize(),
                                        config.getThreadPool().getMaxSize()))
                                .build());
            }

            ProcessingResult result = transactionProcessor.processMultiThreaded("transactions/**.ndjson", threads);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing transactions", e);
            return ResponseEntity.internalServerError()
                    .body(ProcessingResult.builder()
                            .status("ERROR")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Process log files using single-threaded approach
     */
    @PostMapping("/logs/single")
    public ResponseEntity<ProcessingResult> processLogsSingleThreaded() {
        try {
            log.info("Received request to process logs (single-threaded)");
            ProcessingResult result = logProcessor.processSingleThreaded("logs/**.ndjson");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing logs", e);
            return ResponseEntity.internalServerError()
                    .body(ProcessingResult.builder()
                            .status("ERROR")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Process log files using multi-threaded approach
     */
    @PostMapping("/logs/multi")
    public ResponseEntity<ProcessingResult> processLogsMultiThreaded(
            @RequestParam(defaultValue = "5") int threads) {
        try {
            log.info("Received request to process logs (multi-threaded) with {} threads", threads);

            // Validate thread pool size
            if (threads < config.getThreadPool().getMinSize() || threads > config.getThreadPool().getMaxSize()) {
                return ResponseEntity.badRequest()
                        .body(ProcessingResult.builder()
                                .status("ERROR")
                                .message(String.format("Thread pool size must be between %d and %d",
                                        config.getThreadPool().getMinSize(),
                                        config.getThreadPool().getMaxSize()))
                                .build());
            }

            ProcessingResult result = logProcessor.processMultiThreaded("logs/**.ndjson", threads);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing logs", e);
            return ResponseEntity.internalServerError()
                    .body(ProcessingResult.builder()
                            .status("ERROR")
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Compare single-threaded vs multi-threaded performance
     */
    @PostMapping("/transactions/compare")
    public ResponseEntity<Map<String, Object>> compareTransactionProcessing(
            @RequestParam(defaultValue = "5") int threads) {
        try {
            log.info("Comparing single vs multi-threaded transaction processing");

            ProcessingResult singleResult = transactionProcessor.processSingleThreaded("transactions/**.ndjson");
            ProcessingResult multiResult = transactionProcessor.processMultiThreaded("transactions/**.ndjson", threads);

            Map<String, Object> comparison = new HashMap<>();
            comparison.put("singleThreaded", singleResult);
            comparison.put("multiThreaded", multiResult);

            double speedup = (double) singleResult.getProcessingTimeMs() / multiResult.getProcessingTimeMs();
            comparison.put("speedupFactor", String.format("%.2fx", speedup));
            comparison.put("timeSaved", singleResult.getProcessingTimeMs() - multiResult.getProcessingTimeMs() + "ms");

            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            log.error("Error comparing processing methods", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Get system configuration
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfiguration() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("defaultThreadPoolSize", config.getThreadPool().getDefaultSize());
        configMap.put("maxThreadPoolSize", config.getThreadPool().getMaxSize());
        configMap.put("minThreadPoolSize", config.getThreadPool().getMinSize());
        configMap.put("availableProcessors", Runtime.getRuntime().availableProcessors());

        return ResponseEntity.ok(configMap);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "File Processing Multithread");
        return ResponseEntity.ok(health);
    }
}
