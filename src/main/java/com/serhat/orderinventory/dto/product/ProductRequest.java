package com.serhat.orderinventory.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @Size(max = 1000, message = "Product description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}