package com.go5u.foodflowplatform.orders.infrastructure.messaging;

import com.go5u.foodflowplatform.orders.domain.model.events.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    @Value("${kafka.topic.orders-events:orders-events}")
    private String ordersTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderEvent(OrderEvent event) {
        event.setTimestamp(LocalDateTime.now());

        Message<Object> message = MessageBuilder
                .withPayload((Object) event)
                .setHeader(KafkaHeaders.TOPIC, ordersTopicName)
                .setHeader(KafkaHeaders.KEY, event.getOrderId().toString())
                .setHeader("event-type", event.getStatus())
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order event with id: {}",
                                event.getOrderId(), ex);
                    } else {
                        log.info("Successfully published order event with id: {} to topic: {}",
                                event.getOrderId(), ordersTopicName);
                    }
                });
    }
}
