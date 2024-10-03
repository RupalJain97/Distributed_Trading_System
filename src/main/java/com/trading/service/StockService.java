package com.trading.service;

import com.trading.model.StockModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {

    // Mock database for stock information
    private Map<String, StockModel> stockDatabase = new HashMap<>();

    // Constructor to initialize some stocks (this will later be replaced by actual database queries)
    public StockService() {
        stockDatabase.put("AAPL", new StockModel("AAPL", "Apple Inc.", 100, 150.50));
        stockDatabase.put("GOOGL", new StockModel("GOOGL", "Alphabet Inc.", 200, 2800.75));
        stockDatabase.put("AMZN", new StockModel("AMZN", "Amazon.com Inc.", 150, 3400.60));
        stockDatabase.put("TSLA", new StockModel("TSLA", "Tesla Inc.", 250, 740.90));
    }

    // Retrieve all stocks
    public List<StockModel> getAllStocks() {
        List<StockModel> stocks = new ArrayList<>();
        stocks.add(new StockModel("AAPL", "Apple Inc.", 100, 150.50));
        stocks.add(new StockModel("GOOGL", "Alphabet Inc.", 200, 2800.75));
        stocks.add(new StockModel("AMZN", "Amazon.com Inc.", 300, 3400.60));
        stocks.add(new StockModel("TSLA", "Tesla Inc.", 400, 740.90));
        return stocks;
    }

    // Retrieve available stocks (stocks that have quantity available)
    public List<StockModel> getAvailableStocks() {
        return new ArrayList<>(stockDatabase.values());
    }

    // Buy stocks - decrease available quantity for a specific stock
    public String buyStock(String userid, String stockSymbol, int quantity) {
        StockModel stock = stockDatabase.get(stockSymbol);
        if (stock != null) {
            if (stock.getQuantity() >= quantity) {
                stock.setAvailableQuantity(stock.getQuantity() - quantity);
                return "Successfully bought " + quantity + " shares of " + stockSymbol;
            } else {
                return "Insufficient stock available for " + stockSymbol;
            }
        } else {
            return "Stock " + stockSymbol + " not found.";
        }
    }
}
