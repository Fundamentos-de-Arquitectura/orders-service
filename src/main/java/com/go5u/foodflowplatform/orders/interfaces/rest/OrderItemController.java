package com.go5u.foodflowplatform.orders.interfaces.rest;

import com.go5u.foodflowplatform.orders.domain.model.queries.GetAllOrdersQuery;
import com.go5u.foodflowplatform.orders.domain.services.OrderQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/orders_dishes")
public class OrderItemController {

    private final OrderQueryService orderQueryService;

    public OrderItemController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrderItems() {
        var orders = orderQueryService.handle(new GetAllOrdersQuery());

        var items = orders.stream()
                .flatMap(order -> order.getOrderSummary().getOrderItems().stream()
                        .map(item -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", item.getId());
                            map.put("order_id", order.getId());
                            map.put("dish_id", item.getDish().getId());
                            map.put("quantity", item.getQuantity().quantity());
                            map.put("subtotal", item.getSubtotal());
                            return map;
                        })
                )
                .toList();

        return ResponseEntity.ok(items);
    }

}
