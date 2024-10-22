package com.trading.service;

import com.trading.service.OrderService;
import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.repository.StockRepository;

import org.assertj.core.api.DateAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // Start a transaction for the entire test class
@Rollback(true) // Rollback all changes after the tests
public class StockServiceTest {

    @Autowired
    private TestOrderService orderService;

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        stockService.clearStockCache(); // Clear cache before each test
    }

    @Test
    @Order(1)
    public void testConcurrentBuyAndSell() throws InterruptedException {
        System.out.println("Test with only 2 orders");
        ExecutorService executor = Executors.newFixedThreadPool(2); // Two threads for buy and sell operations

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Print the stock quantities before the test
        System.out.println("Before: [" + dtf.format(LocalDateTime.now()) + "]");
        printStockQuantity("AAPL");
        printStockQuantity("GOOGL");

        // Buy order
        executor.submit(() -> {
            OrderModel buyOrder = new OrderModel("emma", "AAPL", 10, "Apple", 149.3f, null, 1493f, "Buy");
            System.out.println("Attempting to buy 10 shares of AAPL... " + dtf.format(LocalDateTime.now()));
            orderService.placeBuyOrder(buyOrder, "emma");
        });

        // Sell order
        executor.submit(() -> {
            OrderModel sellOrder = new OrderModel("jainrupal", "GOOGL", 5, "Google", 2801.5f, null, 14007.5f, "Sell");
            System.out.println("Attempting to sell 5 shares of GOOGL... " + dtf.format(LocalDateTime.now()) );
            orderService.placeSellOrder(sellOrder, "jainrupal");
        });

        // Shut down the executor and wait for tasks to finish
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(1000);
        }

        // Print the stock quantities before the test
        System.out.println("After: [" + dtf.format(LocalDateTime.now()) + "]");
        printStockQuantity("AAPL");
        printStockQuantity("GOOGL");
    }

    @Test
    @Order(2)
    public void testConcurrentBuyAndSellBulk() throws InterruptedException {
        System.out.println("Test with only 15 orders");
        ExecutorService executor = Executors.newFixedThreadPool(10); // Two threads for buy and sell operations

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Print the stock quantities before the test
        System.out.println("Before: [" + dtf.format(LocalDateTime.now()) + "]");
        printStockQuantity("AAPL");
        printStockQuantity("GOOGL");

        for (int i = 0; i < 15; i++) {
            final int requestId = i + 1;

            if (i % 2 == 0) {
                executor.submit(() -> {
                    OrderModel buyOrder = new OrderModel("emma", "AAPL", 10, "Apple", 149.3f, null, 1493f, "Buy");
                    System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Thread-" + Thread.currentThread().getName() +
                        " : Request " + requestId + ": Attempting to buy 10 shares of AAPL...");
                    orderService.placeBuyOrder(buyOrder, "emma");
                });
            } else {
                executor.submit(() -> {
                    OrderModel sellOrder = new OrderModel("jainrupal", "GOOGL", 5, "Google", 2801.5f, null, 14007.5f, "Sell");
                    System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Thread-" + Thread.currentThread().getName() +
                        " : Request " + requestId + ": Attempting to sell 5 shares of GOOGL...");
                    orderService.placeSellOrder(sellOrder, "jainrupal");
                });
            }
        }
    
        // Shut down the executor and wait for tasks to finish
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Some tasks did not finish within the timeout!");
        } else {
            System.out.println("[" + dtf.format(LocalDateTime.now()) + "] All tasks completed.");
        }

        // Print the stock quantities before the test
        System.out.println("After: [" + dtf.format(LocalDateTime.now()) + "]");
        printStockQuantity("AAPL");
        printStockQuantity("GOOGL");
    }


    // Helper method to print stock quantities before and after the test
    private void printStockQuantity(String stockSymbol) {
        StockModel stock = stockRepository.findBySymbol(stockSymbol);
        if (stock != null) {
            System.out.println("Stock: " + stockSymbol + ", Available Quantity: " + stock.getQuantity() + ", Price: " + stock.getPrice());
        } else {
            System.out.println("Stock: " + stockSymbol + " not found in the repository.");
        }
    }


    @Test
    @Order(3)
    public void testFindStockBySymbolFromCache() {
        // Given
        StockModel cachedStock = new StockModel("BIDU", "Baidu Inc.", 2000, 190.85);

        stockService.updateStock(cachedStock);
        System.out.println(stockService.findStockBySymbol("BIDU"));

        // When
        StockModel result = stockService.findStockBySymbol("BIDU");
        System.out.println("Cache Result: " + result.getSymbol() + " " + result.getQuantity());

        // Then
        assertEquals("BIDU", result.getSymbol()); 
        assertEquals(2000, result.getQuantity());
        System.out.println("Stock retrieved from cache...");
    }

    @Test
    @Order(4)
    public void testFindStockBySymbolFromDB() {
        // Given
        StockModel dbStock = new StockModel("BIDU", "Baidu Inc.", 2000, 190.85);
        when(stockRepository.findBySymbol("BIDU")).thenReturn(dbStock);

        // When
        StockModel result = stockService.findStockBySymbol("BIDU");
        System.out.println("DB Result: " + result.getSymbol() + " " + result.getQuantity());

        // Then
        assertEquals("BIDU", result.getSymbol()); // Verify the symbol
        assertEquals(2000, result.getQuantity()); // Verify the quantity
        verify(stockRepository, times(1)).findBySymbol("BIDU");
        System.out.println("Stock retrieved from DB...");
    }

    @Test
    @Order(5)
    public void testUpdateStock() {
        // Given
        StockModel existingStock = new StockModel("AFRM", "Affirm Holdings Inc.", 700, 100.25);
        when(stockRepository.findBySymbol("AFRM")).thenReturn(existingStock); 
        
        StockModel updatedStock = new StockModel("AFRM", "Affirm Holdings Inc.", 1000, 100.25);

        // When
        stockService.updateStock(updatedStock);

        // Then
        verify(stockRepository, times(1)).save(any(StockModel.class)); // Verify save call
        assertEquals(updatedStock.getQuantity(), stockRepository.findBySymbol("AFRM").getQuantity());
        System.out.println("Stock updated successfully...");
    }
}
