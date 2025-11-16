package com.go5u.foodflowplatform.orders.interfaces.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de una orden
 */
public record OrderResponse(
        Long id,
        Integer tableNumber,
        List<OrderItemResponse> items,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        List<StockWarningResponse> stockWarnings // Stock warnings for this order
) {
    /**
     * DTO para un item de la orden
     */
    public record OrderItemResponse(
            Long dishId,
            String dishName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal finalPrice
    ) {}

    /**
     * DTO for stock warnings
     */
    public record StockWarningResponse(
            String ingredientName,
            String warningLevel,
            Integer availableQuantity,
            Double requiredQuantity,
            String message
    ) {}
}

