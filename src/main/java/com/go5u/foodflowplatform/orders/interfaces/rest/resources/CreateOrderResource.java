package com.go5u.foodflowplatform.orders.interfaces.rest.resources;


public record CreateOrderResource(Long orderItemId, int tableNumber, Long userId) {
}
