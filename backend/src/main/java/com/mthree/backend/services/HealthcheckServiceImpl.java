package com.mthree.backend.services;

import org.springframework.stereotype.Service;

import com.mthree.backend.services.interfaces.HealthcheckService;

@Service
public class HealthcheckServiceImpl implements HealthcheckService {
    public String getHealthCheck() {
        return "OK";
    }
}
