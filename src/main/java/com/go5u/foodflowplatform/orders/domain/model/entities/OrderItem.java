package com.go4u.keepitfreshplatform.orders.domain.model.entities;

import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Dish;
import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Order;
import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Quantity;

import com.go4u.keepitfreshplatform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class OrderItem extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @NotNull
    private Quantity quantity;

    @NotNull
    private BigDecimal subtotal;

    public OrderItem(Order order, Dish dish, Quantity quantity, BigDecimal subtotal) {
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public OrderItem() {

    }

    public OrderItem(Order order, Dish dish, Quantity quantity) {
        super();
    }
}
