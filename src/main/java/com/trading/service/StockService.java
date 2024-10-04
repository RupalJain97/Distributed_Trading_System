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
    private static Map<String, StockModel> stockDatabase = new HashMap<>();

    // Constructor to initialize some stocks (this will later be replaced by actual
    // database queries)
    public StockService() {
        stockDatabase.put("AAPL", new StockModel("AAPL", "Apple Inc.", 100, 150.50));
        stockDatabase.put("GOOGL", new StockModel("GOOGL", "Alphabet Inc.", 200, 2800.75));
        stockDatabase.put("AMZN", new StockModel("AMZN", "Amazon.com Inc.", 150, 3400.60));
        stockDatabase.put("TSLA", new StockModel("TSLA", "Tesla Inc.", 250, 740.90));
    }

    // Retrieve available stocks (stocks that have quantity available)
    public List<StockModel> getAvailableStocks() {
        return new ArrayList<>(stockDatabase.values());
    }

    public static StockModel findStockBySymbol(String stockSymbol) {
        for (String stock : stockDatabase.keySet()) {
            if (stock.equals(stockSymbol)) {
                return stockDatabase.get(stock);
            }
        }
        return null; // Return null if stock not found
    }
}
