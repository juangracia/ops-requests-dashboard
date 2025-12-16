# Ops Requests Dashboard Backend

Spring Boot 3 backend API for the Operations Requests Dashboard.

## Technologies

- Java 17
- Spring Boot 3.2.5
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Flyway for database migrations
- Lombok
- OpenAPI/Swagger UI
- Maven

## Prerequisites

- Java 17 or higher
- PostgreSQL database
- Maven 3.6+

## Configuration

The application can be configured using environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| SERVER_PORT | 38081 | Application server port |
| DB_HOST | localhost | Database host |
| DB_PORT | 54329 | Database port |
| DB_NAME | opsrequests | Database name |
| DB_USER | postgres | Database username |
| DB_PASSWORD | postgres | Database password |
| JWT_SECRET | (see application.yml) | JWT signing secret (min 32 chars) |

## Database Setup

1. Create PostgreSQL database:
```bash
createdb -U postgres opsrequests
```

2. The application will automatically run Flyway migrations on startup

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Maven Wrapper

```bash
./mvnw spring-boot:run
```

### With custom environment variables

```bash
DB_HOST=localhost DB_PORT=54329 mvn spring-boot:run
```

## Building the Application

```bash
mvn clean package
```

Run the JAR:
```bash
java -jar target/ops-requests-backend-1.0.0-SNAPSHOT.jar
```

## Running Tests

```bash
mvn test
```

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:38081/swagger-ui.html
```

## Seed Data

The application seeds the database with test data:

### Users
- **Admin**: admin@example.com / admin123
- **Manager**: manager@example.com / manager123
- **Employee 1**: employee1@example.com / employee123
- **Employee 2**: employee2@example.com / employee123

### Request Types
- PURCHASE - Purchase Request
- IT_ACCESS - IT Access Request
- HR - HR Request
- TRAVEL - Travel Request

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - Login and get JWT token
- GET `/api/auth/me` - Get current user info

### Requests
- GET `/api/requests` - List requests (filtered by role)
- POST `/api/requests` - Create request
- GET `/api/requests/{id}` - Get request detail
- PUT `/api/requests/{id}` - Update request
- POST `/api/requests/{id}/cancel` - Cancel request
- POST `/api/requests/{id}/approve` - Approve request (manager)
- POST `/api/requests/{id}/reject` - Reject request (manager)
- POST `/api/requests/{id}/status` - Change status (admin)
- POST `/api/requests/{id}/comments` - Add comment

### Request Types
- GET `/api/request-types` - List active types
- POST `/api/request-types` - Create type (admin)
- PUT `/api/request-types/{id}` - Update type (admin)
- DELETE `/api/request-types/{id}` - Soft delete type (admin)

### Users
- GET `/api/users` - List all users (admin)

## Security

The API uses JWT-based authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

### Roles
- **EMPLOYEE**: Can create and manage own requests
- **MANAGER**: Can approve/reject requests assigned to them
- **ADMIN**: Full access to all operations

## CORS Configuration

The backend is configured to accept requests from:
- http://localhost:33456 (frontend)

## Project Structure

```
src/
├── main/
│   ├── java/com/opsrequests/
│   │   ├── config/          # Security, JWT, OpenAPI config
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── service/         # Business logic
│   │   ├── security/        # JWT and security classes
│   │   └── exception/       # Exception handling
│   └── resources/
│       ├── db/migration/    # Flyway migrations
│       └── application.yml  # Application config
└── test/
    └── java/com/opsrequests/
        ├── service/         # Unit tests
        └── integration/     # Integration tests
```
