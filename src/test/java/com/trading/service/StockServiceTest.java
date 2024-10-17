package com.trading.service;

import com.trading.service.OrderService;
import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.repository.StockRepository;

import org.assertj.core.api.DateAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class StockServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Test
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
            OrderModel buyOrder = new OrderModel();
            buyOrder.setStockSymbol("AAPL");
            buyOrder.setQuantity(10);
            buyOrder.setStockPrice((float) 149.3);
            buyOrder.setOrderPrice(1493);
            buyOrder.setOrderType("Buy");
            buyOrder.setUserId("emma");
            System.out.println("Attempting to buy 10 shares of AAPL... " + dtf.format(LocalDateTime.now()));
            orderService.placeBuyOrder(buyOrder, "emma");
        });

        // Sell order
        executor.submit(() -> {
            OrderModel sellOrder = new OrderModel();
            sellOrder.setStockSymbol("GOOGL");
            sellOrder.setQuantity(5);
            sellOrder.setStockPrice((float) 2801.5);
            sellOrder.setOrderPrice((float) 14007.5);
            sellOrder.setOrderType("Sell");
            sellOrder.setUserId("jainrupal");
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
    public void testConcurrentBuyAndSellBulk() throws InterruptedException {
        System.out.println("Test with only 15 orders");
        ExecutorService executor = Executors.newFixedThreadPool(10); // Two threads for buy and sell operations

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Print the stock quantities before the test
        System.out.println("Before: [" + dtf.format(LocalDateTime.now()) + "]");
        printStockQuantity("AAPL");
        printStockQuantity("GOOGL");

        for (int i = 0; i < 15; i++) {
            final int requestId = i + 1; // To differentiate between requests
    
            // Half the requests will be buy orders, the rest will be sell orders
            if (i % 2 == 0) {
                executor.submit(() -> {
                    OrderModel buyOrder = new OrderModel();
                    buyOrder.setStockSymbol("AAPL");
                    buyOrder.setQuantity(10);
                    buyOrder.setStockPrice((float) 149.3);
                    buyOrder.setOrderPrice(1493);
                    buyOrder.setOrderType("Buy");
                    buyOrder.setUserId("emma");
                    System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Thread-" + Thread.currentThread().getName() +
                        " : Request " + requestId + ": Attempting to buy 10 shares of AAPL...");
                    orderService.placeBuyOrder(buyOrder, "emma");
                });
            } else {
                executor.submit(() -> {
                    OrderModel sellOrder = new OrderModel();
                    sellOrder.setStockSymbol("GOOGL");
                    sellOrder.setQuantity(5);
                    sellOrder.setStockPrice((float) 2801.5);
                    sellOrder.setOrderPrice((float) 14007.5);
                    sellOrder.setOrderType("Buy");
                    sellOrder.setUserId("jainrupal");
                    System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Thread-" + Thread.currentThread().getName() +
                        " : Request " + requestId + ": Attempting to buy 5 shares of GOOGL...");
                    orderService.placeBuyOrder(sellOrder, "jainrupal");
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
}
