package com.trading.service;

import com.trading.model.StockModel;
import com.trading.repository.StockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<StockModel> getAvailableStocks() {
        return stockRepository.findAll();
    }

    public StockModel findStockBySymbol(String stockSymbol) {
        return stockRepository.findBySymbol(stockSymbol); 
    }
}
