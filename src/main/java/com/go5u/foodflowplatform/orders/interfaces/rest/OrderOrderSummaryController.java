package com.go5u.foodflowplatform.orders.interfaces.rest;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.commands.AddDishToOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetOrderByIdQuery;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Quantity;
import com.go5u.foodflowplatform.orders.domain.services.OrderCommandService;
import com.go5u.foodflowplatform.orders.domain.services.OrderQueryService;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.AddOrderItemResource;
import com.go5u.foodflowplatform.orders.interfaces.rest.resources.OrderItemResource;
import com.go5u.foodflowplatform.orders.interfaces.rest.transform.OrderItemResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders/order-summary")
@Tag(name = "Orders", description = "Order Summary APIs")
public class OrderOrderSummaryController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    public OrderOrderSummaryController(OrderCommandService orderCommandService, OrderQueryService orderQueryService) {
        this.orderCommandService = orderCommandService;
        this.orderQueryService = orderQueryService;
    }

    /**
     * Add order item to order summary
     */
    @PostMapping("/{orderId}")
    public ResponseEntity<OrderItemResource> addOrderItemToOrderSummary(
            @PathVariable Long orderId, @RequestBody AddOrderItemResource resource) {

        try {
            var addDishToOrderCommand = new AddDishToOrderCommand(orderId, resource.dishId(), new Quantity(resource.quantity()));

            orderCommandService.handle(addDishToOrderCommand);

            var updatedOrder = orderQueryService.handle(new GetOrderByIdQuery(orderId));
            if (updatedOrder.isEmpty()) return ResponseEntity.notFound().build();

            var order = updatedOrder.get();
            var items = order.getOrderSummary().getOrderItems();
            if (items.isEmpty()) return ResponseEntity.noContent().build();

            var lastItem = items.getLast();
            var itemResource = OrderItemResourceFromEntityAssembler.toResourceFromEntity(lastItem);

            return new ResponseEntity<>(itemResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error processing order summary item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get order summary by order ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderItemResource> getOrderItemById(@PathVariable Long orderId) {
        var order = orderQueryService.handle(new GetOrderByIdQuery(orderId));
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var items = order.get().getOrderSummary().getOrderItems();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var lastItem = items.getLast();
        var itemResource = OrderItemResourceFromEntityAssembler.toResourceFromEntity(lastItem);

        return ResponseEntity.ok(itemResource);
    }

}
