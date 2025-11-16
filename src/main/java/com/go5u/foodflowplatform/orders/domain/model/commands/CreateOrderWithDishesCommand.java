package com.go5u.foodflowplatform.orders.domain.model.commands;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comando para crear una orden con platos directamente
 */
public record CreateOrderWithDishesCommand(
        Integer tableNumber,
        List<OrderDishInfo> dishes,
        Long userId
) {
    public record OrderDishInfo(
            Long dishId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal finalPrice
    ) {}
}

