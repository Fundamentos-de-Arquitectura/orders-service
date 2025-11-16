package com.go5u.foodflowplatform.orders.domain.model.aggregates;


import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;

import com.go5u.foodflowplatform.orders.domain.model.valueobjects.OrderSummary;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Price;

import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Quantity;
import com.go5u.foodflowplatform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class Order extends AuditableAbstractAggregateRoot<Order> {

    @NotNull
    private int tableNumber;

    @NotNull
    @Getter
    private Price total;

    @Embedded
    private final OrderSummary orderSummary;

    @Column(name = "user_id")
    private Long userId; // ID of the user who created the order

    public Order(int tableNumber, Price total, OrderSummary orderSummary) {
        this.tableNumber = tableNumber;
        this.total = total;
        this.orderSummary = orderSummary;
    }

    public Order(int tableNumber, Price total, OrderSummary orderSummary, Long userId) {
        this.tableNumber = tableNumber;
        this.total = total;
        this.orderSummary = orderSummary;
        this.userId = userId;
    }

    public Order(CreateOrderCommand command, OrderSummary orderSummary) {
        this.tableNumber = command.tableNumber();
        this.total = new Price(BigDecimal.ZERO); // O el valor que corresponda
        this.orderSummary = orderSummary;
        this.userId = command.userId();
    }

    public Order() {
        this.tableNumber = 0;
        this.total = null;
        this.orderSummary = new OrderSummary();
    }

    public void addDishToOrderSummary(Dish dish, Quantity quantity) {
        this.orderSummary.addItem(this, dish, quantity);
    }

    public void setTotal(Price total) {
        this.total = total;
    }


}
