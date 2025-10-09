package com.go5u.foodflowplatform.orders.domain.model.commands;

import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Price;

public record CreateDishCommand(String name, Price price) {
}
