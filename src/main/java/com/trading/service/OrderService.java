package com.trading.service;

import com.trading.controller.PerformanceController;
import com.trading.model.*;
import com.trading.repository.*;
import com.trading.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.trading.proto.PerformanceServiceGrpc;
import com.trading.proto.Performance.PerformanceRequest;
import com.trading.proto.Performance.PerformanceResponse;

import java.util.Optional;
import java.sql.Timestamp;
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
    private PerformanceController performanceController; // Use PerformanceController to access the SSE emitter

    @Autowired
    private PerformanceMetricsService metricsService;

    private ConcurrentHashMap<String, List<OrderModel>> orderCache = new ConcurrentHashMap<>();

    // ExecutorService for managing threads
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final ManagedChannel channel;
    private final PerformanceServiceGrpc.PerformanceServiceBlockingStub performanceStub;

    public OrderService() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.performanceStub = PerformanceServiceGrpc.newBlockingStub(channel);
    }

    public void sendPerformanceMetrics() {
        PerformanceRequest request = PerformanceRequest.newBuilder()
                .setServiceName("OrderService")
                .build();

        try {
            PerformanceResponse response = performanceStub.getPerformance(request);
            System.out.println("Received performance metrics via gRPC: " + response +  " \n Timestamp:" + System.currentTimeMillis());

            // Convert gRPC response to PerformanceMetrics
            PerformanceMetrics metrics = new PerformanceMetrics();
            metrics.setTotalThreads(response.getTotalThreads());
            metrics.setRunningThreads(response.getRunningThreads());
            metrics.setPeakThreads(response.getPeakThreads());
            metrics.setHeapMemoryUsage(response.getHeapMemoryUsage());
            metrics.setNonHeapMemoryUsage(response.getNonHeapMemoryUsage());
            metrics.setSystemLoad(response.getSystemLoad());
            metrics.setTimestamp(new Timestamp(System.currentTimeMillis()));

            // Send order-triggered metrics via SSE
            performanceController.broadcastMetrics(metrics);

        } catch (Exception e) {
            System.err.println("Error sending performance metrics via gRPC: " + e.getMessage());
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
                    stock.setLowPrice(stock.getPrice());
                    stock.setHighPrice(stock.getPrice());
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

                    // Trigger performance metrics
                    sendPerformanceMetrics();

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
                    stock.setLowPrice(stock.getPrice());
                    stock.setHighPrice(stock.getPrice());
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

                        // Trigger performance metrics
                        sendPerformanceMetrics();

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
