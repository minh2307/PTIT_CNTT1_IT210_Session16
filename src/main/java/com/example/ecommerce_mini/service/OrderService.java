package com.example.ecommerce_mini.service;

import com.example.ecommerce_mini.model.CartItem;
import com.example.ecommerce_mini.model.Order;

import java.util.Collection;

public interface OrderService {
    void checkout(Order order, Collection<CartItem> cartItems);
}
