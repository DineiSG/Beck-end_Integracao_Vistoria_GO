package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.util.StringUtils.truncate;

@Getter
@Service
@Slf4j
public class AuthService {

    @Value("${app.token.url}")
    private String tokenUrl;

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

    /**
     * Define os cookies de sessão recebidos do front-end e inicializa a sessão
     */
    public boolean initializeSessionWithCookies(String cookies) {
        try {
            if (cookies == null || cookies.trim().isEmpty()) {
                log.error("Cookies de sessão vazios ou nulos");
                return false;
            }

            this.sessionCookies = cookies;
            log.info("Cookies de sessão recebidos do front-end");

            // Obtém o ID numérico do login
            Long loginId = fetchLoginId(cookies);
            if (loginId != null) {
                this.currentIdLogin = loginId;

                // Obtém os dados do usuário
                this.currentUserData = fetchUserData(loginId);
                if (this.currentUserData != null) {
                    log.info("Sessão inicializada com sucesso para loginId: {}", loginId);
                    return true;
                }
            } else {
                log.error("Falha ao obter ID de login");
            }

            return false;
        } catch (Exception e) {
            log.error("Erro ao inicializar sessão com cookies: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Obtém o ID numérico do login.
     * Observação: nesse sistema, o campo "token" na resposta da API é na verdade o id_login (número).
     */
    private Long fetchLoginId(String cookies) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, cookies);
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Resposta do endpoint de token: {}", response.getBody());

                JsonNode json = objectMapper.readTree(response.getBody());
                if (json.has("dados") && json.get("dados").has("token")) {
                    String tokenStr = json.get("dados").get("token").asText().trim();
                    try {
                        return Long.parseLong(tokenStr);
                    } catch (NumberFormatException e) {
                        log.error("Valor de 'token' não é um número válido: {}", tokenStr);
                        return null;
                    }
                } else {
                    log.warn("Campo 'dados.token' não encontrado na resposta");
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Erro ao obter ID de login: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca os dados completos do usuário usando o loginId
     */
    private JsonNode fetchUserData(Long loginId) {
        try {
            String url = userDataUrl + loginId;
            log.info("Buscando dados do usuário em: {}", url);
            log.info("Enviando Hash no header: {}", externalHash);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Hash", externalHash);
            headers.set(HttpHeaders.COOKIE, sessionCookies);
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            log.info("Resposta recebida (resumo): {}", truncate(response.getBody(), 300));

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return objectMapper.readTree(response.getBody());
            }

            log.warn("Falha ao obter dados do usuário. Status: {}", response.getStatusCode());
            return null;

        } catch (Exception e) {
            log.error("Erro ao obter dados do usuário: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Verifica se a sessão está válida
     */
    public boolean isSessionValid() {
        return currentUserData != null && sessionCookies != null;
    }

    /**
     * Limpa a sessão atual
     */
    public void clearSession() {
        this.sessionCookies = null;
        this.currentIdLogin = null;
        this.currentUserData = null;
        log.info("Sessão limpa");
    }
}