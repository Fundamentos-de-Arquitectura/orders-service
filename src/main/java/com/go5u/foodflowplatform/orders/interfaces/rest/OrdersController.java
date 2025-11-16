package com.go5u.foodflowplatform.orders.interfaces.rest;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderWithDishesCommand;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetAllOrdersQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrderByIdQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrdersByTableNumberQuery;
import com.go5u.foodflowplatform.orders.domain.services.OrderCommandService;
import com.go5u.foodflowplatform.orders.domain.services.OrderQueryService;
import com.go5u.foodflowplatform.orders.interfaces.dto.CreateOrderRequest;
import com.go5u.foodflowplatform.orders.interfaces.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order APIs")
public class OrdersController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    public OrdersController(OrderCommandService orderCommandService, OrderQueryService orderQueryService) {
        this.orderCommandService = orderCommandService;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new order with dishes", description = "Create a new order with dishes, validating stock and updating inventory")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            log.info("Creating order for table {} with {} dishes", request.tableNumber(), request.dishes().size());

            // Convertir CreateOrderRequest a CreateOrderWithDishesCommand
            List<CreateOrderWithDishesCommand.OrderDishInfo> dishes = request.dishes().stream()
                    .map(dish -> new CreateOrderWithDishesCommand.OrderDishInfo(
                            dish.dishId(),
                            dish.quantity(),
                            dish.unitPrice(),
                            dish.finalPrice()
                    ))
                    .collect(Collectors.toList());

            CreateOrderWithDishesCommand command = new CreateOrderWithDishesCommand(
                    request.tableNumber(),
                    dishes,
                    request.userId()
            );

            Long orderId = orderCommandService.handle(command);

            Map<String, Object> resp = new HashMap<>();
            resp.put("orderId", orderId);
            resp.put("message", "Order created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid order creation request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating order in DB", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/table/{tableNumber}")
    @Operation(summary = "Get orders by table number", description = "Retrieve Orders by table number")
    public ResponseEntity<List<OrderResponse>> getOrdersByTableNumber(@PathVariable int tableNumber) {
        try {
            List<Order> orders = orderQueryService.handle(new GetOrdersByTableNumberQuery(tableNumber));
            List<OrderResponse> responses = orders.stream()
                    .map(this::toOrderResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching orders for table {}", tableNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/users/{userId}")
    @Operation(summary = "Get all orders for a user", description = "Retrieve all Orders for a specific user")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@PathVariable Long userId) {
        try {
            log.info("Fetching all orders for user {}", userId);
            List<Order> orders = orderQueryService.handle(new GetAllOrdersQuery(userId));
            List<OrderResponse> responses = orders.stream()
                    .map(this::toOrderResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching all orders for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Convierte una entidad Order a OrderResponse
     */
    private OrderResponse toOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getOrderSummary().getOrderItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getDish().getId(),
                        item.getDish().getName(),
                        item.getQuantity().quantity(),
                        item.getUnitPrice(),
                        item.getFinalPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTableNumber(),
                items,
                order.getTotal().price(),
                order.getCreatedAt(),
                java.util.Collections.emptyList() // Stock warnings are checked at creation time
        );
    }

}
