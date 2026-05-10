# LoanLens

Loan Portfolio Risk Monitoring and Early Warning System built with Spring Boot.

## Features

- Loan portfolio management and tracking
- Risk scoring engine
- Early warning scheduler for at-risk loans
- JWT-based authentication and authorization
- REST API with Swagger/OpenAPI documentation

## Tech Stack

- **Java 17**
- **Spring Boot 3.2** (Web, Security, Data JPA, Validation)
- **PostgreSQL** — primary database
- **JWT (JJWT 0.12.3)** — stateless authentication
- **Lombok** — boilerplate reduction
- **Springdoc OpenAPI 2.3** — API docs

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/harg-15/Loanlens.git
   cd loanlens
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE loanlens;
   ```

3. **Configure environment variables**

   Copy `.env.example` to `.env` and fill in your values:
   ```bash
   cp .env.example .env
   ```

   | Variable | Description |
   |---|---|
   | `DB_URL` | JDBC URL for PostgreSQL |
   | `DB_USERNAME` | Database username |
   | `DB_PASSWORD` | Database password |
   | `JWT_SECRET` | 256-bit hex secret for signing JWTs |
   | `JWT_EXPIRATION` | Token expiry in milliseconds (default: 86400000) |
   | `ADMIN_USERNAME` | Default admin username |
   | `ADMIN_PASSWORD` | Default admin password |

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The server starts on `http://localhost:8080`.

### API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:
```
http://localhost:8080/api-docs
```

## Running Tests

```bash
mvn test
```

Tests use an in-memory H2 database and do not require a running PostgreSQL instance.

## Project Structure

```
src/
├── main/
│   ├── java/com/loanlens/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Spring Data JPA repositories
│   │   ├── entity/         # JPA entities
│   │   ├── dto/            # Request/response DTOs
│   │   ├── security/       # JWT filter, config
│   │   ├── scoring/        # Risk scoring engine
│   │   └── scheduler/      # Early warning scheduler
│   └── resources/
│       └── application.properties
└── test/
```

## License

MIT
