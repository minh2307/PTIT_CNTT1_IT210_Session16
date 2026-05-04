package com.example.ecommerce_mini.controller;

import com.example.ecommerce_mini.model.Product;
import com.example.ecommerce_mini.repository.CategoryRepository;
import com.example.ecommerce_mini.repository.ProductRepository;
import com.example.ecommerce_mini.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }
    
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Specification<Product> spec = ProductSpecification.withFilters(name, categoryId, minPrice, maxPrice)
                .and(ProductSpecification.hasStock());
        
        Page<Product> products = productRepository.findAll(spec, pageable);
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        
        // Preserve search parameters for pagination
        model.addAttribute("name", name);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        
        return "home";
    }
    
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        return productRepository.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    
                    // Get related products from same category
                    if (product.getCategory() != null) {
                        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdAt"));
                        Page<Product> relatedProducts = productRepository.findByCategoryId(
                            product.getCategory().getId(), pageable);
                        model.addAttribute("relatedProducts", relatedProducts.getContent());
                    }
                    
                    return "product-detail";
                })
                .orElse("redirect:/products");
    }
    
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(query, pageable);
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("searchQuery", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        
        return "home";
    }
}
