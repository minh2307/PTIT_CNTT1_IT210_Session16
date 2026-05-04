package com.example.ecommerce_mini.repository;

import com.example.ecommerce_mini.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.status NOT IN ('CANCELLED')")
    Double findTotalRevenue();
    
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findLatestOrders();
}
