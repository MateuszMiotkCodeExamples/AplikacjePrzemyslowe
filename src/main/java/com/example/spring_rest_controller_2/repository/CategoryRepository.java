package com.example.spring_rest_controller_2.repository;

import com.example.spring_rest_controller_2.model.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findAll();
    Optional<Category> findByNameIgnoreCase(String name);
}
