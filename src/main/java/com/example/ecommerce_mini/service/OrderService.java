package com.example.ecommerce_mini.service;

import com.example.ecommerce_mini.dto.CartItem;
import com.example.ecommerce_mini.model.Order;
import com.example.ecommerce_mini.model.OrderDetail;
import com.example.ecommerce_mini.model.Product;
import com.example.ecommerce_mini.repository.OrderDetailRepository;
import com.example.ecommerce_mini.repository.OrderRepository;
import com.example.ecommerce_mini.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    
    @Transactional
    public Order createOrder(String customerName, String customerEmail, 
                           String customerPhone, String shippingAddress, 
                           String paymentMethod, String notes) {
        
        // Validate cart is not empty
        if (cartService.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể đặt hàng!");
        }
        
        // Check stock availability
        if (!cartService.hasEnoughStock()) {
            List<String> errors = cartService.getStockErrors();
            throw new IllegalStateException(String.join("; ", errors));
        }
        
        // Create order
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setNotes(notes);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(cartService.getTotalAmount().doubleValue());
        
        // Save order first
        order = orderRepository.save(order);
        
        // Create order details and update stock
        List<CartItem> cartItems = cartService.getCartItems();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            // Create order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetailRepository.save(orderDetail);
            
            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Clear cart after successful order
        cartService.clearCart();
        
        return order;
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại!"));
    }
    
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByOrderDateDesc(email);
    }
    
    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Đơn hàng đã bị hủy!");
        }
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã giao!");
        }
        
        // Restore stock for cancelled order
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            product.setStockQuantity(product.getStockQuantity() + detail.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
