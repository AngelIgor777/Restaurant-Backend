package org.test.restaurant_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductType;
import org.test.restaurant_service.repository.ProductTypeRepository;
import org.test.restaurant_service.service.impl.ProductServiceImpl;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    void parseRequest_ShouldReturnProduct_WhenValidInput() {
        // Arrange
        int typeId = 1;
        ProductType productType = new ProductType();
        when(productTypeRepository.findById(typeId)).thenReturn(Optional.of(productType));

        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("10.99");
        String cookingTime = "00:30";

        // Act
        Product result = productServiceImpl.parseRequest(name, description, typeId, price, cookingTime);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(productType, result.getType());
        assertEquals(price, result.getPrice());
        assertEquals(LocalTime.parse(cookingTime), result.getCookingTime());
    }

    @Test
    void parseRequest_ShouldThrowException_WhenProductTypeNotFound() {
        // Arrange
        int typeId = 99;
        when(productTypeRepository.findById(typeId)).thenReturn(Optional.empty());

        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("10.99");
        String cookingTime = "00:30";

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> productServiceImpl.parseRequest(name, description, typeId, price, cookingTime));
        assertTrue(exception.getMessage().contains("ProductType not found with id"));
    }

    @Test
    void parseRequest_ShouldThrowException_WhenCookingTimeIsInvalid() {
        // Arrange
        int typeId = 1;
        ProductType productType = new ProductType();
        when(productTypeRepository.findById(typeId)).thenReturn(Optional.of(productType));

        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("10.99");
        String cookingTime = "01:30"; // Invalid cooking time

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productServiceImpl.parseRequest(name, description, typeId, price, cookingTime));
        assertEquals("Cooking time is not valid", exception.getMessage());
    }
}
