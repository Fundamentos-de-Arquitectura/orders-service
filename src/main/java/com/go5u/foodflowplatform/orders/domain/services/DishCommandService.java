package com.go4u.keepitfreshplatform.orders.domain.services;

import com.go4u.keepitfreshplatform.orders.domain.model.commands.CreateDishCommand;

public interface DishCommandService {

    Long handle(CreateDishCommand command);

}
