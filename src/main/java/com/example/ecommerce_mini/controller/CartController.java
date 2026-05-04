package com.example.ecommerce_mini.controller;

import com.example.ecommerce_mini.model.CartItem;
import com.example.ecommerce_mini.model.Order;
import com.example.ecommerce_mini.service.CartService;
import com.example.ecommerce_mini.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Long, CartItem> cart = cartService.getCart(session);
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalAmount", cartService.getTotalAmount(session));
        model.addAttribute("totalQuantity", cartService.getTotalQuantity(session));
        
        // Prepare empty order for checkout form
        if (!model.containsAttribute("order")) {
            model.addAttribute("order", new Order());
        }
        
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        try {
            cartService.addToCart(session, productId, quantity);
        } catch (RuntimeException e) {
            // handle error if product not found
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        cartService.updateQuantity(session, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        cartService.removeFromCart(session, productId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@ModelAttribute Order order, HttpSession session, RedirectAttributes redirectAttributes) {
        Collection<CartItem> cartItems = cartService.getCart(session).values();
        
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống, không thể thanh toán!");
            return "redirect:/cart";
        }

        try {
            orderService.checkout(order, cartItems);
            cartService.clearCart(session);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công!");
            return "redirect:/cart";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("order", order); // keep user's input
            return "redirect:/cart";
        }
    }
}
