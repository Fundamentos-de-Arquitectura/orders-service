package com.go4u.keepitfreshplatform.orders.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Table(int table) {

    public Table{
        if (table <= 0){
            throw new IllegalArgumentException("Table number must be greater than zero");
        }
    }


}
