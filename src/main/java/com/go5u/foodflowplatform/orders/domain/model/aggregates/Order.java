package com.go4u.keepitfreshplatform.orders.domain.model.aggregates;


import com.go4u.keepitfreshplatform.orders.domain.model.commands.CreateOrderCommand;

import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.OrderSummary;
import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Price;

import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Quantity;
import com.go4u.keepitfreshplatform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
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

    public Order(int tableNumber, Price total, OrderSummary orderSummary) {
        this.tableNumber = tableNumber;
        this.total = total;
        this.orderSummary = orderSummary;
    }

    public Order(CreateOrderCommand command, OrderSummary orderSummary) {
        this.tableNumber = command.tableNumber();
        this.total = new Price(BigDecimal.ZERO); // O el valor que corresponda
        this.orderSummary = orderSummary;
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
