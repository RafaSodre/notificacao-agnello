package com.agnelio.notificacao.controller;

import com.agnelio.notificacao.dto.NotificacaoBemVindoRequest;
import com.agnelio.notificacao.service.ApiKeyService;
import com.agnelio.notificacao.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificacao")
@RequiredArgsConstructor
public class NotificacaoController {

    private final ApiKeyService apiKeyService;
    private final EmailService emailService;

    @PostMapping("/bem-vindo")
    public ResponseEntity<?> notificarBemVindo(
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody NotificacaoBemVindoRequest request) {

        if (!apiKeyService.isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "API Key inválida ou não fornecida! "));
        }

        try {
            emailService.enviarBemVindo(request);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", "Email de boas-vindas enviado com sucesso",
                    "email", request.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor"));
        }
    }
}
