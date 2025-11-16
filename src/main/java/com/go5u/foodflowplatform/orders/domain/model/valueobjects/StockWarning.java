package com.go5u.foodflowplatform.orders.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockWarning {
    private String ingredientName;
    private String warningLevel; // "OUT_OF_STOCK", "LOW_STOCK", "CRITICAL"
    private Integer availableQuantity;
    private Double requiredQuantity;
    private String message;

    public static StockWarning outOfStock(String ingredientName, Double required) {
        return new StockWarning(
                ingredientName,
                "OUT_OF_STOCK",
                0,
                required,
                String.format("The inventory has no %s. Required: %.2f units", ingredientName, required)
        );
    }

    public static StockWarning lowStock(String ingredientName, Integer available, Double required) {
        return new StockWarning(
                ingredientName,
                "LOW_STOCK",
                available,
                required,
                String.format("The inventory doesn't have enough %s. Available: %d, Required: %.2f", 
                        ingredientName, available, required)
        );
    }

    public static StockWarning critical(String ingredientName, Integer available) {
        return new StockWarning(
                ingredientName,
                "CRITICAL",
                available,
                null,
                String.format("The inventory of %s is critically low. Only %d units left", 
                        ingredientName, available)
        );
    }
}

