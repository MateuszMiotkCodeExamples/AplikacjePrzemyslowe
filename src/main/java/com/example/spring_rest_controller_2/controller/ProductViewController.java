package com.example.spring_rest_controller_2.controller;

import com.example.spring_rest_controller_2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.spring_rest_controller_2.model.entity.Product;

@Controller
@RequestMapping("/products")
public class ProductViewController {
    private final ProductService productService;

    @Autowired
    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute Product product,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("products", productService.getAllProducts());
            return "products/list";
        }

        try {
            System.out.println(product.toString());
            productService.addProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produkt został dodany pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się dodać produktu");
            redirectAttributes.addFlashAttribute("product", product);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.product", result);
        }
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produkt został usunięty");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie można usunąć produktu");
        }
        return "redirect:/products";
    }

    @GetMapping("/details/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/details";
    }

}
