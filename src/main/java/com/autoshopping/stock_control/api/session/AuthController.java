package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(
        origins = {"http://localhost:3001", "http://localhost:5173", "http://177.70.23.73:5173", "https://ambteste.credtudo.com.br/", "https://credtudo.com.br" },
        allowCredentials = "false",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class AuthController {

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Value("${app.consultas.url}")
    private String consultasUrl;

    @Value("${app.pendencias.url}")
    private String pendenciasUrl;

    @PostMapping("/initialize-session")
    public ResponseEntity<?> initializeSession(@RequestBody TokenRequest tokenRequest) {
        if (tokenRequest.token() == null || tokenRequest.token().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token não fornecido");
        }

        boolean success = authService.initializeSessionWithToken(tokenRequest.token());

        if (success) {
            return ResponseEntity.ok(new SessionInfo(
                    authService.getCurrentIdLogin().toString(),
                    tokenRequest.token(),
                    authService.getCurrentUserData()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha ao inicializar sessão com o token fornecido");
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão inválida ou não inicializada");
        }

        JsonNode userData = authService.getCurrentUserData();
        Long idLogin = authService.getCurrentIdLogin();

        SessionInfo sessionInfo = new SessionInfo(
                idLogin != null ? idLogin.toString() : null,
                authService.getCurrentToken(),
                userData
        );

        return ResponseEntity.ok(sessionInfo);
    }

    @PostMapping("/consultas")
    public ResponseEntity<?> obterConsultas() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cookies de sessão não disponíveis");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(consultasUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                return ResponseEntity.ok(objectMapper.readTree(response.getBody()));
            } catch (Exception e) {
                return ResponseEntity.ok(response.getBody());
            }
        }

        return ResponseEntity.status(response.getStatusCode())
                .body("Erro ao obter consultas");
    }

    @PostMapping("/pendencias")
    public ResponseEntity<?> obterPendencias() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cookies de sessão não disponíveis");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Object> response = restTemplate.exchange(pendenciasUrl, HttpMethod.POST, entity, Object.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }

        return ResponseEntity.status(response.getStatusCode())
                .body("Erro ao obter pendências");
    }

    @PostMapping("/clear-session")
    public ResponseEntity<?> clearSession() {
        authService.clearSession();
        return ResponseEntity.ok("Sessão limpa com sucesso");
    }

    private record TokenRequest(String token) {}
    private record SessionInfo(String idLogin, String token, Object userData) {}
}
