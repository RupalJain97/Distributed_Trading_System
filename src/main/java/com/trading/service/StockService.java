package com.trading.service;

import com.trading.model.StockModel;
import com.trading.repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class StockService {

    private final Map<String, StockModel> stockCache = new ConcurrentHashMap<>(); // For real-time stock updates

    @Autowired
    private StockRepository stockRepository;

    public List<StockModel> getAvailableStocks() {
        if (stockCache.isEmpty()) { // Cache is empty, load from DB and populate the cache
            List<StockModel> stocksFromDb = stockRepository.findAll();
            for (StockModel stock : stocksFromDb) {
                stockCache.put(stock.getSymbol(), stock);
            }
            return stocksFromDb;
        } else {
            return List.copyOf(stockCache.values()); // Return cached stocks
        }
    }

    public StockModel findStockBySymbol(String stockSymbol) {
        StockModel cachedStock = stockCache.get(stockSymbol);
        if (cachedStock != null) {
            System.out.println("Stock Found in cache...");
            return cachedStock; // Return the cached stock if found
        }

        // If not found in cache, retrieve it from the database
        StockModel stockFromDb = stockRepository.findBySymbol(stockSymbol);
        System.out.println("Stock Found in DB..." + stockFromDb);
        
        // Cache the stock for future requests
        if (stockFromDb != null) {
            stockCache.put(stockSymbol, stockFromDb);
        }

        return stockFromDb;
    }

    public void updateStock(StockModel stock) {
        synchronized (this) { // Ensure that stock updates happen in a thread-safe manner
            StockModel existingStock = stockRepository.findBySymbol(stock.getSymbol());
            if (existingStock != null) {
                existingStock.setPrice(stock.getPrice());
                existingStock.setQuantity(stock.getQuantity());
                stockRepository.save(existingStock);

                // Also update the cache after modifying the stock
                stockCache.put(stock.getSymbol(), existingStock);

                System.out.println("Stock Updated..." + stockCache.get(stock.getSymbol()));
            } else {
                // Add new stock to the cache if not found in DB
                stockCache.put(stock.getSymbol(), stock);
                System.out.println("New stock added to cache: " + stock.getSymbol() + " " + stock.getQuantity());
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Clear cache every 1 minute
    public void clearStockCache() {
        stockCache.clear();
        System.out.println("Stock cache cleared");
    }
}
