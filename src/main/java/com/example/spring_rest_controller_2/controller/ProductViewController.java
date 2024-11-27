package com.example.spring_rest_controller_2.controller;

import com.example.spring_rest_controller_2.exception.ResourceNotFoundException;
import com.example.spring_rest_controller_2.exception.StorageException;
import com.example.spring_rest_controller_2.model.ProductDTO;
import com.example.spring_rest_controller_2.model.entity.Category;
import com.example.spring_rest_controller_2.model.entity.Review;
import com.example.spring_rest_controller_2.service.CategoryService;
import com.example.spring_rest_controller_2.service.FileService;
import com.example.spring_rest_controller_2.service.ProductService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.spring_rest_controller_2.model.entity.Product;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
@Slf4j
public class ProductViewController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileService fileService;

    @Autowired
    public ProductViewController(ProductService productService,
                                 CategoryService categoryService,
                                 FileService fileService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.fileService = fileService;
    }

    // Wyświetlanie listy produktów wraz z formularzem dodawania
    @GetMapping
    public String listProducts(Model model) {
        try {
            log.info("Pobieranie listy wszystkich produktów");
            List<Product> products = productService.getAllProducts();
            List<Category> categories = categoryService.getAllCategories();

            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("productDTO", new ProductDTO());
            return "products/list";

        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy produktów", e);
            model.addAttribute("errorMessage", "Wystąpił błąd podczas ładowania strony");
            return "error";
        }
    }

    // Wyświetlanie szczegółów produktu
    @GetMapping("/details/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        try {
            log.info("Pobieranie szczegółów produktu o ID: {}", id);
            Product product = productService.getProduct(id);
            model.addAttribute("product", product);
            model.addAttribute("review", new Review()); // dla formularza dodawania opinii
            return "products/details";

        } catch (ResourceNotFoundException e) {
            log.warn("Nie znaleziono produktu o ID: {}", id);
            model.addAttribute("errorMessage", "Produkt nie został znaleziony");
            return "error";
        } catch (Exception e) {
            log.error("Błąd podczas pobierania szczegółów produktu", e);
            model.addAttribute("errorMessage", "Wystąpił błąd podczas ładowania szczegółów");
            return "error";
        }
    }

    // Dodawanie nowego produktu
    @PostMapping
    public String addProduct(@ModelAttribute @Valid ProductDTO productDTO,
                             BindingResult result,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             RedirectAttributes redirectAttributes) {
        try {
            // Sprawdzenie błędów walidacji
            if (result.hasErrors()) {
                log.warn("Błędy walidacji podczas dodawania produktu");
                redirectAttributes.addFlashAttribute("errorMessage", "Popraw błędy w formularzu");
                return "redirect:/products";
            }

            // Tworzenie i konfiguracja nowego produktu
            Product product = new Product();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setDescription(productDTO.getDescription());

            // Ustawianie kategorii, jeśli została wybrana
            if (productDTO.getCategoryId() != null) {
                Category category = categoryService.getCategory(productDTO.getCategoryId());
                product.setCategory(category);
            }

            // Obsługa przesłanego zdjęcia
            if (image != null && !image.isEmpty()) {
                String fileName = fileService.saveFile(image);
                product.setImagePath(fileName);
                log.info("Zapisano zdjęcie produktu: {}", fileName);
            }

            // Zapisanie produktu
            productService.save(product);
            log.info("Dodano nowy produkt: {}", product.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Produkt został dodany pomyślnie");

        } catch (ValidationException e) {
            log.warn("Błąd walidacji: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (StorageException e) {
            log.error("Błąd podczas zapisywania pliku", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się zapisać zdjęcia produktu");
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas dodawania produktu", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas dodawania produktu");
        }

        return "redirect:/products";
    }

    // Dodawanie opinii do produktu
    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Long id,
                            @Valid @ModelAttribute Review review,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nieprawidłowa treść opinii");
                return "redirect:/products/details/" + id;
            }

            productService.addReview(id, review);
            log.info("Dodano opinię do produktu o ID: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Opinia została dodana");

        } catch (ResourceNotFoundException e) {
            log.warn("Próba dodania opinii do nieistniejącego produktu: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Produkt nie został znaleziony");
        } catch (Exception e) {
            log.error("Błąd podczas dodawania opinii", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Nie udało się dodać opinii");
        }

        return "redirect:/products/details/" + id;
    }

    // Obsługa wyświetlania zdjęć
    @GetMapping("/image/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            Resource file = fileService.loadFileAsResource(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_JPEG) // można dodać określanie typu na podstawie rozszerzenia
                    .body(file);

        } catch (StorageException e) {
            log.warn("Nie znaleziono pliku: {}", fileName);
            return ResponseEntity.notFound().build();
        }
    }

    // Obsługa błędów walidacji
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        model.addAttribute("validationErrors", errors);
        return "products/list";
    }
}
