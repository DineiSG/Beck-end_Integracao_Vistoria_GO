package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService {

    // Removido @Value("${app.token.url}")
    @Value("${app.userdata.url}")
    private String userdataUrl;

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

    public boolean initializeSessionWithToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.error("Token vazio ou nulo");
                return false;
            }

            this.currentToken = token;

            JsonNode userInfo = fetchUserInfoFromExternal(token);
            if (userInfo == null) {
                return false;
            }

            if (!userInfo.has("id_login")) {
                log.error("Resposta do endpoint userdata não contém 'id_login'");
                return false;
            }

            Long idLogin = userInfo.get("id_login").asLong();
            String cookies = userInfo.has("cookies") ? userInfo.get("cookies").asText() : null;

            this.currentIdLogin = idLogin;
            this.sessionCookies = cookies;
            this.currentUserData = userInfo;

            log.info("Sessão inicializada com sucesso para id_login: {}", idLogin);
            return true;

        } catch (Exception e) {
            log.error("Erro ao inicializar sessão com token: {}", e.getMessage(), e);
            return false;
        }
    }

    private JsonNode fetchUserInfoFromExternal(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Hash", externalHash);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("token", token);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(userdataUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody().trim();
                log.info("Resposta do endpoint userdata: {}", responseBody);

                // Tenta interpretar como JSON. Se falhar, converte conteúdo bruto em JSON (ex: { "raw": "..." })
                try {
                    return objectMapper.readTree(responseBody);
                } catch (Exception jsonEx) {
                    log.warn("Resposta não é JSON válido, encapsulando como objeto JSON.");
                    return objectMapper.createObjectNode()
                            .put("raw_response", responseBody);
                }
            }

            log.warn("Resposta inválida do endpoint userdata. Status: {}", response.getStatusCode());
            return null;

        } catch (Exception e) {
            log.error("Erro ao comunicar com o endpoint userdata: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean isSessionValid() {
        return currentToken != null && currentIdLogin != null;
    }

    public void clearSession() {
        this.currentToken = null;
        this.sessionCookies = null;
        this.currentIdLogin = null;
        this.currentUserData = null;
        log.info("Sessão limpa");
    }
}