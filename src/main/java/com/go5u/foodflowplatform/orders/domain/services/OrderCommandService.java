package com.go4u.keepitfreshplatform.orders.domain.services;

import com.go4u.keepitfreshplatform.orders.domain.model.commands.AddDishToOrderCommand;
import com.go4u.keepitfreshplatform.orders.domain.model.commands.CreateDishCommand;
import com.go4u.keepitfreshplatform.orders.domain.model.commands.CreateOrderCommand;

public interface OrderCommandService {

    Long handle(CreateOrderCommand command);

    void handle(AddDishToOrderCommand command);

}
