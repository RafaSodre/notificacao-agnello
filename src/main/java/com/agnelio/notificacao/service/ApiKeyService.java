package com.agnelio.notificacao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    @Value("${app.api.key}")
    private String validApiKey;

    public boolean isValidApiKey(String apiKey) {
        return validApiKey != null && validApiKey.equals(apiKey);
    }
}
