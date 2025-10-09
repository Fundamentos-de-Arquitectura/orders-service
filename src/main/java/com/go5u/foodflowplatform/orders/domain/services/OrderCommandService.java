package com.go5u.foodflowplatform.orders.domain.services;

import com.go5u.foodflowplatform.orders.domain.model.commands.AddDishToOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateDishCommand;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;

public interface OrderCommandService {

    Long handle(CreateOrderCommand command);

    void handle(AddDishToOrderCommand command);

}
