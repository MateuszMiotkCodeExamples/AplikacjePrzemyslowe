package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    @DisplayName("Dodawanie dwóch liczb dodatnich")
    void shouldAddTwoPositiveNumbers() {
        // Arrange
        int a = 5;
        int b = 3;
        
        // Act
        int result = calculator.add(a, b);
        
        // Assert
        assertEquals(8, result);
    }
    
    @Test
    void shouldSubtractNumbers() {
        assertEquals(2, calculator.subtract(5, 3));
    }
    
    @ParameterizedTest
    @CsvSource({
        "2, 3, 6",
        "5, 4, 20",
        "-2, 3, -6"
    })
    void shouldMultiplyNumbers(int a, int b, int expected) {
        assertEquals(expected, calculator.multiply(a, b));
    }
    
    @Test
    void shouldDivideNumbers() {
        assertEquals(2.5, calculator.divide(5, 2), 0.001);
    }
    
    @Test
    void shouldThrowExceptionWhenDividingByZero() {
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> calculator.divide(10, 0)
        );
        
        assertEquals("Division by zero", exception.getMessage());
    }
}
