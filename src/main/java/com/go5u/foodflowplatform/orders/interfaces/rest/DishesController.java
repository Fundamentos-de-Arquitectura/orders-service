package com.go4u.keepitfreshplatform.orders.interfaces.rest;

import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Dish;
import com.go4u.keepitfreshplatform.orders.infrastructure.persistence.jpa.repositories.DishRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/dishes")
public class DishesController {

    private final DishRepository dishRepository;

    public DishesController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDishes() {
        var dishes = dishRepository.findAll().stream().map(d -> Map.of(
                "dish_id", d.getId(),
                "name", d.getName(),
                "price", d.getPrice()
        )).toList();

        return ResponseEntity.ok(dishes);
    }

    // 🔥 Añade este método para permitir POST
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createDish(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        double price = ((Number) body.get("price")).doubleValue();
        Dish newDish = new Dish(name, BigDecimal.valueOf(price));
        dishRepository.save(newDish);
    }
}