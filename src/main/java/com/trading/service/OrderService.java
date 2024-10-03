package com.trading.service;

import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    // private final List<StockModel> stocks;  
    private ConcurrentHashMap<String, List<OrderModel>> orderDatabase = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, List<OrderModel>> userHistoryDatabase = new ConcurrentHashMap<>();

    // private final AtomicInteger orderCount = new AtomicInteger(0);
    // private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // public OrderService(List<StockModel> stocks) {
    //     this.stocks = stocks;
    // }

    public void placeBuyOrder(OrderModel order, String userId) {
        List<OrderModel> userOrders = orderDatabase.getOrDefault(userId, new ArrayList<>());
        userOrders.add(order); // Add the new order
        orderDatabase.put(userId, userOrders);
        addToOrderHistory(order, userId);

        // executorService.execute(() -> {
        //     StockModel stock = findStockBySymbol(order.getStockSymbol());
        //     if (stock != null && order.getQuantity() > 0) {
        //         stock.setPrice(stock.getPrice() + 1);  // Simulate stock price increase after buying
        //         orderCount.incrementAndGet();
        //         System.out.println("Buy Order placed for " + order.getQuantity() + " shares of " + stock.getSymbol());
        //     }
        // });
    }

    public void placeSellOrder(OrderModel order, String userId) {
        List<OrderModel> orders = orderDatabase.getOrDefault(userId, new ArrayList<>());
        OrderModel orderToSell = orders.stream()
            .filter(o -> o.getStockSymbol().equals(order.getStockSymbol()))
            .findFirst()
            .orElse(null);

        if (orderToSell != null) {
            int remainingQuantity = orderToSell.getQuantity() - order.getQuantity();
            if (remainingQuantity <= 0) {
                orders.remove(orderToSell);  // Remove if fully sold
            } else {
                orderToSell.setQuantity(remainingQuantity);
            }
            addToOrderHistory(order, userId);  // Store in history
        }

        // executorService.execute(() -> {
        //     StockModel stock = findStockBySymbol(order.getStockSymbol());
        //     if (stock != null && order.getQuantity() > 0) {
        //         stock.setPrice(stock.getPrice() - 1);  // Simulate stock price decrease after selling
        //         orderCount.incrementAndGet();
        //         System.out.println("Sell Order placed for " + order.getQuantity() + " shares of " + stock.getSymbol());
        //     }
        // });
    }

    // Retrieve user orders
    public List<OrderModel> getUserOrders(String userId) {
        return orderDatabase.getOrDefault(userId, new ArrayList<>());
    }

    // Retrieve order history
    public List<OrderModel> getUserOrderHistory(String userId) {
        return userHistoryDatabase.getOrDefault(userId, new ArrayList<>());
    }

    // Helper method to add to order history
    private void addToOrderHistory(OrderModel order, String userId) {
        List<OrderModel> orderHistory = userHistoryDatabase.getOrDefault(userId, new ArrayList<>());
        orderHistory.add(order);
        userHistoryDatabase.put(userId, orderHistory);
    }

    public int getOrderCount() {
        return orderDatabase.values().stream().mapToInt(List::size).sum();
    }

}
