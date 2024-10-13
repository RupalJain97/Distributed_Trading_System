package com.trading.repository;

import com.trading.model.StockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockModel, Long> {
    StockModel findBySymbol(String symbol);  // Find stock by symbol
}
