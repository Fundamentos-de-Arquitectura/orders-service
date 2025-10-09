package com.go4u.keepitfreshplatform.orders.interfaces.rest.transform;

import com.go4u.keepitfreshplatform.orders.domain.model.entities.OrderItem;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.CreateOrderResource;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.OrderItemResource;

public class OrderItemResourceFromEntityAssembler {
    public static OrderItemResource toResourceFromEntity(OrderItem entity) {
       return new OrderItemResource(entity.getId(), entity.getOrder().getId(), entity.getOrder().getTableNumber() );
    }

}
