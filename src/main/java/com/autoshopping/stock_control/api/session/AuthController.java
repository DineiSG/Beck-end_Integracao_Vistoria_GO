/*package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(
        origins = {"http://localhost:3001", "http://localhost:5173"},
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

    @Value("${app.cookies-session.uri}")
    private String cookiesSessionUri; // Caminho da pasta com os cookies

    /**
     * Endpoint para inicializar a sessão buscando os cookies na pasta do servidor
     */
   /* @GetMapping("/initialize-session")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> initializeSession() {
        // Lê os cookies da pasta do servidor
        String cookies = readCookiesFromServerFolder();

        if (cookies == null || cookies.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Cookies de sessão não encontrados na pasta do servidor.");
        }

        boolean success = authService.initializeSessionWithCookies(cookies);

        if (success) {
            return ResponseEntity.ok(new SessionInfo(
                    authService.getCurrentIdLogin().toString(),
                    authService.getCurrentIdLogin().toString(),
                    authService.getCurrentUserData()
            ));
        }

        return ResponseEntity.status(401).body("Falha ao inicializar sessão com os cookies obtidos.");
    }

    /**
     * Lê os cookies de sessão da pasta configurada no servidor
     */
   /* private String readCookiesFromServerFolder() {
        try {
            Path cookiesDir = Paths.get(cookiesSessionUri);
            if (!Files.exists(cookiesDir) || !Files.isDirectory(cookiesDir)) {
                throw new RuntimeException("Diretório de cookies não encontrado: " + cookiesSessionUri);
            }

            // Busca o arquivo .txt mais recente que começa com sess_ e contém dados
            Path cookieFile = Files.list(cookiesDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.startsWith("sess_") && fileName.endsWith(".txt");
                    })
                    .filter(path -> {
                        try {
                            return Files.size(path) > 0;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .max(Comparator.comparingLong(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis();
                        } catch (IOException e) {
                            return 0L;
                        }
                    }))
                    .orElseThrow(() -> new RuntimeException("Nenhum arquivo sess_*.txt com dados encontrado em: " + cookiesDir));

            byte[] content = Files.readAllBytes(cookieFile);
            return new String(content).trim();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo de cookies: " + e.getMessage(), e);
        }
    }

    /**
     * Endpoint para o front-end obter as informações da sessão atual
     * Retorna os dados do usuário em formato JSON
     */
  /*  @GetMapping("/session-info")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> getSessionInfo() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida ou não inicializada");
        }

        JsonNode userData = authService.getCurrentUserData();
        Long idLogin = authService.getCurrentIdLogin();

        SessionInfo sessionInfo = new SessionInfo(
                idLogin != null ? idLogin.toString() : null,
                idLogin != null ? idLogin.toString() : null,
                userData
        );

        return ResponseEntity.ok(sessionInfo);
    }

    /**
     * Endpoint para obter consultas usando a sessão ativa
     */
  /*  @PostMapping("/consultas")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> obterConsultas() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.status(401).body("Cookies de sessão não disponíveis");
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

    /**
     * Endpoint para obter pendências usando a sessão ativa
     */
  /*  @PostMapping("/pendencias")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> obterPendencias() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.status(401).body("Cookies de sessão não disponíveis");
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

    /**
     * Endpoint para limpar a sessão atual
     */
  /*  @PostMapping("/clear-session")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> clearSession() {
        authService.clearSession();
        return ResponseEntity.ok("Sessão limpa com sucesso");
    }

    // Records para request e response
    private record SessionInfo(String idLogin, String token, Object userData) {}
}*/

/*

Código antigo:

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
        origins = {"http://localhost:3001", "http://localhost:5173"},
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

    /**
     * Endpoint para receber os cookies de sessão do front-end e inicializar a sessão
     */
    /*@PostMapping("/initialize-session")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> initializeSession(@RequestBody CookiesRequest cookiesRequest) {
        if (cookiesRequest.cookies() == null || cookiesRequest.cookies().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Cookies de sessão não fornecidos");
        }

        boolean success = authService.initializeSessionWithCookies(cookiesRequest.cookies());

        if (success) {
            return ResponseEntity.ok(new SessionInfo(
                    authService.getCurrentIdLogin().toString(),
                    authService.getCurrentIdLogin().toString(),
                    authService.getCurrentUserData()
            ));
        }

        return ResponseEntity.status(401).body("Falha ao inicializar sessão com os cookies fornecidos");
    }

    /**
     * Endpoint para o front-end obter as informações da sessão atual
     * Retorna os dados do usuário em formato JSON
     */
    /*@GetMapping("/session-info")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> getSessionInfo() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida ou não inicializada");
        }

        JsonNode userData = authService.getCurrentUserData();
        Long idLogin = authService.getCurrentIdLogin();

        SessionInfo sessionInfo = new SessionInfo(
                idLogin != null ? idLogin.toString() : null,
                idLogin != null ? idLogin.toString() : null,
                userData
        );

        return ResponseEntity.ok(sessionInfo);
    }

    /**
     * Endpoint para obter consultas usando a sessão ativa
     */
    /*@PostMapping("/consultas")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> obterConsultas() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.status(401).body("Cookies de sessão não disponíveis");
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

    /**
     * Endpoint para obter pendências usando a sessão ativa
     */
    /*@PostMapping("/pendencias")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> obterPendencias() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.status(401).body("Cookies de sessão não disponíveis");
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

    /**
     * Endpoint para limpar a sessão atual
     */
    /*@PostMapping("/clear-session")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> clearSession() {
        authService.clearSession();
        return ResponseEntity.ok("Sessão limpa com sucesso");
    }

    // Records para request e response
    private record CookiesRequest(String cookies) {}
    private record SessionInfo(String idLogin, String token, Object userData) {}
}*/

package com.autoshopping.stock_control.api.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(
        origins = {"http://localhost:3001", "http://localhost:5173", "http://177.70.23.73:5173"},
        allowCredentials = "false",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Value("${app.userdata.url}")
    private String userDataURL;

    @Value("${app.consultas.url}")
    private String consultasUrl;

    @Value("${app.pendencias.url}")
    private String pendenciasUrl;

    @Value("${app.cookies-session.uri}")
    private String cookiesSessionUri; // Ex: E:/Projetos/Integracao_VistoriaGO

    /**
     * Inicializa a sessão: lê o arquivo .txt, extrai o ID, obtém token e dados do usuário
     */
    @GetMapping("/initialize-session")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> initializeSession() {
        try {
            // 1. Ler apenas o arquivo JSON da pasta configurada
            Path jsonFile = Paths.get(cookiesSessionUri, "user_data.json");
            if (!Files.exists(jsonFile)) {
                return ResponseEntity.status(404).body("Arquivo JSON não encontrado: " + jsonFile);
            }

            String jsonContent = Files.readString(jsonFile, StandardCharsets.UTF_8);
            JsonNode userDataJson = objectMapper.readTree(jsonContent);

            // 2. Validar estrutura e extrair ID de dados.Login.id
            if (userDataJson == null ||
                    !userDataJson.has("dados") ||
                    !userDataJson.get("dados").has("Login") ||
                    !userDataJson.get("dados").get("Login").has("id")) {
                return ResponseEntity.status(500).body("JSON inválido: campo 'dados.Login.id' ausente.");
            }

            JsonNode loginNode = userDataJson.get("dados").get("Login");
            Long userId = loginNode.get("id").isTextual()
                    ? Long.parseLong(loginNode.get("id").asText().trim())
                    : loginNode.get("id").asLong();

            log.info("✅ Sessão inicializada com ID do JSON: {}", userId);

            // 3. Armazenar sessão (mesmo sem cookies reais)
            authService.setSession(userId, "PHPSESSID=simulated", userDataJson);

            // 4. Retornar ao frontend
            return ResponseEntity.ok(Map.of(
                    "idLogin", userId,
                    "token", userId.toString(),
                    "userData", userDataJson
            ));

        } catch (Exception e) {
            log.error("Erro ao inicializar sessão a partir do JSON", e);
            return ResponseEntity.status(400).body("Erro ao carregar sessão: " + e.getMessage());
        }
    }
    // === Endpoints mantidos conforme original ===

    @GetMapping("/session-info")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> getSessionInfo() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida ou não inicializada");
        }
        return ResponseEntity.ok(new SessionInfo(
                authService.getCurrentIdLogin().toString(),
                authService.getCurrentIdLogin().toString(),
                authService.getCurrentUserData()
        ));
    }

    @PostMapping("/consultas")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> obterConsultas() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(consultasUrl, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    return ResponseEntity.ok(objectMapper.readTree(response.getBody()));
                } catch (Exception e) {
                    return ResponseEntity.ok(response.getBody());
                }
            }
            return ResponseEntity.status(response.getStatusCode()).body("Erro ao obter consultas");
        } catch (Exception e) {
            log.error("Erro ao chamar /consultas: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erro interno ao obter consultas");
        }
    }

    @PostMapping("/pendencias")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> obterPendencias() {
        if (!authService.isSessionValid()) {
            return ResponseEntity.status(401).body("Sessão inválida");
        }

        String cookies = authService.getSessionCookies();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookies);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(pendenciasUrl, HttpMethod.POST, entity, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            }
            return ResponseEntity.status(response.getStatusCode()).body("Erro ao obter pendências");
        } catch (Exception e) {
            log.error("Erro ao chamar /pendencias: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erro interno ao obter pendências");
        }
    }

    @PostMapping("/clear-session")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> clearSession() {
        authService.clearSession();
        return ResponseEntity.ok("Sessão limpa com sucesso");
    }

    private record SessionInfo(String idLogin, String token, Object userData) {}
}