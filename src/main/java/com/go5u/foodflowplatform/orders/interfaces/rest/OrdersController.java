package com.go4u.keepitfreshplatform.orders.interfaces.rest;


import com.go4u.keepitfreshplatform.orders.domain.model.queries.GetAllOrdersQuery;
import com.go4u.keepitfreshplatform.orders.domain.model.queries.GetOrderByIdQuery;
import com.go4u.keepitfreshplatform.orders.domain.model.queries.GetOrdersByTableNumberQuery;
import com.go4u.keepitfreshplatform.orders.domain.services.OrderCommandService;
import com.go4u.keepitfreshplatform.orders.domain.services.OrderQueryService;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.CreateOrderResource;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.OrderResource;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.transform.CreateOrderCommandFromResourceAssembler;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.transform.OrderResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/v1/orders", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Orders", description = "Order Management Endpoints")
public class OrdersController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    public OrdersController(OrderCommandService orderCommandService, OrderQueryService orderQueryService) {
        this.orderCommandService = orderCommandService;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping
    public ResponseEntity<OrderResource> createOrder(@RequestBody CreateOrderResource createOrderResource) {
        var createOrderCommand = CreateOrderCommandFromResourceAssembler.toCommandFromResource(createOrderResource);
        var orderId = orderCommandService.handle(createOrderCommand);
        if (orderId == null) {
            return ResponseEntity.badRequest().build();
        }
        var getOrderByIdQuery = new GetOrderByIdQuery(orderId);
        var order = orderQueryService.handle(getOrderByIdQuery);
        if (order.isEmpty()) return ResponseEntity.badRequest().build();
        var orderResource = OrderResourceFromEntityAssembler.toResourceFromEntity(order.get());
        return new ResponseEntity<>(orderResource, HttpStatus.CREATED);
    }


    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<OrderResource>> getOrderByTableNumber(@PathVariable int tableNumber) {
        var getOrderByTableNumberQuery = new GetOrdersByTableNumberQuery(tableNumber);
        var orders = orderQueryService.handle(getOrderByTableNumberQuery);
        if (orders.isEmpty()) return ResponseEntity.badRequest().build();
        var resources = orders.stream()
                .map(OrderResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);

    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        var getAllOrdersQuery = new GetAllOrdersQuery();
        var orders = orderQueryService.handle(getAllOrdersQuery);

        var orderResources = orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("table_number", order.getTableNumber());
            map.put("total", order.getTotal().price()); // Extrae valor primitivo del VO
            map.put("createdAt", order.getCreatedAt().toInstant().toString()); // Formato ISO
            return map;
        }).toList();

        return ResponseEntity.ok(orderResources);
    }



}
