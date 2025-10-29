package com.go5u.foodflowplatform.orders.application.internal.commandservices;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.commands.AddDishToOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.entities.OrderItem;
import com.go5u.foodflowplatform.orders.domain.model.events.OrderEvent;
import com.go5u.foodflowplatform.orders.domain.model.events.OrderItemEvent;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.OrderSummary;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Price;
import com.go5u.foodflowplatform.orders.domain.services.OrderCommandService;
import com.go5u.foodflowplatform.orders.infrastructure.messaging.OrderEventProducer;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.DishRepository;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderCommandServiceImpl(OrderRepository orderRepository,
                                   DishRepository dishRepository,
                                   OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.orderEventProducer = orderEventProducer;
    }

    @Override
    public Long handle(CreateOrderCommand command){
        var order = new Order(
                command.tableNumber(),
                new Price(BigDecimal.ZERO),
                new OrderSummary()
        );
        try{
            orderRepository.save(order);
            log.info("Order created with ID: {}", order.getId());
        } catch(Exception e){
            throw new IllegalArgumentException("Error while saving order: " + e.getMessage());
        }
        return order.getId();
    }

    @Override
    public void handle(AddDishToOrderCommand command){
        if(!orderRepository.existsById(command.orderId())){
            throw new IllegalArgumentException("Order not found");
        }

        var dish = dishRepository.findById(command.dishId())
                .orElseThrow(() -> new IllegalArgumentException("Dish not found"));

        try{
            orderRepository.findById(command.orderId()).map(order -> {
                order.addDishToOrderSummary(dish, command.quantity());

                BigDecimal total = order.getOrderSummary().getOrderItems().stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                order.setTotal(new Price(total));
                orderRepository.save(order);

                publishOrderEvent(order);

                return order;
            });

        } catch (Exception e){
            throw new IllegalArgumentException("Error while saving order: " + e.getMessage());
        }
    }

    private void publishOrderEvent(Order order) {
        List<OrderItemEvent> items = order.getOrderSummary().getOrderItems().stream()
                .map(item -> new OrderItemEvent(
                        item.getDish().getId(),
                        item.getDish().getName(),
                        item.getQuantity().quantity(),
                        item.getDish().getPrice().price()
                ))
                .toList();

        OrderEvent event = new OrderEvent(
                order.getId(),
                items,
                "CREATED",
                null,
                order.getTableNumber()
        );

        orderEventProducer.publishOrderEvent(event);
    }
}
