# fish-time

Backend REST API service for the Fish Time platform, built with Spring Boot and Kotlin.

## Project Overview

fish-time is a Spring Boot application that serves as the backend for the Fish Time platform. It provides RESTful APIs that are consumed by the [fish-time-web](https://github.com/Kireobat/fish-time-web.git) frontend.

### Prerequisites

- JDK 21 or later
- Kotlin 1.9+
- Maven
- Database (PostgreSQL recommended)

### Installation

1. Clone the repository

```sh
git clone https://github.com/Kireobat/fish-time.git
cd fish-time
```

2. Build the project

```sh
mvn clean install
```

### Development

Run the application in development mode:

```sh
mvn spring-boot:run -Dspring.profiles.active=local
```

```sh
mvn spring-boot:run "-Dspring.profiles.active=local"
```

## API Documentation

The API documentation is automatically generated using SpringDoc OpenAPI:

- Swagger UI: <http://localhost:8080/fish-time/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/fish-time/v3/api-docs>

### Building for Production

Run the packaged application:

```sh
# Build the JAR
mvn package

# Run the application
java -jar target/fish-time-0.0.1-SNAPSHOT.jar
```

### Docker Support

Docker Compose is also supported:

```sh
# Build and start containers
docker-compose up

# Run in detached mode
docker-compose up -d
```

### Configuration

- `application.properties` - Main configuration file
- `application-local.properties` - Local-specific configuration
- `application-prod.properties` - Production configuration

### Technical Stack

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [Kotlin](https://kotlinlang.org/) - Programming language
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data access
- [PostgreSQL](https://www.postgresql.org/) - Database (recommended)
- [SpringDoc OpenAPI](https://springdoc.org/) - API documentation
- [Spring Security](https://spring.io/projects/spring-security) - Authentication and authorization

### License

See the LICENSE file for details.
