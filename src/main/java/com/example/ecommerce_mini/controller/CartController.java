package com.example.ecommerce_mini.controller;

import com.example.ecommerce_mini.dto.CartItem;
import com.example.ecommerce_mini.model.Product;
import com.example.ecommerce_mini.repository.ProductRepository;
import com.example.ecommerce_mini.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    private final ProductRepository productRepository;
    
    @GetMapping
    public String viewCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", cartService.getTotalAmount());
        model.addAttribute("totalItems", cartService.getTotalItems());
        model.addAttribute("isEmpty", cartService.isEmpty());
        return "cart";
    }
    
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, 
                          @RequestParam(defaultValue = "1") int quantity,
                          RedirectAttributes redirectAttributes) {
        
        return productRepository.findById(productId)
                .map(product -> {
                    try {
                        cartService.addToCart(product, quantity);
                        redirectAttributes.addFlashAttribute("success", 
                            "Đã thêm " + quantity + " sản phẩm '" + product.getName() + "' vào giỏ hàng!");
                    } catch (IllegalArgumentException e) {
                        redirectAttributes.addFlashAttribute("error", e.getMessage());
                    }
                    return "redirect:/products/" + productId;
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
                    return "redirect:/products";
                });
    }
    
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                               @RequestParam int quantity,
                               RedirectAttributes redirectAttributes) {
        
        try {
            cartService.updateQuantity(productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cập nhật giỏ hàng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, RedirectAttributes redirectAttributes) {
        
        cartService.findCartItemByProductId(productId)
                .ifPresentOrElse(item -> {
                    cartService.removeFromCart(productId);
                    redirectAttributes.addFlashAttribute("success", 
                        "Đã xóa sản phẩm '" + item.getProduct().getName() + "' khỏi giỏ hàng!");
                }, () -> {
                    redirectAttributes.addFlashAttribute("error", "Sản phẩm không có trong giỏ hàng!");
                });
        
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        int itemCount = cartService.getTotalItems();
        cartService.clearCart();
        redirectAttributes.addFlashAttribute("success", "Đã xóa " + itemCount + " sản phẩm khỏi giỏ hàng!");
        return "redirect:/cart";
    }
    
    @GetMapping("/count")
    @ResponseBody
    public int getCartItemCount() {
        return cartService.getTotalItems();
    }
    
    @GetMapping("/total")
    @ResponseBody
    public String getCartTotal() {
        return cartService.getTotalAmount().toString();
    }
}
