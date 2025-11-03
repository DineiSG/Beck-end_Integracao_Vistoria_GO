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
            // 1. Ler ID do arquivo PHP serializado
            String userIdStrFromFile = readUserIdFromSessionFile();
            Long userIdFromFile = Long.parseLong(userIdStrFromFile.trim());
            log.info("ID extraído do arquivo PHP: {}", userIdFromFile);

            // 2. Ler JSON do mesmo diretório
            Path jsonFile = Paths.get(cookiesSessionUri, "user_data.json"); // ou "usuario.json", etc.
            if (!Files.exists(jsonFile)) {
                return ResponseEntity.status(404).body("Arquivo JSON de usuário não encontrado: " + jsonFile);
            }

            String jsonContent = Files.readString(jsonFile, StandardCharsets.UTF_8);
            JsonNode userDataJson = objectMapper.readTree(jsonContent);

            // 3. Validar e extrair ID do JSON
            if (userDataJson == null ||
                    !userDataJson.has("dados") ||
                    !userDataJson.get("dados").has("Login") ||
                    !userDataJson.get("dados").get("Login").has("id")) {
                return ResponseEntity.status(500).body("JSON inválido: campo 'dados.Login.id' não encontrado.");
            }

            JsonNode loginNode = userDataJson.get("dados").get("Login");
            Long userIdFromJson = loginNode.get("id").isTextual()
                    ? Long.parseLong(loginNode.get("id").asText().trim())
                    : loginNode.get("id").asLong();

            log.info("ID do JSON: {}", userIdFromJson);

            // 4. Comparar
            if (!userIdFromFile.equals(userIdFromJson)) {
                log.warn("ID do arquivo ({}) ≠ ID do JSON ({})", userIdFromFile, userIdFromJson);
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Identidade não verificada",
                        "fileId", userIdFromFile,
                        "jsonId", userIdFromJson
                ));
            }

            // 5. Sessão válida
            authService.setSession(userIdFromFile, "PHPSESSID=simulated", userDataJson);

            log.info("Sessão validada com sucesso para ID: {}", userIdFromFile);
            return ResponseEntity.ok(Map.of(
                    "idLogin", userIdFromFile,
                    "token", userIdFromFile.toString(),
                    "userData", userDataJson
            ));

        } catch (Exception e) {
            log.error("Erro ao inicializar sessão", e);
            return ResponseEntity.status(400).body("Erro: " + e.getMessage());
        }
    }

    /**
     * Lê o arquivo .txt mais recente com serialização PHP e extrai o ID do usuário
     */
    private String readUserIdFromSessionFile() throws IOException {
        Path dir = Paths.get(cookiesSessionUri);
        log.info(" Diretório de sessão configurado: {}", dir.toAbsolutePath());

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new RuntimeException("Diretório de sessão não encontrado: " + dir.toAbsolutePath());
        }

        Path sessionFile = Files.list(dir)
                .filter(path -> {
                    String name = path.getFileName().toString();
                    return name.startsWith("sess_") && name.endsWith(".txt");
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
                .orElseThrow(() -> new RuntimeException("Nenhum arquivo sess_*.txt válido encontrado em: " + dir));

        // Lê o arquivo com tratamento de BOM
        String content = readStringWithoutBom(sessionFile);
        log.info("Arquivo lido: {}", sessionFile.getFileName());
        log.debug("Conteúdo (primeiros 150 chars): '{}'", content.substring(0, Math.min(150, content.length())));

        // Remove qualquer caractere invisível no início
        content = content.replaceAll("^[\\x00-\\x1F]+", "").trim();

        if (!content.startsWith("login|a:")) {
            log.warn("O arquivo não começa com 'login|a:', mas vamos tentar extrair o ID mesmo assim.");
        }

        String userId = extractUserIdFromPhpSerialized(content);
        if (userId == null) {
            log.error(" Não foi possível extrair o 'id' da serialização PHP.");
            throw new RuntimeException("Campo 'id' não encontrado no arquivo: " + sessionFile.getFileName());
        }

        log.warn("ID do usuário extraído com sucesso: {}", userId);
        return userId;
    }

    /**
     * Lê arquivo removendo BOM (Byte Order Mark) comum em arquivos UTF-8 do Windows
     */
    private String readStringWithoutBom(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            // UTF-8 BOM encontrado — pula os 3 primeiros bytes
            return new String(bytes, 3, bytes.length - 3, java.nio.charset.StandardCharsets.UTF_8);
        }
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Extrai o ID do formato: s:2:"id";s:N:"1234"
     */
    private String extractUserIdFromPhpSerialized(String serialized) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("s:2:\"id\";s:\\d+:\"(\\d+)\"");
        java.util.regex.Matcher matcher = pattern.matcher(serialized);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
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