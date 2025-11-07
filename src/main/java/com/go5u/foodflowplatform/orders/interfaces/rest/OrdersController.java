package com.go5u.foodflowplatform.orders.interfaces.rest;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetAllOrdersQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrderByIdQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrdersByTableNumberQuery;
import com.go5u.foodflowplatform.orders.domain.services.OrderCommandService;
import com.go5u.foodflowplatform.orders.domain.services.OrderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Operation(summary = "Create a new order", description = "Create a new order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> body) {
        try {
            Object tableNumberObj = body.get("tableNumber");
            if (tableNumberObj == null) {
                return ResponseEntity.badRequest().build();
            }
            int tableNumber = (tableNumberObj instanceof Number) ? ((Number) tableNumberObj).intValue() : Integer.parseInt(String.valueOf(tableNumberObj));

            Long orderId = orderCommandService.handle(new CreateOrderCommand(tableNumber));
            Map<String, Object> resp = new HashMap<>();
            resp.put("orderId", orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            log.error("Error creating order in DB", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/table/{tableNumber}")
    @Operation(summary = "Get orders by table number", description = "Retrieve Orders by table number")
    public ResponseEntity<List<Order>> getOrdersByTableNumber(@PathVariable int tableNumber) {
        List<Order> orders = orderQueryService.handle(new GetOrdersByTableNumberQuery(tableNumber));
        return ResponseEntity.ok(orders);
    }


    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all Orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderQueryService.handle(new GetAllOrdersQuery());
        return ResponseEntity.ok(orders);
    }

}
