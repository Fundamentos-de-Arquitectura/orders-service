package com.go4u.keepitfreshplatform.orders.interfaces.rest;

import com.go4u.keepitfreshplatform.orders.domain.model.commands.AddDishToOrderCommand;
import com.go4u.keepitfreshplatform.orders.domain.model.queries.GetOrderByIdQuery;
import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Quantity;
import com.go4u.keepitfreshplatform.orders.domain.services.OrderCommandService;
import com.go4u.keepitfreshplatform.orders.domain.services.OrderQueryService;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.AddOrderItemResource;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.resources.OrderItemResource;
import com.go4u.keepitfreshplatform.orders.interfaces.rest.transform.OrderItemResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/v1/orders/order-summary",produces = APPLICATION_JSON_VALUE)
@Tag(name = "Orders")
public class OrderOrderSummaryController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    public OrderOrderSummaryController(OrderCommandService orderCommandService, OrderQueryService orderQueryService) {
        this.orderCommandService = orderCommandService;
        this.orderQueryService = orderQueryService;
    }

    /**
     * Adds an order item to order summary
     */
    @PostMapping("/{orderId}")
    public ResponseEntity<OrderItemResource> addOrderItemToOrderSummary(
            @PathVariable Long orderId, @RequestBody AddOrderItemResource resource) {

        var addDishToOrderCommand = new AddDishToOrderCommand(orderId, resource.dishId(), new Quantity(resource.quantity()));

        try{
            orderCommandService.handle(addDishToOrderCommand);

            var updatedOrder = orderQueryService.handle(new GetOrderByIdQuery(orderId));
            if(updatedOrder.isEmpty()) return ResponseEntity.notFound().build();

            var order = updatedOrder.get();

            var items = order.getOrderSummary().getOrderItems();
            if(items.isEmpty()) return ResponseEntity.internalServerError().build();

            var lastItem = items.getLast();

            var itemResource = OrderItemResourceFromEntityAssembler.toResourceFromEntity(lastItem);

            return new ResponseEntity<>(itemResource, HttpStatus.CREATED);

        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

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
