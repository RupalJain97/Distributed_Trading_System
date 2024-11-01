package com.trading.service;

import com.trading.model.StockModel;
import com.trading.repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class StockService {

    private final Map<String, StockModel> stockCache = new ConcurrentHashMap<>(); // For real-time stock updates

    @Autowired
    private StockRepository stockRepository;

    public List<StockModel> getAvailableStocks() {
        List<String> allSymbols = stockRepository.findAllSymbols();
        List<StockModel> stocksToFetch = new ArrayList<>();
        List<StockModel> availableStocks = new ArrayList<>();

        // Check each symbol to see if it's in the cache
        for (String symbol : allSymbols) {
            StockModel cachedStock = stockCache.get(symbol);
            if (cachedStock != null) {
                // If found in cache, add to the available stocks list
                availableStocks.add(cachedStock);
            } else {
                // If not found in cache, add symbol to fetch list
                stocksToFetch.add(stockRepository.findBySymbol(symbol));
            }
        }

        // Add newly fetched stocks to the cache and available stocks list
        for (StockModel stock : stocksToFetch) {
            stockCache.put(stock.getSymbol(), stock);
            availableStocks.add(stock);
        }

        return availableStocks;
    }

    public StockModel findStockBySymbol(String stockSymbol) {
        StockModel cachedStock = stockCache.get(stockSymbol);
        if (cachedStock != null) {
            return cachedStock; // Return the cached stock if found
        }

        // If not found in cache, retrieve it from the database
        StockModel stockFromDb = stockRepository.findBySymbol(stockSymbol);

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

            } else {
                // Add new stock to the cache if not found in DB
                stockCache.put(stock.getSymbol(), stock);
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Clear cache every 1 minute
    public void clearStockCache() {
        stockCache.clear();
        System.out.println("Stock cache cleared");
    }
}
