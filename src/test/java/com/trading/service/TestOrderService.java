package com.trading.service;

import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.model.UserHoldingsModel;
import com.trading.model.UserModel;
import com.trading.repository.OrderRepository;
import com.trading.repository.StockRepository;
import com.trading.repository.UserHoldingsRepository;
import com.trading.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("test") // Activate this service only in the 'test' profile
public class TestOrderService {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserHoldingsRepository userHoldingsRepository;

    @Autowired
    private UserRepository userRepository;

    private ConcurrentHashMap<String, List<OrderModel>> orderCache = new ConcurrentHashMap<>();

    public void placeBuyOrder(OrderModel order, String userId) {
        if (!isUserExists(userId)) {
            throw new RuntimeException("User does not exist. Cannot place order.");
        }
        synchronized (this) {
            StockModel stock = stockRepository.findBySymbol(order.getStockSymbol());
            order.setCompanyName(stock.getCompanyName());
            order.setOrderDate();

            if (stock != null) {
                if (stock.getQuantity() < order.getQuantity()) {
                    throw new RuntimeException("Insufficient stock quantity.");
                }

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
            } else {
                throw new RuntimeException("Stock not available, cannot buy stock.");
            }
        }
    }

    public void placeSellOrder(OrderModel order, String userId) {
        if (!isUserExists(userId)) {
            throw new RuntimeException("User does not exist. Cannot place order.");
        }
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
            } else {
                throw new RuntimeException("Cannot sell stock user does not own.");
            }

            if (userHoldingOpt.isPresent()) {
                UserHoldingsModel userHolding = userHoldingOpt.get();
                if (userHolding.getCurrentQuantity() < order.getQuantity()) {
                    throw new RuntimeException("Cannot sell more than current holdings.");
                }

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
    }

    private boolean isUserExists(String userId) {
        UserModel user = userRepository.findByUserid(userId);
        return user != null;
    }

    public List<UserHoldingsModel> getUserStocksByUserId(String userId) {
        return userHoldingsRepository.findByUserId(userId);
    }
}
