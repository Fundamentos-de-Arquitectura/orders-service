package com.go5u.foodflowplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FoodFlowPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodFlowPlatformApplication.class, args);
    }

}