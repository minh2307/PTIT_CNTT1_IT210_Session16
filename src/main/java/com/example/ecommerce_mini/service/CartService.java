package com.example.ecommerce_mini.service;

import com.example.ecommerce_mini.dto.CartItem;
import com.example.ecommerce_mini.model.Product;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
@Data
public class CartService {
    
    private List<CartItem> cartItems = new ArrayList<>();
    
    public void addToCart(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        
        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng. Chỉ còn " + product.getStockQuantity() + " sản phẩm trong kho.");
        }
        
        Optional<CartItem> existingItem = findCartItemByProductId(product.getId());
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Không thể thêm " + quantity + " sản phẩm '" + product.getName() + "'. Tổng số lượng (" + newQuantity + ") vượt quá số lượng trong kho (" + product.getStockQuantity() + ").");
            }
            
            item.setQuantity(newQuantity);
        } else {
            cartItems.add(new CartItem(product, quantity));
        }
    }
    
    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }
        
        Optional<CartItem> item = findCartItemByProductId(productId);
        if (item.isPresent()) {
            CartItem cartItem = item.get();
            Product product = cartItem.getProduct();
            
            if (quantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng. Chỉ còn " + product.getStockQuantity() + " sản phẩm trong kho.");
            }
            
            cartItem.setQuantity(quantity);
        }
    }
    
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    
    public void clearCart() {
        cartItems.clear();
    }
    
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }
    
    public int getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public BigDecimal getTotalAmount() {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
    
    public int getCartSize() {
        return cartItems.size();
    }
    
    public Optional<CartItem> findCartItemByProductId(Long productId) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }
    
    public boolean hasEnoughStock() {
        return cartItems.stream().allMatch(CartItem::hasEnoughStock);
    }
    
    public List<String> getStockErrors() {
        List<String> errors = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (!item.hasEnoughStock()) {
                errors.add("Sản phẩm '" + item.getProduct().getName() + "' chỉ còn " + item.getProduct().getStockQuantity() + " sản phẩm trong kho.");
            }
        }
        return errors;
    }
}
