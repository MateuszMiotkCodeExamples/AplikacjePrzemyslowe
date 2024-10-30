package com.example.spring_rest_controller_2.service;

import com.example.spring_rest_controller_2.model.ProductModel;
import com.example.spring_rest_controller_2.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    private final ProductModel productModel;

    @Autowired
    public ProductService(ProductModel productModel) {
        this.productModel = productModel;
    }

    public List<Product> getAllProducts() {
        return productModel.findAll();
    }

    public Product getProductById(Long id) {
        return productModel.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product addProduct(Product product) {
        return productModel.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        if (!productModel.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        updatedProduct.setId(id);
        return productModel.save(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productModel.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
