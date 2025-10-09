package com.go4u.keepitfreshplatform.orders.domain.model.valueobjects;


import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Dish;
import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Order;
import com.go4u.keepitfreshplatform.orders.domain.model.entities.OrderItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
public class OrderSummary {

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public OrderSummary() {this.orderItems = new ArrayList<>();}

    public boolean isEmpty(){return orderItems.isEmpty();}

    public void addItem(Order order, Dish dish, Quantity quantity) {
        BigDecimal price = dish.getPrice().price();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity.quantity()));
        OrderItem orderItem = new OrderItem(order, dish, quantity, subtotal);
        orderItems.add(orderItem);
    }

}
