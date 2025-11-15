package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
            String url = userDataBaseUrl + (userDataBaseUrl.endsWith("/") ? "" : "/") + token;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Hash", externalHash);
            headers.set("Accept", "application/json");
            // Não definir Accept-Encoding se não quiser lidar com gzip

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("Resposta inválida do endpoint userdata. Status: {}", response.getStatusCode());
                return null;
            }

            // ✅ Extrair cookies do cabeçalho Set-Cookie
            List<String> setCookieHeaders = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
                // Concatenar todos os cookies em uma única string no formato "key1=value1; key2=value2"
                this.sessionCookies = String.join("; ", setCookieHeaders.stream()
                        .map(cookie -> cookie.split(";")[0]) // pega apenas "nome=valor", ignora atributos como Path, Secure etc.
                        .toList());
                log.debug("Cookies de sessão armazenados: {}", this.sessionCookies);
            } else {
                this.sessionCookies = null;
                log.warn("Nenhum cookie Set-Cookie recebido na resposta");
            }

            // ✅ Parsear o corpo JSON (dados do usuário)
            String body = response.getBody().trim();
            try {
                return objectMapper.readTree(body);
            } catch (Exception e) {
                log.warn("Resposta não é JSON válido: {}", body);
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
        // ✅ Não tenta ler cookies do JSON — já foram extraídos em fetchUserData()
        // this.sessionCookies permanece como foi definido em fetchUserData()
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