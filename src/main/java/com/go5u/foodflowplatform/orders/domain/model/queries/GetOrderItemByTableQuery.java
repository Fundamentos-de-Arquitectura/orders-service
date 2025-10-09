package com.go5u.foodflowplatform.orders.domain.model.queries;

import com.go5u.foodflowplatform.orders.domain.model.entities.OrderItem;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Table;

public record GetOrderItemByTableQuery(int tableNumber){
}
