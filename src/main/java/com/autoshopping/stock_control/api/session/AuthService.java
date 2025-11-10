package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService {

    @Value("${app.userdata.url}")
    private String userDataBaseUrl; // ex: https://ambteste.credtudo.com.br/App/LoginExterno/GoStock/

    @Value("${app.external.hash}")
    private String externalHash;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private String sessionCookies;

    @Getter
    private JsonNode currentUserData;

    @Getter
    private String currentToken;

    // idLogin foi removido completamente — não é necessário

    public JsonNode fetchUserData(String token) {
        try {
            // Monta URL final: base + / + token
            String url = userDataBaseUrl + (userDataBaseUrl.endsWith("/") ? "" : "/") + token;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Hash", externalHash);
            headers.set("Accept", "application/json");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("Resposta inválida do endpoint userdata. Status: {}", response.getStatusCode());
                return null;
            }

            String body = response.getBody().trim();
            try {
                return objectMapper.readTree(body);
            } catch (Exception e) {
                log.warn("Resposta não é JSON válido. Retornando como fallback.");
                return objectMapper.createObjectNode().put("raw_response", body);
            }

        } catch (Exception e) {
            log.error("Erro ao buscar dados do usuário: {}", e.getMessage(), e);
            return null;
        }
    }

    public void setCurrentSession(String token, JsonNode userData) {
        this.currentToken = token;
        this.currentUserData = userData;

        // Extrai cookies se existirem (opcional)
        this.sessionCookies = (userData != null && userData.has("cookies"))
                ? userData.get("cookies").asText()
                : null;
    }

    public boolean isSessionValid() {
        return currentToken != null && currentUserData != null;
    }

    public void clearSession() {
        this.currentToken = null;
        this.sessionCookies = null;
        this.currentUserData = null;
        log.info("Sessão limpa");
    }
}