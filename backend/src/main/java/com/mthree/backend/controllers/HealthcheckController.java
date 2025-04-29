package com.mthree.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mthree.backend.services.interfaces.HealthcheckService;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/healthcheck")
public class HealthcheckController {
    private final HealthcheckService healthcheckService;

    public HealthcheckController(HealthcheckService healthcheckService) {
        this.healthcheckService = healthcheckService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseType<String>> healthCheck() {
        ResponseType<String> response = new ResponseType<>(
                "OK",
                this.healthcheckService.getHealthCheck(),
                200
        );

        return ResponseEntity.ok(response);
    }
}
