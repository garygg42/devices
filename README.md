# Device Management API

A Spring Boot REST API for managing device resources.

## Features

- CRUD operations for devices
- Filtering and pagination
- Validation
- OpenAPI (Swagger) documentation
- PostgreSQL persistence
- Dockerized setup

## Build and Run

1. Build the application and Docker image:
   ```bash
   ./gradlew clean build bootBuildImage
   
2. Start the services using Docker Compose:
   ```bash
   docker compose up -d
   
3. Open the API documentation in your browser: http://localhost:8080/swagger-ui/index.html