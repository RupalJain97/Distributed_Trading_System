package com.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.trading.model.UserHoldingsModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHoldingsRepository extends JpaRepository<UserHoldingsModel, Long> {

    @Query("SELECT o FROM UserHoldingsModel o WHERE o.userId = :userId ORDER BY o.companyName")
    List<UserHoldingsModel> findByUserId(String userId);

    Optional<UserHoldingsModel> findByUserIdAndStockSymbol(String userId, String stockSymbol);

    @Query("SELECT COUNT(DISTINCT o.stockSymbol) FROM UserHoldingsModel o WHERE o.userId = :userId")
    int countDistinctCompaniesByUserId(@Param("userId") String userId);
}
