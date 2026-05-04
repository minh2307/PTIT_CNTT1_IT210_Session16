package com.example.ecommerce_mini.controller;

import com.example.ecommerce_mini.model.Category;
import com.example.ecommerce_mini.repository.CategoryRepository;
import com.example.ecommerce_mini.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class    CategoryController {
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/category-list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }
    
    @PostMapping("/add")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoryRepository.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    return "admin/category-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
                    return "redirect:/admin/categories";
                });
    }
    
    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            category.setId(id);
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return categoryRepository.findById(id)
                .map(category -> {
                    // Kiểm tra xem có sản phẩm nào trong danh mục này không
                    long productCount = productRepository.countByCategoryId(id);
                    if (productCount > 0) {
                        redirectAttributes.addFlashAttribute("error", 
                            "Không thể xóa danh mục '" + category.getName() + "' vì còn " + productCount + " sản phẩm!");
                        return "redirect:/admin/categories";
                    }
                    
                    try {
                        categoryRepository.delete(category);
                        redirectAttributes.addFlashAttribute("success", "Danh mục đã được xóa thành công!");
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
                    }
                    return "redirect:/admin/categories";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
                    return "redirect:/admin/categories";
                });
    }
}
