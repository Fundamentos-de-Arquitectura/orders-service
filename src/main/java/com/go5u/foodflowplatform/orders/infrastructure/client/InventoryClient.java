package com.go5u.foodflowplatform.orders.infrastructure.client;

import com.go5u.foodflowplatform.orders.interfaces.dto.InventoryStockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

/**
 * Cliente para comunicarse con el microservicio Inventory usando RestClient
 */
@Component
public class InventoryClient {

    private static final Logger logger = LoggerFactory.getLogger(InventoryClient.class);
    private static final String INVENTORY_SERVICE = "http://inventory-service";

    private final RestClient restClient;

    public InventoryClient(org.springframework.web.client.RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(INVENTORY_SERVICE)
                .build();
    }

    /**
     * Obtiene el stock disponible de un ingrediente por nombre para un usuario específico
     * @param userId ID del usuario
     * @param ingredientName Nombre del ingrediente
     * @return Optional con el stock o vacío si no existe
     */
    public Optional<InventoryStockResponse> getStockByIngredientName(Long userId, String ingredientName) {
        try {
            logger.info("Fetching stock for ingredient: {} for user {}", ingredientName, userId);

            InventoryStockResponse stock = restClient.get()
                    .uri("/api/v1/inventory/users/{userId}/ingredients/{ingredientName}/stock", userId, ingredientName)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                        logger.warn("Error fetching stock for ingredient {} for user {}: Status {}", ingredientName, userId, response.getStatusCode());
                        throw new RuntimeException("Stock not found or error in Inventory service");
                    })
                    .body(InventoryStockResponse.class);

            return Optional.ofNullable(stock);

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            logger.warn("Ingredient {} not found in Inventory service for user {}", ingredientName, userId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error fetching stock for ingredient {} for user {}: {}", ingredientName, userId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Resta cantidad de un ingrediente del inventario para un usuario específico
     * @param userId ID del usuario
     * @param ingredientName Nombre del ingrediente
     * @param quantity Cantidad a restar
     * @return true si se restó exitosamente, false en caso contrario
     */
    public boolean decreaseIngredientStock(Long userId, String ingredientName, Double quantity) {
        try {
            logger.info("Decreasing stock for ingredient {} by {} for user {}", ingredientName, quantity, userId);

            var request = Map.of("quantity", quantity);

            restClient.post()
                    .uri("/api/v1/inventory/users/{userId}/ingredients/{ingredientName}/decrease", userId, ingredientName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                        logger.warn("Error decreasing stock for ingredient {} for user {}: Status {}", ingredientName, userId, res.getStatusCode());
                        throw new RuntimeException("Failed to decrease stock");
                    })
                    .toBodilessEntity();

            logger.info("Successfully decreased stock for ingredient {} by {} for user {}", ingredientName, quantity, userId);
            return true;

        } catch (Exception e) {
            logger.error("Error decreasing stock for ingredient {} for user {}: {}", ingredientName, userId, e.getMessage(), e);
            return false;
        }
    }
}

