package com.example.spring_rest_controller_2.repository;

import com.example.spring_rest_controller_2.model.entity.Category;
import com.example.spring_rest_controller_2.model.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAll();
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice AND p.category.id = :categoryId")
    List<Product> findByCategoryAndMaxPrice(@Param("categoryId") Long categoryId,
                                            @Param("maxPrice") BigDecimal maxPrice);
}
