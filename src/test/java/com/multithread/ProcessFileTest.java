package com.multithread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multithread.domain.dto.TransactionDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ProcessFileTest {

    @Autowired
    private ResourceLoader resourceLoader;

    // Field without Multithread
    //List<TransactionDto> listTransaction = new ArrayList<>();
    //Integer transactionCountRecord = 0;

    //Field with Multithread
    List<TransactionDto> listTransaction = Collections.synchronizedList(new ArrayList<>());
    AtomicInteger transactionCountRecord = new AtomicInteger(0);


//    @Test
//    @SneakyThrows
//    void processFileWithoutMultithread() {
//
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
//        Resource[] resources = resolver.getResources("transactions/**.ndjson");
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        for (Resource resource : resources) {
//            Thread.sleep(1000L);
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    TransactionDto transactionDto = objectMapper.readValue(line, TransactionDto.class);
//                    transactionCountRecord += 1;
//                    listTransaction.add(transactionDto);
//                    System.out.println(transactionDto);
//                }
//            }
//        }
//        System.out.println("Size of list transaction: " + listTransaction.size());
//        System.out.println("transaction count record: " + transactionCountRecord);
//    }

    @Test
    @SneakyThrows
    void processFileWithMultithread() {

        // Define size of thread pool
        ExecutorService executor = Executors.newFixedThreadPool(5);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources("transactions/**.ndjson");
        ObjectMapper objectMapper = new ObjectMapper();

        for (Resource resource : resources) {
            executor.execute(() -> {
                try {
                    Thread.sleep(1000L);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        TransactionDto transactionDto = objectMapper.readValue(line, TransactionDto.class);
                        transactionCountRecord.incrementAndGet();

                        listTransaction.add(transactionDto);
                        System.out.println(transactionDto);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        System.out.println("Size of list transaction: " + listTransaction.size());
        System.out.println("transaction count record: " + transactionCountRecord);
    }
}