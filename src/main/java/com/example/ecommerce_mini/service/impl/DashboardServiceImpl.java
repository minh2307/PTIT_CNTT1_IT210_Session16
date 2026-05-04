package com.example.ecommerce_mini.service.impl;

import com.example.ecommerce_mini.repository.*;
import com.example.ecommerce_mini.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    
    @Override
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalRevenue", orderRepository.findTotalRevenue() != null ? orderRepository.findTotalRevenue() : 0.0);
        
        return stats;
    }
    
    @Override
    public List<Object[]> getTopSellingProducts(int limit) {
        return orderDetailRepository.findTopSellingProductsLimit(limit);
    }
}
