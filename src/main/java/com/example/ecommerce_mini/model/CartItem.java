package com.example.ecommerce_mini.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItem {
    private Long productId;
    private String name;
    private String imageUrl;
    private Double price;
    private Integer quantity;
}
