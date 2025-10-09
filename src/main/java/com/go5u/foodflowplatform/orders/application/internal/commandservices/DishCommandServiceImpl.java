package com.go5u.foodflowplatform.orders.application.internal.commandservices;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Dish;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateDishCommand;
import com.go5u.foodflowplatform.orders.domain.services.DishCommandService;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.DishRepository;
import org.springframework.stereotype.Service;

@Service
public class DishCommandServiceImpl implements DishCommandService {

    private final DishRepository dishRepository;

    public DishCommandServiceImpl(DishRepository dishRepository) { this.dishRepository = dishRepository; }

    @Override
    public Long handle(CreateDishCommand command){
        if(dishRepository.existsByName(command.name())){
            throw new IllegalArgumentException("Dish already exists");
        }
        var dish = new Dish(command);
        try{
            dishRepository.save(dish);
        }catch (Exception e){
            throw new IllegalArgumentException("Error while saving dish" + e.getMessage());
        }
        return dish.getId();

    }

}
