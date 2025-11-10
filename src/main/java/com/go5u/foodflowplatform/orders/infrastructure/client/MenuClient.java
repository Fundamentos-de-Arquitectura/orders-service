package com.go5u.foodflowplatform.orders.infrastructure.client;

import com.go5u.foodflowplatform.orders.interfaces.dto.DishResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Cliente para comunicarse con el microservicio Menu usando RestClient
 */
@Component
public class MenuClient {

    private static final Logger logger = LoggerFactory.getLogger(MenuClient.class);
    private static final String MENU_SERVICE = "http://menu-service";

    private final RestClient restClient;

    public MenuClient(org.springframework.web.client.RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(MENU_SERVICE)
                .build();
    }

    /**
     * Obtiene un plato por ID desde el microservicio Menu
     * @param dishId ID del plato
     * @return Optional con el plato o vacío si no existe
     */
    public Optional<DishResponse> getDishById(Long dishId) {
        try {
            logger.info("Fetching dish from Menu service: {}", dishId);

            DishResponse dish = restClient.get()
                    .uri("/api/v1/menu/{id}", dishId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                        logger.warn("Error fetching dish {} from Menu service: Status {}", dishId, response.getStatusCode());
                        throw new RuntimeException("Dish not found or error in Menu service");
                    })
                    .body(DishResponse.class);

            return Optional.ofNullable(dish);

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            logger.warn("Dish {} not found in Menu service", dishId);
            return Optional.empty();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.warn("HTTP error fetching dish {} from Menu service: {}", dishId, e.getStatusCode());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching dish {} from Menu service: {}", dishId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}

