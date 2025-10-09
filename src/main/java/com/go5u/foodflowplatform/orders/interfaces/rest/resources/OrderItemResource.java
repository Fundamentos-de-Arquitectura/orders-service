package com.go5u.foodflowplatform.orders.interfaces.rest.resources;

public record OrderItemResource(Long orderItemId, Long orderId, int tableNumber) {
}
