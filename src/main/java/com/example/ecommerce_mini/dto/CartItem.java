package com.example.ecommerce_mini.dto;

import com.example.ecommerce_mini.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    private Product product;
    private int quantity;
    
    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
    }
    
    public BigDecimal getSubtotal() {
        return BigDecimal.valueOf(product.getPrice() * quantity);
    }
    
    public void increaseQuantity() {
        this.quantity++;
    }
    
    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }
    
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }
    
    public boolean hasEnoughStock() {
        return quantity <= product.getStockQuantity();
    }
    
    public int getMaxQuantity() {
        return product.getStockQuantity();
    }
}
