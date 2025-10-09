package com.go4u.keepitfreshplatform.orders.interfaces.rest.transform;

import com.go4u.keepitfreshplatform.orders.domain.model.commands.CreateOrderCommand;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.CreateOrderResource;

public class CreateOrderCommandFromResourceAssembler {
    public static CreateOrderCommand toCommandFromResource(CreateOrderResource resource) {
        return new CreateOrderCommand(resource.tableNumber());
    }
}
