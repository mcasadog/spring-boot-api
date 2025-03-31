# Spring Boot API Project - Deployment Guide

This document provides instructions for deploying the Spring Boot API project to various environments.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 14 or higher
- Git (optional, for version control)

## Local Development Setup

1. Clone the repository (if using Git):
   ```
   git clone <repository-url>
   cd spring-boot-api
   ```

2. Configure the database:
   - Create a PostgreSQL database named `apidb`
   - Update database credentials in `src/main/resources/application.yml` if needed

3. Build the application:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

5. Access the application:
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - API Documentation: http://localhost:8080/api/v3/api-docs

## Production Deployment

### Option 1: Traditional Deployment

1. Build the application:
   ```
   mvn clean package -Pprod
   ```

2. Copy the JAR file to your server:
   ```
   scp target/spring-boot-api-1.0-SNAPSHOT.jar user@your-server:/path/to/deployment
   ```

3. Create an application.yml file on the server with production settings:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://your-db-host:5432/apidb
       username: your-db-username
       password: your-db-password
   
   jwt:
     secret: your-secure-jwt-secret-key
     expiration: 86400000
   ```

4. Run the application:
   ```
   java -jar spring-boot-api-1.0-SNAPSHOT.jar --spring.config.location=file:/path/to/application.yml
   ```

5. Configure a reverse proxy (Nginx or Apache) to forward requests to the application.

### Option 2: Docker Deployment

1. Build the Docker image:
   ```
   docker build -t spring-boot-api .
   ```

2. Run the container:
   ```
   docker run -d -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/apidb \
     -e SPRING_DATASOURCE_USERNAME=your-db-username \
     -e SPRING_DATASOURCE_PASSWORD=your-db-password \
     -e JWT_SECRET=your-secure-jwt-secret-key \
     --name spring-boot-api spring-boot-api
   ```

### Option 3: Kubernetes Deployment

1. Apply the Kubernetes manifests:
   ```
   kubectl apply -f k8s/
   ```

2. Configure secrets for database credentials and JWT secret:
   ```
   kubectl create secret generic api-secrets \
     --from-literal=db-password=your-db-password \
     --from-literal=jwt-secret=your-secure-jwt-secret-key
   ```

## Environment Variables

The application can be configured using the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database connection URL | jdbc:postgresql://localhost:5432/apidb |
| `SPRING_DATASOURCE_USERNAME` | Database username | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Database password | postgres |
| `JWT_SECRET` | Secret key for JWT signing | (a secure default in application.yml) |
| `JWT_EXPIRATION` | JWT token expiration in milliseconds | 86400000 (24 hours) |
| `SERVER_PORT` | Port for the application to listen on | 8080 |

## Health Checks

The application provides health endpoints through Spring Boot Actuator:

- Health check: http://localhost:8080/api/actuator/health
- Metrics: http://localhost:8080/api/actuator/metrics

## Troubleshooting

1. Database connection issues:
   - Verify database credentials
   - Ensure the database server is running and accessible
   - Check network connectivity between the application and database

2. Application startup issues:
   - Check the logs for error messages
   - Verify Java version is 17 or higher
   - Ensure all required environment variables are set

3. Authentication issues:
   - Verify JWT secret is properly configured
   - Check token expiration settings
   - Ensure user credentials are correct

## Support

For additional support, please contact the development team.
