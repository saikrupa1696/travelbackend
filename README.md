# TravelMate AI - Backend Service

Spring Boot 3.3.4 RESTful API backend for the AI-Powered Personalized Travel Planner web application.

## Core Capabilities
- **Authentication & Authorization**: Stateless JWT authentication with BCrypt password hashing and custom UserDetailsService.
- **Relational Persistence**: MySQL 8.x with Flyway schema migration and Spring Data JPA.
- **AI & Fallback Itinerary Generation**: Configurable integration with Google Gemini or OpenAI APIs with a sophisticated, realistic rule-based fallback generator.
- **Places Integration**: OpenStreetMap Nominatim POI search with in-memory caching and curated fallback catalogs.
- **Budget Calculation**: Group vs. per-person budgeting, categorized breakdown (stays, dining, transit, entry tickets, buffer).
- **Public Trip Sharing**: Unguessable UUID token-based public read-only access.
- **Interactive API Documentation**: Springdoc OpenAPI / Swagger UI at `/swagger-ui.html`.

## Prerequisites
- Java 21 LTS (or Java 25)
- Maven 3.9+
- MySQL Server 8.0+

## Local Execution
1. Create the MySQL database:
   ```sql
   CREATE DATABASE travelmate_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Build and run tests:
   ```bash
   mvn clean test
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   Or run the compiled JAR:
   ```bash
   java -jar target/travel-planner-backend-1.0.0.jar
   ```

## Swagger UI Documentation
Once running, open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```
