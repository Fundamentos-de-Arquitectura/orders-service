package com.go5u.foodflowplatform.orders.application.internal.queryservices;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Dish;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetAllDishesQuery;
import com.go5u.foodflowplatform.orders.domain.model.queries.GetDishByIdQuery;
import com.go5u.foodflowplatform.orders.domain.services.DishQueryService;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishQueryServiceImpl implements DishQueryService {

    private final DishRepository dishRepository;

    public DishQueryServiceImpl(DishRepository dishRepository) {this.dishRepository = dishRepository;}

    @Override
    public Optional<Dish> handle(GetDishByIdQuery query){
        return dishRepository.findById(query.dishId());
    };

    @Override
    public List<Dish> handle(GetAllDishesQuery query){
        return dishRepository.findAll();
    };
}
