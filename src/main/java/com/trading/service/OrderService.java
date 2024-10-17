package com.trading.service;

import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.model.UserHoldingsModel;
import com.trading.repository.OrderRepository;
import com.trading.repository.StockRepository;
import com.trading.repository.UserHoldingsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserHoldingsRepository userHoldingsRepository;

    private ConcurrentHashMap<String, List<OrderModel>> orderCache = new ConcurrentHashMap<>();

    // ExecutorService for managing threads
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void placeBuyOrder(OrderModel order, String userId) {
        executorService.execute(() -> {
            synchronized (this) {
                StockModel stock = stockRepository.findBySymbol(order.getStockSymbol());
                order.setCompanyName(stock.getCompanyName());
                order.setOrderDate();

                // System.out.println("Order received: " + order);
                if (stock != null) {
                    stock.setQuantity(stock.getQuantity() - order.getQuantity());
                    stock.setPrice(stock.getPrice() + 1); // Simulate stock price increase after buy
                    stockService.updateStock(stock);

                    Optional<UserHoldingsModel> userHoldingOpt = userHoldingsRepository
                            .findByUserIdAndStockSymbol(userId, order.getStockSymbol());

                    UserHoldingsModel userHolding = userHoldingOpt.orElse(new UserHoldingsModel(null, userId,
                            order.getStockSymbol(), stock.getCompanyName(), 0, 0, order.getOrderDate()));

                    userHolding.setCurrentQuantity(userHolding.getCurrentQuantity() + order.getQuantity());
                    userHolding.setLastPrice(stock.getPrice());
                    userHolding.setLastOrderDate(order.getOrderDate());

                    userHoldingsRepository.save(userHolding);

                    orderRepository.save(order);
                    orderCache.computeIfAbsent(userId, k -> new ArrayList<>()).add(order);

                    // System.out.println(
                    //         "Buy Order placed for " + order.getQuantity() + " shares of " + order.getStockSymbol());
                }

            }
        });
    }

    public void placeSellOrder(OrderModel order, String userId) {
        executorService.execute(() -> {
            synchronized (this) {
                StockModel stock = stockRepository.findBySymbol(order.getStockSymbol());
                order.setCompanyName(stock.getCompanyName());
                order.setOrderDate();

                Optional<UserHoldingsModel> userHoldingOpt = userHoldingsRepository.findByUserIdAndStockSymbol(userId,
                        order.getStockSymbol());

                if (stock != null) {
                    stock.setQuantity(stock.getQuantity() + order.getQuantity());
                    stock.setPrice(stock.getPrice() - 1); // Simulate price drop after selling
                    stockService.updateStock(stock);
                }
                if (userHoldingOpt.isPresent()) {
                    UserHoldingsModel userHolding = userHoldingOpt.get();
                    int remainingQuantity = userHolding.getCurrentQuantity() - order.getQuantity();
                    if (remainingQuantity <= 0) {
                        userHoldingsRepository.delete(userHolding);
                    } else {
                        userHolding.setCurrentQuantity(remainingQuantity);
                        userHolding.setLastPrice(stockRepository.findBySymbol(order.getStockSymbol()).getPrice());
                        userHolding.setLastOrderDate(order.getOrderDate());
                        userHoldingsRepository.save(userHolding);
                    }
                    orderRepository.save(order);
                    orderCache.computeIfAbsent(userId, k -> new ArrayList<>()).add(order);
                } else {
                    throw new RuntimeException("Cannot sell stock user does not own.");
                }
            }
        });

    }

    // Scheduled task that runs every minute to persist cached orders (optional)
    @Scheduled(fixedRate = 60000) // 1 minute
    public void updateOrderHistory() {
        for (Entry<String, List<OrderModel>> entry : orderCache.entrySet()) {
            List<OrderModel> orders = entry.getValue();
            orderRepository.saveAll(orders);
            entry.getValue().clear(); // Clear the cache after saving

        }
    }

    public List<UserHoldingsModel> getUserStocksByUserId(String userId) {
        return userHoldingsRepository.findByUserId(userId);
    }

    public List<OrderModel> getOrderHistoryByUserId(String userId) {
        return orderRepository.findAllByUserid(userId);
    }

    public int getOrderCount(String userId) {
        return userHoldingsRepository.countDistinctCompaniesByUserId(userId);
    }
}
