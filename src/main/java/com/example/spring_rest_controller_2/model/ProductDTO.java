package com.example.spring_rest_controller_2.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String name;
    private BigDecimal price;
    private String description;
    private Long categoryId;
}
