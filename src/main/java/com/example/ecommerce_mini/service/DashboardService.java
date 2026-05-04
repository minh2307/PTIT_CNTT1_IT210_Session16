package com.example.ecommerce_mini.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    
    Map<String, Object> getDashboardStatistics();
    
    List<Object[]> getTopSellingProducts(int limit);
}
