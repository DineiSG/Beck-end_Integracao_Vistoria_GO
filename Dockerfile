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
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/stock_control-2.2.0.jar /app/stock_control-2.2.0.jar

EXPOSE 8090
ENTRYPOINT [ "java", "-jar", "/app/stock_control-2.2.0.jar" ]