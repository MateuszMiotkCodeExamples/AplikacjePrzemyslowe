package com.example.spring_rest_controller_2.service;

import com.example.spring_rest_controller_2.exception.ResourceNotFoundException;
import com.example.spring_rest_controller_2.model.ProductModel;
import com.example.spring_rest_controller_2.model.entity.Category;
import com.example.spring_rest_controller_2.model.entity.Product;
import com.example.spring_rest_controller_2.model.entity.Review;
import com.example.spring_rest_controller_2.repository.CategoryRepository;
import com.example.spring_rest_controller_2.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        // Dodajemy przykładowe dane tylko jeśli baza jest pusta
        if (productRepository.count() == 0) {
            createSampleData();
        }
    }

    private void createSampleData() {
        try {
            // Tworzenie kategorii
            Category electronics = new Category();
            electronics.setName("Elektronika");
            categoryRepository.save(electronics);

            // Tworzenie produktu
            Product laptop = new Product();
            laptop.setName("Laptop Dell XPS 13");
            laptop.setPrice(new BigDecimal("4999.99"));
            laptop.setDescription("Nowoczesny laptop biznesowy");
            laptop.setCategory(electronics);
            productRepository.save(laptop);

            log.info("Utworzono przykładowe dane");
        } catch (Exception e) {
            log.error("Błąd podczas tworzenia przykładowych danych", e);
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produkt", "id", id));
    }

    @Transactional
    public Product save(Product product) {
        validateProduct(product);

        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategoria nie istnieje"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product addReview(Long productId, Review review) {
        Product product = getProduct(productId);
        review.setProduct(product);
        product.getReviews().add(review);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProduct(id);
        if (product.getImagePath() != null) {
            fileService.deleteFile(product.getImagePath());
        }
        productRepository.delete(product);
    }

    private void validateProduct(Product product) {
        List<String> errors = new ArrayList<>();

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.add("Nazwa produktu jest wymagana");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Cena musi być większa od zera");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }
}