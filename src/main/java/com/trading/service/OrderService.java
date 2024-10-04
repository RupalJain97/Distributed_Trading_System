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
    // private final ExecutorService executorService =
    // Executors.newFixedThreadPool(5);

    public void placeBuyOrder(OrderModel order, String userId) {
        List<OrderModel> userOrders = orderDatabase.getOrDefault(userId, new ArrayList<>());
        StockModel company_stock = StockService.findStockBySymbol(order.getStockSymbol());
        order.setCompanyName(company_stock.getCompanyName());
        order.setOrderDate();

        System.out.println(order.toString());
        userOrders.add(order); // Add the new order
        orderDatabase.put(userId, userOrders);
        addToOrderHistory(order, userId);

        // executorService.execute(() -> {
        // StockModel stock = findStockBySymbol(order.getStockSymbol());
        // if (stock != null && order.getQuantity() > 0) {
        // stock.setPrice(stock.getPrice() + 1); // Simulate stock price increase after
        // buying
        // orderCount.incrementAndGet();
        // System.out.println("Buy Order placed for " + order.getQuantity() + " shares
        // of " + stock.getSymbol());
        // }
        // });

        // Update stock price after buying (optional, could simulate price change)
        StockModel stock = StockService.findStockBySymbol(order.getStockSymbol());
        if (stock != null) {
            stock.setQuantity(stock.getQuantity() - order.getQuantity()); // Update stock quantity
            stock.setPrice(stock.getPrice() + 1); // Simulate stock price increase after buying
        }

        System.out.println("Buy Order placed for " + order.getQuantity() + " shares of " + order.getStockSymbol());
    }

    public void placeSellOrder(OrderModel order, String userId) {
        StockModel company_stock = StockService.findStockBySymbol(order.getStockSymbol());
        order.setCompanyName(company_stock.getCompanyName());
        order.setOrderDate();
        
        List<OrderModel> orders = orderDatabase.getOrDefault(userId, new ArrayList<>());
        OrderModel orderToSell = orders.stream()
                .filter(o -> o.getStockSymbol().equals(order.getStockSymbol()))
                .findFirst()
                .orElse(null);

        if (orderToSell != null) {
            int remainingQuantity = orderToSell.getQuantity() - order.getQuantity();
            if (remainingQuantity <= 0) {
                orders.remove(orderToSell); // Remove if fully sold
            } 
            orderToSell.setQuantity(remainingQuantity);
            addToOrderHistory(order, userId);
            orderDatabase.put(userId, orders); 
        }

        // executorService.execute(() -> {
        // StockModel stock = findStockBySymbol(order.getStockSymbol());
        // if (stock != null && order.getQuantity() > 0) {
        // stock.setPrice(stock.getPrice() - 1); // Simulate stock price decrease after
        // selling
        // orderCount.incrementAndGet();
        // System.out.println("Sell Order placed for " + order.getQuantity() + " shares
        // of " + stock.getSymbol());
        // }
        // });
    }

    // Retrieve user orders
    public List<OrderModel> getUserOrders(String userId) {
        System.out.println(orderDatabase.getOrDefault(userId, new ArrayList<>()));
        return orderDatabase.getOrDefault(userId, new ArrayList<>());
    }

    // Retrieve order history
    public List<OrderModel> getUserOrderHistory(String userId) {
        List<OrderModel> history = userHistoryDatabase.getOrDefault(userId, new ArrayList<>());
        for (OrderModel temp : history) {
            System.out.println(temp.toString());
        }
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
