package com.trading.repository;

import com.trading.model.StockModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockModel, Long> {
    StockModel findBySymbol(String symbol); // Find stock by symbol

    @Query("SELECT s.symbol FROM StockModel s")
    List<String> findAllSymbols();
}
