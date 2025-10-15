# =========================
# Etap 1: Budowanie aplikacji
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Skopiuj pliki konfiguracyjne i źródła
COPY pom.xml .
COPY src ./src

# Zbuduj aplikację (bez testów)
RUN mvn clean package -DskipTests

# =========================
# Etap 2: Uruchamianie aplikacji
# =========================
FROM eclipse-temurin:21-jdk-alpine

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj zbudowany plik JAR z poprzedniego etapu
COPY --from=builder /app/target/snackTrack-0.0.1-SNAPSHOT.jar app.jar

# Domyślny port aplikacji
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
