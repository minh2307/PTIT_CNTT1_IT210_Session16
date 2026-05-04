package com.example.ecommerce_mini.service;

import com.example.ecommerce_mini.model.CartItem;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

public interface CartService {
    Map<Long, CartItem> getCart(HttpSession session);
    void addToCart(HttpSession session, Long productId, int quantity);
    void updateQuantity(HttpSession session, Long productId, int quantity);
    void removeFromCart(HttpSession session, Long productId);
    void clearCart(HttpSession session);
    double getTotalAmount(HttpSession session);
    int getTotalQuantity(HttpSession session);
}
