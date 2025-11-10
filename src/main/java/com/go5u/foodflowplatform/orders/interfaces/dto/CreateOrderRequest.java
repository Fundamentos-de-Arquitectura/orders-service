package com.go5u.foodflowplatform.orders.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear una orden con platos
 */
public record CreateOrderRequest(
        @NotNull(message = "El número de mesa es obligatorio")
        @Positive(message = "El número de mesa debe ser positivo")
        Integer tableNumber,

        @NotNull(message = "Los platos son obligatorios")
        List<OrderDishRequest> dishes
) {
    /**
     * DTO para un plato en la orden
     */
    public record OrderDishRequest(
            @NotNull(message = "El ID del plato es obligatorio")
            Long dishId,

            @NotNull(message = "La cantidad es obligatoria")
            @Positive(message = "La cantidad debe ser positiva")
            Integer quantity,

            @NotNull(message = "El precio unitario es obligatorio")
            @Positive(message = "El precio unitario debe ser positivo")
            BigDecimal unitPrice,

            @NotNull(message = "El precio final es obligatorio")
            @Positive(message = "El precio final debe ser positivo")
            BigDecimal finalPrice
    ) {}
}

