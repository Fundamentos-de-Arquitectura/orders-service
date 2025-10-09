package com.go4u.keepitfreshplatform.orders.domain.model.queries;

import com.go4u.keepitfreshplatform.orders.domain.model.entities.OrderItem;
import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Table;

public record GetOrderItemByTableQuery(int tableNumber){
}
