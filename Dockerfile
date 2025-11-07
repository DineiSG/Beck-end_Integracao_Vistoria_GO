FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o pom.xml e a pasta src
COPY pom.xml .
COPY src ./src

# Verifica o conteúdo
RUN ls -la /app

# Executa o build
RUN mvn clean package -DskipTests

# Verifica se o JAR foi gerado
RUN ls -la /app/target/

# --- Estágio Final ---
FROM eclipse-temurin:17-jre

# Criando o cliente MariaDB
RUN apt-get update && \
    apt-get install -y --no-install-recommends mariadb-client && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# ===== ADICIONE AQUI A SOLUÇÃO DO CERTIFICADO =====
# Copiar o certificado para dentro da imagem
COPY credtudo.pem /tmp/credtudo.pem

# Importar o certificado na truststore do Java
RUN keytool -import -trustcacerts -alias credtudo \
    -file /tmp/credtudo.pem \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit -noprompt

# Remover o arquivo temporário (opcional)
RUN rm /tmp/credtudo.pem
# ===== FIM DA SOLUÇÃO DO CERTIFICADO =====

# Copia o JAR gerado
COPY --from=build /app/target/stock_control-2.2.0.jar /app/stock_control-2.2.0.jar

EXPOSE 8090
ENTRYPOINT [ "java", "-jar", "/app/stock_control-2.2.0.jar" ]

