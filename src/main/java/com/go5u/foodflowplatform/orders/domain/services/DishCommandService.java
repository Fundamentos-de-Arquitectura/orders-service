package com.go5u.foodflowplatform.orders.domain.services;

import com.go5u.foodflowplatform.orders.domain.model.commands.CreateDishCommand;

public interface DishCommandService {

    Long handle(CreateDishCommand command);

}
