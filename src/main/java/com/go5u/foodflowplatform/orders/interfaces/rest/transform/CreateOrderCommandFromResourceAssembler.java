package com.go5u.foodflowplatform.orders.interfaces.rest.transform;

import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.CreateOrderResource;

public class CreateOrderCommandFromResourceAssembler {
    public static CreateOrderCommand toCommandFromResource(CreateOrderResource resource) {
        return new CreateOrderCommand(resource.tableNumber(), resource.userId());
    }
}
