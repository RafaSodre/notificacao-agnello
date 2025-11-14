package com.agnelio.notificacao.service;

import com.agnelio.notificacao.dto.NotificacaoBemVindoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mailtrap.api.token}")
    private String mailtrapToken;

    @Value("${mailtrap.api.url}")
    private String mailtrapUrl;

    public void enviarBemVindo(NotificacaoBemVindoRequest request) {
        if (mailtrapToken == null || mailtrapToken.isBlank()) {
            log.error("Mailtrap API token não configurado. Defina MAILTRAP_API_TOKEN.");
            throw new IllegalStateException("Configuração de email ausente");
        }

        Map<String, Object> payload = new HashMap<>();

        Map<String, Object> from = new HashMap<>();
        from.put("email", "hello@demomailtrap.co");
        from.put("name", "Vinharia Agnello");
        payload.put("from", from);

        Map<String, Object> toEmail = new HashMap<>();
        toEmail.put("email", request.getEmail());
        payload.put("to", List.of(toEmail));

        payload.put("subject", "Bem vindo a Vinharia Agnello!");
        payload.put("text", criarMensagemBemVindo(request.getNome()));
        payload.put("category", "Integration Test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + mailtrapToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(mailtrapUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email enviado via Mailtrap para {}", request.getEmail());
            } else {
                log.error("Falha no envio. Status: {} Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Falha ao enviar email");
            }
        } catch (Exception e) {
            log.error("Erro ao chamar Mailtrap API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro de comunicação com serviço de email", e);
        }
    }

    private String criarMensagemBemVindo(String nome) {
        return String.format("""
                Olá, %s!

                Seja muito bem-vindo(a) à Vinharia Agnello!

                Estamos muito felizes em tê-lo(a) conosco. Nossa vinharia tem o prazer de oferecer
                os melhores vinhos, produzidos com carinho e tradição familiar.

                Esperamos que desfrute de uma experiência única em nossos vinhos e serviços.

                Saúde!

                Equipe Vinharia Agnello
                """, nome != null && !nome.trim().isEmpty() ? nome : "");
    }
}
