package com.go5u.foodflowplatform.orders.domain.model.entities;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Dish;
import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Quantity;

import com.go5u.foodflowplatform.shared.domain.model.entities.AuditableModel;
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
    private BigDecimal unitPrice; // Precio unitario del plato

    @NotNull
    private BigDecimal finalPrice; // Precio final (unitPrice * quantity)

    public OrderItem(Order order, Dish dish, Quantity quantity, BigDecimal unitPrice, BigDecimal finalPrice) {
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.finalPrice = finalPrice;
    }

    public OrderItem() {
        // Constructor por defecto para JPA
    }

    // Método legacy para compatibilidad
    public OrderItem(Order order, Dish dish, Quantity quantity, BigDecimal subtotal) {
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
        this.unitPrice = subtotal;
        this.finalPrice = subtotal;
    }

    public BigDecimal getSubtotal() {
        return finalPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
}
