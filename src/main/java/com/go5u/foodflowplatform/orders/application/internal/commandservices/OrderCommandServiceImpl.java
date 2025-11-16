package com.go5u.foodflowplatform.orders.application.internal.commandservices;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Dish;
import com.go5u.foodflowplatform.orders.domain.model.aggregates.Order;
import com.go5u.foodflowplatform.orders.domain.model.commands.AddDishToOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderCommand;
import com.go5u.foodflowplatform.orders.domain.model.commands.CreateOrderWithDishesCommand;
import com.go5u.foodflowplatform.orders.domain.model.entities.OrderItem;
import com.go5u.foodflowplatform.orders.domain.model.events.OrderEvent;
import com.go5u.foodflowplatform.orders.domain.model.events.OrderItemEvent;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.OrderSummary;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Price;
import com.go5u.foodflowplatform.orders.domain.model.valueobjects.Quantity;
import com.go5u.foodflowplatform.orders.domain.services.OrderCommandService;
import com.go5u.foodflowplatform.orders.infrastructure.client.InventoryClient;
import com.go5u.foodflowplatform.orders.infrastructure.client.MenuClient;
import com.go5u.foodflowplatform.orders.infrastructure.messaging.OrderEventProducer;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.DishRepository;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.OrderRepository;
import com.go5u.foodflowplatform.orders.interfaces.dto.DishResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderEventProducer orderEventProducer;
    private final MenuClient menuClient;
    private final InventoryClient inventoryClient;

    public OrderCommandServiceImpl(OrderRepository orderRepository,
                                   DishRepository dishRepository,
                                   OrderEventProducer orderEventProducer,
                                   MenuClient menuClient,
                                   InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.orderEventProducer = orderEventProducer;
        this.menuClient = menuClient;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public Long handle(CreateOrderCommand command){
        var order = new Order(
                command.tableNumber(),
                new Price(BigDecimal.ZERO),
                new OrderSummary(),
                command.userId()
        );
        try{
            orderRepository.save(order);
            log.info("Order created with ID: {} for user: {}", order.getId(), command.userId());
        } catch(Exception e){
            throw new IllegalArgumentException("Error while saving order: " + e.getMessage());
        }
        return order.getId();
    }

    @Override
    public void handle(AddDishToOrderCommand command){
        if(!orderRepository.existsById(command.orderId())){
            throw new IllegalArgumentException("Order not found");
        }

        var dish = dishRepository.findById(command.dishId())
                .orElseThrow(() -> new IllegalArgumentException("Dish not found"));

        try{
            orderRepository.findById(command.orderId()).map(order -> {
                order.addDishToOrderSummary(dish, command.quantity());

                BigDecimal total = order.getOrderSummary().getOrderItems().stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                order.setTotal(new Price(total));
                orderRepository.save(order);

                publishOrderEvent(order);

                return order;
            });

        } catch (Exception e){
            throw new IllegalArgumentException("Error while saving order: " + e.getMessage());
        }
    }

    private void publishOrderEvent(Order order) {
        List<OrderItemEvent> items = order.getOrderSummary().getOrderItems().stream()
                .map(item -> new OrderItemEvent(
                        item.getDish().getId(),
                        item.getDish().getName(),
                        item.getQuantity().quantity(),
                        item.getDish().getPrice().price()
                ))
                .toList();

        OrderEvent event = new OrderEvent(
                order.getId(),
                items,
                "CREATED",
                null,
                order.getTableNumber(),
                order.getUserId()
        );

        orderEventProducer.publishOrderEvent(event);
    }

    @Override
    public Long handle(CreateOrderWithDishesCommand command) {
        log.info("Creating order with {} dishes for table {}", command.dishes().size(), command.tableNumber());

        // 1. Obtener información de platos desde Menu service y validar stock
        Map<Long, DishResponse> dishesInfo = new HashMap<>();
        Map<String, Double> requiredIngredients = new HashMap<>(); // Nombre -> cantidad total necesaria
        List<String> stockWarnings = new java.util.ArrayList<>(); // Para mensajes de advertencia

        // Obtener cada plato y calcular ingredientes necesarios
        for (var dishInfo : command.dishes()) {
            DishResponse dish = menuClient.getDishById(dishInfo.dishId())
                    .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishInfo.dishId()));

            dishesInfo.put(dishInfo.dishId(), dish);

            // Calcular ingredientes necesarios (cantidad del plato * cantidad de cada ingrediente * cantidad de la orden)
            for (var ingredient : dish.ingredients()) {
                String ingredientName = ingredient.name();
                // Cantidad necesaria = cantidad del ingrediente en el plato * cantidad de platos pedidos
                double requiredQuantity = ingredient.quantity() * dishInfo.quantity();
                requiredIngredients.merge(ingredientName, requiredQuantity, Double::sum);
            }
        }

        // 2. Validar stock disponible para todos los ingredientes
        for (Map.Entry<String, Double> entry : requiredIngredients.entrySet()) {
            String ingredientName = entry.getKey();
            Double requiredQuantity = entry.getValue();

            var stockOpt = inventoryClient.getStockByIngredientName(command.userId(), ingredientName);
            if (stockOpt.isEmpty()) {
                throw new IllegalArgumentException("Ingredient not found in inventory: " + ingredientName);
            }

            Integer availableStock = stockOpt.get().availableQuantity();
            
            // Check if stock is insufficient
            if (availableStock == 0) {
                throw new IllegalArgumentException(
                        String.format("The inventory has no %s", ingredientName));
            }
            
            if (availableStock < requiredQuantity.intValue()) {
                throw new IllegalArgumentException(
                        String.format("The inventory doesn't have enough %s. Required: %.2f, Available: %d",
                                ingredientName, requiredQuantity, availableStock));
            }

            log.info("Stock validated for ingredient {}: Required={}, Available={}", 
                    ingredientName, requiredQuantity, availableStock);
        }

        // 3. Crear la orden
        Order order = new Order(
                command.tableNumber(),
                new Price(BigDecimal.ZERO),
                new OrderSummary(),
                command.userId()
        );

        BigDecimal totalPrice = BigDecimal.ZERO;

        // 4. Agregar platos a la orden (crear Dish local si no existe)
        for (var dishInfo : command.dishes()) {
            DishResponse dishResponse = dishesInfo.get(dishInfo.dishId());

            // Buscar o crear Dish local
            Dish dish = dishRepository.findById(dishInfo.dishId())
                    .orElseGet(() -> {
                        Dish newDish = new Dish(dishResponse.name(), dishResponse.price());
                        newDish.setId(dishInfo.dishId());
                        return dishRepository.save(newDish);
                    });

            // Crear OrderItem con precio unitario y precio final
            OrderItem orderItem = new OrderItem(
                    order,
                    dish,
                    new Quantity(dishInfo.quantity()),
                    dishInfo.unitPrice(),
                    dishInfo.finalPrice()
            );

            order.getOrderSummary().getOrderItems().add(orderItem);
            totalPrice = totalPrice.add(dishInfo.finalPrice());
        }

        order.setTotal(new Price(totalPrice));

        try {
            orderRepository.save(order);
            log.info("Order created successfully with ID: {}", order.getId());

            // 5. Publicar evento de orden creada (inventory service procesará la actualización de stock via Kafka)
            publishOrderEvent(order);

            return order.getId();

        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error while saving order: " + e.getMessage());
        }
    }
}
