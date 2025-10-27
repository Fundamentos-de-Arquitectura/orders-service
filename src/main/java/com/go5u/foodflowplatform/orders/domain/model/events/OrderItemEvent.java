package com.go5u.foodflowplatform.orders.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEvent {
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private BigDecimal price;
}