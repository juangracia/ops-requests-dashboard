# Quick Start Guide

## Prerequisites

- Java 17 or higher installed
- Docker and Docker Compose installed (for database)
- Maven 3.6+ (or use the included Maven Wrapper)

## Step 1: Start the Database

```bash
./start-db.sh
```

This will start a PostgreSQL database in Docker on port 25432.

## Step 2: Run the Application

```bash
mvn spring-boot:run
```

Or using Maven Wrapper:
```bash
./mvnw spring-boot:run
```

The application will:
- Start on port 28080
- Automatically run Flyway migrations
- Seed the database with test data

## Step 3: Test the API

### Option 1: Using Swagger UI

Open your browser and navigate to:
```
http://localhost:28080/swagger-ui.html
```

### Option 2: Using curl

#### Login as Admin
```bash
curl -X POST http://localhost:28080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

Save the token from the response.

#### Get Requests (use the token)
```bash
curl -X GET http://localhost:28080/api/requests \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Test Credentials

See CREDENTIALS.md for all test user credentials.

## Running Tests

```bash
mvn test
```

## Stopping the Application

- Press `Ctrl+C` to stop the Spring Boot application
- Run `docker-compose down` to stop the database

## Troubleshooting

### Port Already in Use
If port 28080 or 25432 is already in use, you can change them:

```bash
SERVER_PORT=8080 DB_PORT=5432 mvn spring-boot:run
```

### Database Connection Issues
Make sure the database container is running:
```bash
docker-compose ps
```

View database logs:
```bash
docker-compose logs -f postgres
```

### Clean Database
To reset the database:
```bash
docker-compose down -v
./start-db.sh
mvn spring-boot:run
```

## Development Tips

### Hot Reload
For development with hot reload, use Spring Boot DevTools by adding it to your pom.xml.

### View Database
You can connect to the database using any PostgreSQL client:
- Host: localhost
- Port: 25432
- Database: opsrequests
- User: postgres
- Password: postgres

### API Testing
Import the Swagger/OpenAPI spec into Postman or Insomnia for easier API testing.

## Next Steps

1. Review the API documentation at `/swagger-ui.html`
2. Test the complete workflow: create request → approve → change status
3. Explore the role-based access control with different users
4. Review the audit events and comments functionality
