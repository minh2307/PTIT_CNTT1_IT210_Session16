package com.example.ecommerce_mini.controller;

import com.example.ecommerce_mini.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> statistics = dashboardService.getDashboardStatistics();
        model.addAttribute("statistics", statistics);
        
        List<Object[]> topSellingProducts = dashboardService.getTopSellingProducts(5);
        model.addAttribute("topSellingProducts", topSellingProducts);
        
        return "admin/dashboard";
    }
}
