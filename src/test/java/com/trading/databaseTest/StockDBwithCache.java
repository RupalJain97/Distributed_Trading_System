package com.trading.databaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.trading.model.StockModel;
import com.trading.repository.StockRepository;
import com.trading.service.StockService;

@SpringBootTest
// Ensure a single instance is used for all tests in the class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // Start a transaction for the entire test class
@Rollback(true) // Rollback all changes after the tests
public class StockDBwithCache {
    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Test
    public void testStockCacheSyncWithDB() {
        StockModel stockFromCache = stockService.findStockBySymbol("AAPL");
        StockModel stockFromDB = stockRepository.findBySymbol("AAPL");

        // Print results
        if (stockFromCache != null) {
            System.out.println("Cache - Stock: AAPL, Quantity: " + stockFromCache.getQuantity() + ", Price: " + stockFromCache.getPrice());
        } else {
            System.out.println("Stock not found in cache.");
        }

        if (stockFromDB != null) {
            System.out.println("DB - Stock: AAPL, Quantity: " + stockFromDB.getQuantity() + ", Price: " + stockFromDB.getPrice());
        } else {
            System.out.println("Stock not found in DB.");
        }
        
        assertEquals(stockFromCache.getQuantity(), stockFromDB.getQuantity());
        assertEquals(stockFromCache.getPrice(), stockFromDB.getPrice());
    }

}
