package com.example.ecommerce_mini.controller;

import com.example.ecommerce_mini.dto.CartItem;
import com.example.ecommerce_mini.model.Order;
import com.example.ecommerce_mini.service.CartService;
import com.example.ecommerce_mini.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    
    private final CartService cartService;
    private final OrderService orderService;
    
    @GetMapping
    public String showCheckoutForm(Model model, RedirectAttributes redirectAttributes) {
        if (cartService.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống! Vui lòng thêm sản phẩm vào giỏ hàng.");
            return "redirect:/cart";
        }
        
        if (!cartService.hasEnoughStock()) {
            List<String> errors = cartService.getStockErrors();
            redirectAttributes.addFlashAttribute("error", String.join("; ", errors));
            return "redirect:/cart";
        }
        
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalAmount", cartService.getTotalAmount());
        model.addAttribute("totalItems", cartService.getTotalItems());
        model.addAttribute("checkoutForm", new CheckoutForm());
        
        return "checkout";
    }
    
    @PostMapping
    public String processCheckout(@Valid @ModelAttribute CheckoutForm checkoutForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        // Check cart again in case it was modified
        if (cartService.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống! Vui lòng thêm sản phẩm vào giỏ hàng.");
            return "redirect:/cart";
        }
        
        if (!cartService.hasEnoughStock()) {
            List<String> errors = cartService.getStockErrors();
            redirectAttributes.addFlashAttribute("error", String.join("; ", errors));
            return "redirect:/cart";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("cartItems", cartService.getCartItems());
            model.addAttribute("totalAmount", cartService.getTotalAmount());
            model.addAttribute("totalItems", cartService.getTotalItems());
            return "checkout";
        }
        
        try {
            Order order = orderService.createOrder(
                checkoutForm.getCustomerName(),
                checkoutForm.getCustomerEmail(),
                checkoutForm.getCustomerPhone(),
                checkoutForm.getShippingAddress(),
                checkoutForm.getPaymentMethod(),
                checkoutForm.getNotes()
            );
            
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công! Mã đơn hàng: #" + order.getId());
            return "redirect:/order-success/" + order.getId();
            
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }
    
    @GetMapping("/order-success/{orderId}")
    public String showOrderSuccess(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order-success";
    }
    
    public static class CheckoutForm {
        
        @NotBlank(message = "Vui lòng nhập họ tên")
        private String customerName;
        
        @NotBlank(message = "Vui lòng nhập email")
        @Email(message = "Email không hợp lệ")
        private String customerEmail;
        
        @NotBlank(message = "Vui lòng nhập số điện thoại")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
        private String customerPhone;
        
        @NotBlank(message = "Vui lòng nhập địa chỉ giao hàng")
        private String shippingAddress;
        
        @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
        private String paymentMethod;
        
        private String notes;
        
        // Getters and Setters
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
