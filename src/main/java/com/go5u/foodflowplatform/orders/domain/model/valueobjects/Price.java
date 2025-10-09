package com.go5u.foodflowplatform.orders.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record Price(BigDecimal price) {

}
