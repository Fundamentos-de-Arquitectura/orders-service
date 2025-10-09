package com.go4u.keepitfreshplatform.orders.domain.model.commands;

import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Quantity;

public record AddDishToOrderCommand(Long orderId, Long dishId, Quantity quantity) {
}
