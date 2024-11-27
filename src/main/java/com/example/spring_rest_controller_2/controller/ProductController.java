//package com.example.spring_rest_controller_2.controller;
//
//import com.example.spring_rest_controller_2.model.entity.Product;
//import com.example.spring_rest_controller_2.service.ProductService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/products")
//public class ProductController {
//    private final ProductService productService;
//
//    @Autowired
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @GetMapping
//    public List<Product> getAllProducts() {
//        return productService.getAllProducts();
//    }
//
//    @GetMapping("/{id}")
//    public Product getProductById(@PathVariable Long id) {
//        return productService.getProductById(id);
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Product createProduct(@RequestBody Product product) {
//        return productService.addProduct(product);
//    }
//
//    @PutMapping("/{id}")
//    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
//        return productService.updateProduct(id, product);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteProduct(@PathVariable Long id) {
//        productService.deleteProduct(id);
//    }
//}
//
