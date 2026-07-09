package com.travelagency.confirmationservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

// Controlador que expone el estado de salud del microservicio
@RestController
public class HealthController {

    @GetMapping("/api/confirmations/health")
    public Map<String, String> health() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "up");
        response.put("service", "confirmations");
        return response;
    }

}
