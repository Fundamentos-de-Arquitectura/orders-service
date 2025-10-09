package com.go5u.foodflowplatform.orders.domain.services;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.entities.OrderItem;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetAllOrdersQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrderByIdQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrderItemByTableQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrdersByTableNumberQuery;

import java.util.List;
import java.util.Optional;

public interface OrderQueryService {

    List<Order> handle(GetAllOrdersQuery query);

    List<OrderItem> handle(GetOrderItemByTableQuery query);

    Optional<Order> handle(GetOrderByIdQuery query);

    List<Order> handle(GetOrdersByTableNumberQuery query);


}
