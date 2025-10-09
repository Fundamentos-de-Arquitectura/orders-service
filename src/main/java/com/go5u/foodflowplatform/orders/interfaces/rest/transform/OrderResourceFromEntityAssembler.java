package com.go5u.foodflowplatform.orders.interfaces.rest.transform;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.OrderResource;

public class OrderResourceFromEntityAssembler {

    public static OrderResource toResourceFromEntity(Order entity) {
        return new OrderResource(entity.getId(), entity.getTableNumber());
    }
}
