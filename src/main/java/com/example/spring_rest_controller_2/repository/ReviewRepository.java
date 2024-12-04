package com.example.spring_rest_controller_2.repository;

import com.example.spring_rest_controller_2.model.entity.Product;
import com.example.spring_rest_controller_2.model.entity.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findByProductOrderByIdDesc(Product product);
}
