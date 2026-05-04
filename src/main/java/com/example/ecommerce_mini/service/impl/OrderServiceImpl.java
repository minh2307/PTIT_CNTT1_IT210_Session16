package com.example.ecommerce_mini.service.impl;

import com.example.ecommerce_mini.model.CartItem;
import com.example.ecommerce_mini.model.Order;
import com.example.ecommerce_mini.model.OrderDetail;
import com.example.ecommerce_mini.model.Product;
import com.example.ecommerce_mini.repository.OrderDetailRepository;
import com.example.ecommerce_mini.repository.OrderRepository;
import com.example.ecommerce_mini.repository.ProductRepository;
import com.example.ecommerce_mini.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void checkout(Order order, Collection<CartItem> cartItems) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);

        double total = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        order.setTotalAmount(total);

        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng");
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());

            orderDetailRepository.save(detail);
        }
    }
}
