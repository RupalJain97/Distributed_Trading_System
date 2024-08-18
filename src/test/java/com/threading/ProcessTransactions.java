package com.threading;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ProcessTransactions {
    @Autowired
    private ResourceLoader resourceLoader;
    List<Transactions> listTransactionSingleThread = new ArrayList<>();
    Integer transactionRecordCountSingleThread = 0;

    List<Transactions> listTransaction = Collections.synchronizedList(new ArrayList<>());
    AtomicInteger transactionCountRecord = new AtomicInteger(0);

    @Test
    @SneakyThrows
    void processFileWithoutMultithread() {
        long startTime = System.currentTimeMillis();

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources("classpath:/transactions/**.ndjson");
        ObjectMapper objectMapper = new ObjectMapper();

        for (Resource resource : resources) {
            // Thread sleeps around 1 second as if the time needed to process a file
            Thread.sleep(1000L);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Transactions Transactions = objectMapper.readValue(line, Transactions.class);
                    transactionRecordCountSingleThread += 1;
                    listTransactionSingleThread.add(Transactions);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Size of list transaction: " + listTransactionSingleThread.size());
        System.out.println("Transaction record count: " + transactionRecordCountSingleThread);
        System.out.println("Execution time without multithreading: " + duration + " milliseconds");

    }

    @Test
    @SneakyThrows
    void processFileWithMultithread() {
        long startTime = System.currentTimeMillis();

        // Define size of thread pool
        ExecutorService executor = Executors.newFixedThreadPool(5);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources("classpath:/transactions/**.ndjson");
        ObjectMapper objectMapper = new ObjectMapper();

        for (Resource resource : resources) {
            executor.execute(() -> {
                try {
                    // Thread sleeps around 1 second as if the time needed to process a file
                    Thread.sleep(1000L);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Transactions Transactions = objectMapper.readValue(line, Transactions.class);
                        transactionCountRecord.incrementAndGet();
//                        System.out.println("Thread " + Thread.currentThread().getId() + " updated transaction count to: " + updatedCount);

                        listTransaction.add(Transactions);
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

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Using Multithreading");
        System.out.println("Size of list transaction: " + listTransaction.size());
        System.out.println("transaction count record: " + transactionCountRecord);
        System.out.println("Execution time without multithreading: " + duration + " milliseconds");
    }
}
