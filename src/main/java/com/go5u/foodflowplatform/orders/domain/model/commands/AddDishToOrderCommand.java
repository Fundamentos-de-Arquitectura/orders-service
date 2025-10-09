package com.go5u.foodflowplatform.orders.domain.model.commands;

import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Quantity;

public record AddDishToOrderCommand(Long orderId, Long dishId, Quantity quantity) {
}
