package com.example.ecommerce_mini.repository;

import com.example.ecommerce_mini.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
    @Query("SELECT od.product.id, od.product.name, SUM(od.quantity) as totalSold " +
           "FROM OrderDetail od " +
           "JOIN od.order o " +
           "WHERE o.status NOT IN ('CANCELLED') " +
           "GROUP BY od.product.id, od.product.name " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();
    
    @Query(value = "SELECT od.product_id, od.product_name, SUM(od.quantity) as totalSold " +
           "FROM order_details od " +
           "JOIN orders o ON od.order_id = o.id " +
           "WHERE o.status NOT IN ('CANCELLED') " +
           "GROUP BY od.product_id, od.product_name " +
           "ORDER BY totalSold DESC " +
           "LIMIT :limit", nativeQuery = true)
    List<Object[]> findTopSellingProductsLimit(int limit);
}
