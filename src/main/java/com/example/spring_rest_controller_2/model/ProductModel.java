package com.example.spring_rest_controller_2.model;

import com.example.spring_rest_controller_2.model.entity.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductModel {
    private final List<Product> products = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void init() {
        // Inicjalizacja przykładowych danych
        products.add(new Product() {{
            setId(nextId++);
            setName("Laptop");
            setPrice(new BigDecimal("3999.99"));
            setDescription("Laptop gamingowy");
        }});

        products.add(new Product() {{
            setId(nextId++);
            setName("Smartfon");
            setPrice(new BigDecimal("1999.99"));
            setDescription("Smartfon z 5G");
        }});
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            // Nowy produkt
            product.setId(nextId++);
            products.add(product);
        } else {
            // Aktualizacja istniejącego produktu
            updateExisting(product);
        }
        return product;
    }

    private void updateExisting(Product updatedProduct) {
        findById(updatedProduct.getId()).ifPresent(existing -> {
            existing.setName(updatedProduct.getName());
            existing.setPrice(updatedProduct.getPrice());
            existing.setDescription(updatedProduct.getDescription());
        });
    }

    public boolean delete(Long id) {
        return products.removeIf(p -> p.getId().equals(id));
    }

    public boolean exists(Long id) {
        return products.stream().anyMatch(p -> p.getId().equals(id));
    }
}