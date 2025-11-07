package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService {

    @Value("${app.userdata.url}")
    private String userDataUrl;

    @Value("${app.external.hash}")
    private String externalHash;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private String sessionCookies;

    @Getter
    private Long currentIdLogin;

    @Getter
    private JsonNode currentUserData;

    @Getter
    private String currentToken;

    public JsonNode fetchUserData(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", token);
            headers.set("Hash", externalHash);
            headers.set("Accept", "application/json");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(userDataUrl, HttpMethod.GET, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("Resposta inválida do endpoint de userdata. Status: {}", response.getStatusCode());
                return null;
            }

            String responseBody = response.getBody().trim();
            JsonNode jsonNode;

            try {
                jsonNode = objectMapper.readTree(responseBody);
            } catch (Exception e) {
                log.warn("Resposta não está em JSON. Tentando converter...");
                // Se não for JSON, envolvemos como { "raw": "..." }
                jsonNode = objectMapper.createObjectNode()
                        .put("raw_response", responseBody);
            }

            // Extrai id_login opcionalmente para validação futura
            if (jsonNode.has("id_login")) {
                // ok, tem id_login
            } else {
                log.warn("Resposta do userdata não contém 'id_login'");
            }

            return jsonNode;

        } catch (Exception e) {
            log.error("Erro ao buscar dados do usuário no endpoint {}: {}", userDataUrl, e.getMessage(), e);
            return null;
        }
    }

    public void setCurrentSession(String token, JsonNode userData) {
        this.currentToken = token;
        this.currentUserData = userData;
        this.currentIdLogin = userData.has("id_login") ? userData.get("id_login").asLong() : null;
        this.sessionCookies = userData.has("cookies") ? userData.get("cookies").asText() : null;
    }

    public boolean isSessionValid() {
        return currentToken != null && currentUserData != null;
    }

    public void clearSession() {
        this.currentToken = null;
        this.sessionCookies = null;
        this.currentIdLogin = null;
        this.currentUserData = null;
        log.info("Sessão limpa");
    }
}