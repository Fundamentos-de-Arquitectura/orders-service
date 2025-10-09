package com.go4u.keepitfreshplatform.orders.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Quantity(int quantity) {

    public Quantity{
        if(quantity <=0){
            throw new IllegalArgumentException("Quantity must not be null or zero");
        }
    }
}
