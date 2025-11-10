package com.go5u.foodflowplatform.orders.interfaces.dto;

/**
 * DTO para respuesta de stock de inventario
 */
public record InventoryStockResponse(
        Long productId,
        String ingredientName,
        Integer availableQuantity
) {}

