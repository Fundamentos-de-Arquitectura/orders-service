package com.go5u.foodflowplatform.orders.interfaces.rest;

import com.go5u.foodflowplatform.orders.domain.model.aggregates.Dish;
import com.go5u.foodflowplatform.orders.infrastructure.persistence.jpa.repositories.DishRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/dishes")
@Tag(name = "Dishes", description = "Dish Management APIs")
public class DishesController {

    private final DishRepository dishRepository;

    public DishesController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Operation(summary = "Get all dishes", description = "Retrieve a list of all available dishes")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved dishes")
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        log.info("Fetched {} dishes from DB", dishes != null ? dishes.size() : 0);
        return ResponseEntity.ok(dishes);
    }

    @Operation(summary = "Get dish by ID", description = "Retrieve a specific dish by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dish found"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    @GetMapping("/{dishId}")
    public ResponseEntity<Dish> getDishById(
            @Parameter(description = "ID of the dish to be retrieved")
            @PathVariable Long dishId) {
        Optional<Dish> dish = dishRepository.findById(dishId);
        if (dish.isPresent()) {
            log.info("Fetched dish {} from DB", dishId);
            return ResponseEntity.ok(dish.get());
        } else {
            log.info("Dish {} not found in DB", dishId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create a new dish", description = "Create a new dish in the system")
    @ApiResponse(responseCode = "201", description = "Dish successfully created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Dish> createDish(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            Object priceObj = body.get("price");
            BigDecimal price = priceObj != null ? new BigDecimal(String.valueOf(priceObj)) : null;

            Dish dish = new Dish(name, price);
            Dish saved = dishRepository.save(dish);
            log.info("Successfully created dish with id {} in DB", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating dish in DB", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update an existing dish", description = "Update details of an existing dish")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dish successfully updated"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    @PutMapping("/{dishId}")
    public ResponseEntity<Dish> updateDish(
            @Parameter(description = "ID of the dish to be updated")
            @PathVariable Long dishId,
            @RequestBody Map<String, Object> body) {
        Optional<Dish> optional = dishRepository.findById(dishId);
        if (optional.isPresent()) {
            Dish dish = optional.get();
            String name = (String) body.get("name");
            Object priceObj = body.get("price");
            BigDecimal price = priceObj != null ? new BigDecimal(String.valueOf(priceObj)) : null;

            dish.update(name, price);
            Dish saved = dishRepository.save(dish);
            log.info("Successfully updated dish {} in DB", dishId);
            return ResponseEntity.ok(saved);
        } else {
            log.info("Dish {} not found in DB", dishId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a dish", description = "Remove a dish from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dish successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    @DeleteMapping("/{dishId}")
    public ResponseEntity<Void> deleteDish(
            @Parameter(description = "ID of the dish to be deleted")
            @PathVariable Long dishId) {
        try {
            if (dishRepository.existsById(dishId)) {
                dishRepository.deleteById(dishId);
                log.info("Successfully deleted dish {} from DB", dishId);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting dish {} from DB", dishId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}