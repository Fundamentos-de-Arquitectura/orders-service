package com.go4u.keepitfreshplatform.orders.domain.model.commands;

import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Price;

public record CreateDishCommand(String name, Price price) {
}
