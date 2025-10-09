package com.go5u.foodflowplatform.orders.domain.model.aggregates;

import com.go5u.foodflowplatform.orders.domain.model.commands.CreateDishCommand;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Price;
import com.go5u.foodflowplatform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@Entity
public class Dish extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @Embedded
    private Price price;

    public Dish() {
        this.name = StringUtils.EMPTY;
        this.price = null;
    }

    public Dish(String name, BigDecimal price) {
        this();
        this.name = name;
        this.price = new Price(price);
    }

    public Dish(CreateDishCommand command){
        this();
        this.name = command.name();
        this.price = command.price();
    }

}
