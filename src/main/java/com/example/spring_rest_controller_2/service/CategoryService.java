package com.example.spring_rest_controller_2.service;

import com.example.spring_rest_controller_2.exception.ResourceNotFoundException;
import com.example.spring_rest_controller_2.model.entity.Category;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CategoryService {
    private final List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void init() {
        // Dodajemy przykładowe kategorie
        Category category = new Category();
        category.setId(nextId++);
        category.setName("Elektronika");
        categories.add(category);

        category = new Category();
        category.setId(nextId++);
        category.setName("Książki");
        categories.add(category);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public Category getCategory(Long id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Kategoria o ID " + id + " nie została znaleziona"));
    }

    public Category save(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ValidationException("Nazwa kategorii jest wymagana");
        }

        if (category.getId() == null) {
            category.setId(nextId++);
            categories.add(category);
            log.info("Dodano nową kategorię: {}", category.getName());
        } else {
            int index = getCategoryIndex(category.getId());
            if (index != -1) {
                categories.set(index, category);
                log.info("Zaktualizowano kategorię: {}", category.getName());
            }
        }
        return category;
    }

    private int getCategoryIndex(Long id) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
