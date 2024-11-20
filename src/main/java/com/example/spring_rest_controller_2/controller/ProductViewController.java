package com.example.spring_rest_controller_2.controller;

import com.example.spring_rest_controller_2.service.FileService;
import com.example.spring_rest_controller_2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.spring_rest_controller_2.model.entity.Product;

import java.io.IOException;

@Controller
@RequestMapping("/products")
public class ProductViewController {
    private final ProductService productService;
    private final FileService fileService;

    @Autowired
    public ProductViewController(ProductService productService, FileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    @GetMapping
    public String listProducts(Model model) {
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }



    @PostMapping
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String fileName = fileService.saveFile(image);
            product.setImagePath(fileName);
        }
        productService.addProduct(product);
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

    @GetMapping("/image/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        Resource file = fileService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

}
