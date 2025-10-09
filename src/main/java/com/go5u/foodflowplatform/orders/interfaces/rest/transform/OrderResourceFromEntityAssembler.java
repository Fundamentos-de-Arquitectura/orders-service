package com.go4u.keepitfreshplatform.orders.interfaces.rest.transform;

import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Order;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.OrderResource;

public class OrderResourceFromEntityAssembler {

    public static OrderResource toResourceFromEntity(Order entity) {
        return new OrderResource(entity.getId(), entity.getTableNumber());
    }
}
