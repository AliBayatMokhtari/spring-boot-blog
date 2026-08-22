# Blog Management System

Spring Boot REST API for a blogging platform: users can register, sign in, publish and manage their own posts, and interact with posts through comments and likes. Guests can read published posts; only authenticated users can create or change content.

Full product requirements: [PRD.md](PRD.md)

## Prerequisites

- Java 21
- Maven 3.9+
- Docker and Docker Compose (for PostgreSQL)

Connection settings live in `src/main/resources/application.properties`.

## PostgreSQL with Docker Compose

Start the database defined in `docker-compose.yml`:

```bash
docker compose up -d
```

Stop the database:

```bash
docker compose down
```

To stop and also remove the data volume:

```bash
docker compose down -v
```

## Local installation

Clone the repository and install dependencies:

```bash
mvn install
```

### Clean install (skip tests)

```bash
mvn clean install -DskipTests
```

### Run the application

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080` by default.
