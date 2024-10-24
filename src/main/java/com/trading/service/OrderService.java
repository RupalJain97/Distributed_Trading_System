package com.trading.service;

import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.model.UserHoldingsModel;
import com.trading.model.UserModel;
import com.trading.repository.OrderRepository;
import com.trading.repository.StockRepository;
import com.trading.repository.UserHoldingsRepository;
import com.trading.repository.UserRepository;
import com.trading.service.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    @Autowired
    @Lazy
    private StockService stockService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserHoldingsRepository userHoldingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceService performanceService;

    private ConcurrentHashMap<String, List<OrderModel>> orderCache = new ConcurrentHashMap<>();

    // ExecutorService for managing threads
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void notifyDashboard(String message) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI("ws://localhost:8081/ws/updates");
            Session session = container.connectToServer(new SimpleWebSocketClient(message), uri);
            session.getAsyncRemote().sendText(message);
            session.close();
            System.out.println("Message sent to dashboard: " + message);
        } catch (Exception e) {
            System.err.println("WebSocket error: " + e.getMessage());
        }
    }

    public void placeBuyOrder(OrderModel order, String userId) {
        executorService.execute(() -> {
            synchronized (this) {
                if (!userService.isUserExists(userId)) {
                    throw new RuntimeException("User does not exist. Cannot place order.");
                }

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

                    // Collect performance metrics
                    String metrics = performanceService.getPerformanceMetrics();
                    System.out.println("Message: " + metrics);
                    notifyDashboard(metrics);

                } else {
                    throw new RuntimeException("Cannot Buy stock.");
                }
            }
        });
    }

    public void placeSellOrder(OrderModel order, String userId) {
        executorService.execute(() -> {
            synchronized (this) {
                if (!userService.isUserExists(userId)) {
                    throw new RuntimeException("User does not exist. Cannot place order.");
                }
                StockModel stock = stockRepository.findBySymbol(order.getStockSymbol());
                order.setCompanyName(stock.getCompanyName());
                order.setOrderDate();

                Optional<UserHoldingsModel> userHoldingOpt = userHoldingsRepository.findByUserIdAndStockSymbol(userId,
                        order.getStockSymbol());

                if (userHoldingOpt.isEmpty()) {
                    throw new RuntimeException("Cannot sell stock user does not own.");
                }

                if (stock != null) {
                    stock.setQuantity(stock.getQuantity() + order.getQuantity());
                    stock.setPrice(stock.getPrice() - 1); // Simulate price drop after selling
                    stockService.updateStock(stock);

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

                        String metrics = performanceService.getPerformanceMetrics();
                        System.out.println("Message: " + metrics);
                        notifyDashboard(metrics);

                    } else {
                        throw new RuntimeException("Cannot sell stock user does not own.");
                    }
                } else {
                    throw new RuntimeException("Stock is not available");
                }
            }
        });
    }

    // Scheduled task that runs every minute to persist cached orders
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
