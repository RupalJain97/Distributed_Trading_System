package com.trading.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trading.model.OrderModel;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long>{

    @Query("SELECT o FROM OrderModel o WHERE o.userId = :userId ORDER BY o.orderDate DESC")
    List<OrderModel> findAllByUserid(String userId);
}
