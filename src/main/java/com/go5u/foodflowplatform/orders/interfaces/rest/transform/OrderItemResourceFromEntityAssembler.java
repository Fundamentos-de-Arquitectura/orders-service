package com.go5u.foodflowplatform.orders.interfaces.rest.transform;

import com.go5u.foodflowplatform.orders.domain.model.entities.OrderItem;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.CreateOrderResource;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.OrderItemResource;

public class OrderItemResourceFromEntityAssembler {
    public static OrderItemResource toResourceFromEntity(OrderItem entity) {
       return new OrderItemResource(entity.getId(), entity.getOrder().getId(), entity.getOrder().getTableNumber() );
    }

}
