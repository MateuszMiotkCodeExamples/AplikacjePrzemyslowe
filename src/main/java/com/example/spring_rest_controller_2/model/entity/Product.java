package com.example.spring_rest_controller_2.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Product {
    private Long id;

    @NotBlank(message = "Nazwa produktu jest wymagana")
    @Size(min = 2, max = 100, message = "Nazwa musi mieć od 2 do 100 znaków")
    private String name;

    @NotNull(message = "Cena jest wymagana")
    @DecimalMin(value = "0.01", message = "Cena musi być większa niż 0")
    private BigDecimal price;

    @Size(max = 500, message = "Opis nie może być dłuższy niż 500 znaków")
    private String description;
    private String imagePath; // ścieżka do zdjęcia
}