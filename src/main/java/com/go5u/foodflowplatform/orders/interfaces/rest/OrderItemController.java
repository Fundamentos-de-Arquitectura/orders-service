package com.go5u.foodflowplatform.orders.interfaces.rest;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetAllOrdersQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrderItemByTableQuery;
import com.go5u.foodflowplatform.orders.domain.services.OrderQueryService;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.OrderItemResource;
import com.go5u.foodflowplatform.orders.interfaces.rest.transform.OrderItemResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders_dishes")
@Tag(name = "Order Items", description = "Order items management APIs")
public class OrderItemController {

    private final OrderQueryService orderQueryService;

    public OrderItemController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @Operation(summary = "Get all order items for a user", description = "Retrieve all order items across all orders for a specific user")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderItemResource>> getAllOrderItems(@PathVariable Long userId) {
        log.info("Fetching all order items for user {}", userId);
        List<Order> orders = orderQueryService.handle(new GetAllOrdersQuery(userId));

        List<OrderItemResource> items = orders.stream()
                .flatMap(order -> order.getOrderSummary().getOrderItems().stream())
                .map(OrderItemResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get order item by ID for a user", description = "Retrieve a single order item by item ID from the database for a specific user")
    @GetMapping("/users/{userId}/{itemId}")
    public ResponseEntity<OrderItemResource> getOrderItemById(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        log.info("Fetching order item {} for user {}", itemId, userId);
        List<Order> orders = orderQueryService.handle(new GetAllOrdersQuery(userId));
        for (Order order : orders) {
            var items = order.getOrderSummary().getOrderItems();
            for (var item : items) {
                if (item.getId().equals(itemId)) {
                    var resource = OrderItemResourceFromEntityAssembler.toResourceFromEntity(item);
                    return ResponseEntity.ok(resource);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

}
