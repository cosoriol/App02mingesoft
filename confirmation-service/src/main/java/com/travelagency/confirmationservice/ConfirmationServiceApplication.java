package com.travelagency.confirmationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Clase principal del microservicio confirmation-service
@SpringBootApplication
@EnableDiscoveryClient
public class ConfirmationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfirmationServiceApplication.class, args);
    }

}
