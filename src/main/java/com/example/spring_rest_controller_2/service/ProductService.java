package com.example.spring_rest_controller_2.service;

import com.example.spring_rest_controller_2.exception.ResourceNotFoundException;
import com.example.spring_rest_controller_2.model.ProductModel;
import com.example.spring_rest_controller_2.model.entity.Category;
import com.example.spring_rest_controller_2.model.entity.Product;
import com.example.spring_rest_controller_2.model.entity.Review;
import jakarta.annotation.PostConstruct;
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
@Slf4j
public class ProductService {
    // Symulacja bazy danych w pamięci
    private final List<Product> products = new ArrayList<>();
    private final CategoryService categoryService;
    private Long nextId = 1L;

    @Autowired
    public ProductService(CategoryService categoryService) {
        this.categoryService = categoryService;
        initializeSampleData();
    }

    @PostConstruct
    private void initializeSampleData() {
        try {
            Category category = categoryService.getCategory(1L);

            Product product = new Product();
            product.setId(nextId++);
            product.setName("Laptop Dell XPS 13");
            product.setPrice(new BigDecimal("4999.99"));
            product.setDescription("Nowoczesny laptop biznesowy");
            product.setCategory(category);
            product.setReviews(new ArrayList<>());

            products.add(product);
            log.info("Zainicjalizowano przykładowe dane");
        } catch (Exception e) {
            log.error("Błąd podczas inicjalizacji przykładowych danych", e);
        }
    }

    // Metoda do pobierania wszystkich produktów używana w listProducts()
    public List<Product> getAllProducts() {
        log.debug("Pobieranie wszystkich produktów. Aktualna liczba: {}", products.size());
        return new ArrayList<>(products);
    }

    // Metoda do pobierania pojedynczego produktu używana w productDetails()
    public Product getProduct(Long id) {
        log.debug("Wyszukiwanie produktu o ID: {}", id);
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono produktu o ID: " + id));
    }

    // Metoda do zapisywania produktu używana w addProduct()
    public Product save(Product product) {
        validateProduct(product);

        if (product.getId() == null) {
            // Nowy produkt
            product.setId(nextId++);
            product.setReviews(new ArrayList<>()); // inicjalizacja listy recenzji
            products.add(product);
            log.info("Dodano nowy produkt: {}", product.getName());
        } else {
            // Aktualizacja istniejącego produktu
            int index = findProductIndex(product.getId());
            if (index != -1) {
                // Zachowujemy istniejące recenzje podczas aktualizacji
                List<Review> existingReviews = products.get(index).getReviews();
                product.setReviews(existingReviews);
                products.set(index, product);
                log.info("Zaktualizowano produkt: {}", product.getName());
            } else {
                throw new ResourceNotFoundException("Nie znaleziono produktu o ID: " + product.getId());
            }
        }
        return product;
    }

    // Metoda do dodawania recenzji używana w addReview()
    public Product addReview(Long productId, Review review) {
        log.debug("Dodawanie recenzji do produktu o ID: {}", productId);

        Product product = getProduct(productId);
        validateReview(review);

        // Ustawiamy podstawowe dane recenzji
        review.setId(System.nanoTime()); // Proste generowanie ID dla recenzji
        review.setProduct(product);

        // Dodajemy recenzję do produktu
        if (product.getReviews() == null) {
            product.setReviews(new ArrayList<>());
        }
        product.getReviews().add(review);
        log.info("Dodano recenzję do produktu: {}", product.getName());

        return product;
    }

    // Metody pomocnicze do walidacji
    private void validateProduct(Product product) {
        List<String> errors = new ArrayList<>();

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.add("Nazwa produktu jest wymagana");
        }

        if (product.getPrice() == null) {
            errors.add("Cena jest wymagana");
        } else if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Cena musi być większa od zera");
        }

        if (product.getCategory() != null && product.getCategory().getId() != null) {
            try {
                Category category = categoryService.getCategory(product.getCategory().getId());
                product.setCategory(category);
            } catch (ResourceNotFoundException e) {
                errors.add("Wybrana kategoria nie istnieje");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void validateReview(Review review) {
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new ValidationException("Treść recenzji jest wymagana");
        }
    }

    private int findProductIndex(Long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}