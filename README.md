# Internal Ops Requests Dashboard – Workflow MVP

A production-ready internal tool for managing operational requests (purchase requests, IT access requests, HR requests, travel requests) in small to medium companies. Employees submit requests, managers approve or reject them, and ops admins track everything through completion.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Demo Credentials](#demo-credentials)
- [API Documentation](#api-documentation)
- [Key Workflows](#key-workflows)
- [Testing Strategy](#testing-strategy)
- [Verification Checklist](#verification-checklist)
- [Future Improvements](#future-improvements)
- [Documentation](#documentation)
- [Production Deployment](#production-deployment)
- [License](#license)

## Overview

This project demonstrates a complete, business-minded approach to building internal tooling:

- **Clear Requirements**: Well-documented scope, roles, and rules
- **Clean Architecture**: Layered Spring Boot backend with proper separation of concerns
- **End-to-End Delivery**: Full stack from database to UI with tests and CI
- **Production-Aware**: Docker Compose, environment configuration, proper error handling
- **Responsible AI Usage**: Documented AI-assisted development with human verification

### Who Is This For?

- **Employees** who need to submit operational requests
- **Managers** who approve or reject their team's requests
- **Ops Admins** who oversee all requests and manage the system

## Features

### Employee Features
- Register and log in securely (select role during registration: Employee, Manager, or Admin)
- Create requests with type, title, description, amount, and priority
- View and filter personal requests
- Edit requests while in SUBMITTED status
- Cancel requests before approval
- Add comments to requests

### Manager Features
- View approval queue for team members
- Approve or reject requests with required comments
- Filter by status, type, and priority
- View complete request history and audit trail

### Admin Features
- View all requests across the organization
- Change request status (APPROVED → IN_PROGRESS → DONE)
- Manage request types (create, edit, deactivate)
- Full audit trail visibility

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3, Spring Security, JPA/Hibernate |
| Database | PostgreSQL 15 with Flyway migrations |
| Authentication | JWT (JSON Web Tokens) |
| Frontend | Next.js 14, TypeScript, Tailwind CSS |
| API Docs | OpenAPI/Swagger |
| Testing | JUnit 5, Playwright E2E |
| DevOps | Docker Compose, GitHub Actions CI |

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Next.js)                       │
│                         Port: 33456                             │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────────┐  │
│  │  Login  │  │ Requests │  │ Approvals│  │  Admin Panel    │  │
│  └─────────┘  └──────────┘  └──────────┘  └─────────────────┘  │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP/REST
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       BACKEND (Spring Boot)                     │
│                         Port: 38081                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    REST Controllers                       │  │
│  │   AuthController │ RequestController │ AdminController    │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    Service Layer                          │  │
│  │   AuthService │ RequestService │ RequestTypeService       │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   Security (JWT)                          │  │
│  │   JwtTokenProvider │ JwtAuthFilter │ SecurityConfig       │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                  Repository Layer (JPA)                   │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────┘
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Database                        │
│                         Port: 54329                             │
│  ┌────────┐ ┌─────────────┐ ┌─────────┐ ┌───────────────────┐  │
│  │ users  │ │request_types│ │requests │ │request_audit_events│  │
│  └────────┘ └─────────────┘ └─────────┘ └───────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Data Model

```
┌──────────────┐       ┌──────────────┐       ┌──────────────────┐
│    users     │       │request_types │       │     requests     │
├──────────────┤       ├──────────────┤       ├──────────────────┤
│ id           │       │ id           │       │ id               │
│ email        │       │ code         │       │ requester_id  ──►│users
│ password     │       │ name         │       │ manager_id    ──►│users
│ role         │       │ active       │       │ type_id       ──►│request_types
│ manager_id ──┼──┐    └──────────────┘       │ title            │
│ active       │  │                           │ description      │
└──────────────┘  │                           │ amount           │
      ▲           │                           │ priority         │
      │           │                           │ status           │
      └───────────┘                           │ created_at       │
                                              │ updated_at       │
                                              └──────────────────┘
                                                      │
                    ┌─────────────────────────────────┼─────────────────┐
                    │                                 │                 │
                    ▼                                 ▼                 ▼
          ┌─────────────────┐              ┌────────────────────────────┐
          │request_comments │              │  request_audit_events      │
          ├─────────────────┤              ├────────────────────────────┤
          │ id              │              │ id                         │
          │ request_id      │              │ request_id                 │
          │ author_id       │              │ actor_id                   │
          │ comment         │              │ event_type                 │
          │ created_at      │              │ from_status                │
          └─────────────────┘              │ to_status                  │
                                           │ note                       │
                                           │ created_at                 │
                                           └────────────────────────────┘
```

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Git

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/juangracia/ops-requests-dashboard.git
   cd ops-requests-dashboard
   ```

2. **Start all services**
   ```bash
   docker compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:33456
   - Backend API: http://localhost:38081
   - Swagger UI: http://localhost:38081/swagger-ui.html

4. **Log in with demo credentials** (see below)

### Development Setup

For local development without Docker:

1. **Start PostgreSQL only**
   ```bash
   docker compose up postgres
   ```

2. **Run backend**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. **Run frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Configuration

All services use configurable ports via environment variables:

| Service | Default Port | Environment Variable |
|---------|-------------|---------------------|
| PostgreSQL | 54329 | `DB_PORT` |
| Backend API | 38081 | `BACKEND_PORT` |
| Frontend | 33456 | `FRONTEND_PORT` |

### Customizing Ports

Create a `.env` file in the project root:

```env
DB_PORT=54329
DB_NAME=opsrequests
DB_USER=postgres
DB_PASSWORD=postgres
BACKEND_PORT=38081
FRONTEND_PORT=33456
JWT_SECRET=your-256-bit-secret-key-here-must-be-at-least-32-chars-long
```

## Demo Credentials

### Self-Registration
Users can register their own accounts and **select their role** (Employee, Manager, or Admin) during registration. This is useful for testing different workflows.

### Seeded Accounts (Local Development)

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@example.com | admin123 |
| Manager | manager@example.com | manager123 |
| Employee 1 | employee1@example.com | employee123 |
| Employee 2 | employee2@example.com | employee123 |

### Production Test Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | myadmin@test.com | Test123456! |
| Employee | railwayuser@example.com | Test123456! |

**Seed Data Includes:**
- 4 request types: Purchase Request, IT Access Request, HR Request, Travel Request
- Sample requests in various statuses
- Audit trail for each status change

## API Documentation

Interactive API documentation is available at:
- **Swagger UI**: http://localhost:38081/swagger-ui.html
- **OpenAPI Spec**: http://localhost:38081/v3/api-docs

### Key Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login, returns JWT | No |
| GET | `/api/auth/me` | Get current user | Yes |
| GET | `/api/requests` | List requests (filtered by role) | Yes |
| POST | `/api/requests` | Create new request | Yes |
| GET | `/api/requests/{id}` | Get request details | Yes |
| PUT | `/api/requests/{id}` | Update request | Yes (Owner) |
| POST | `/api/requests/{id}/cancel` | Cancel request | Yes (Owner) |
| POST | `/api/requests/{id}/approve` | Approve request | Yes (Manager) |
| POST | `/api/requests/{id}/reject` | Reject request | Yes (Manager) |
| POST | `/api/requests/{id}/status` | Change status | Yes (Admin) |
| POST | `/api/requests/{id}/comments` | Add comment | Yes |
| GET | `/api/request-types` | List request types | Yes |
| POST | `/api/request-types` | Create type | Yes (Admin) |

## Key Workflows

### 1. Submit Request (Employee)
```
Employee creates request → Status: SUBMITTED → Audit event logged
```

### 2. Approve/Reject (Manager)
```
Manager reviews request → Approves/Rejects with comment → Status: APPROVED/REJECTED → Audit event logged
```

### 3. Process Request (Admin)
```
Admin moves to IN_PROGRESS → Status: IN_PROGRESS → Audit event logged
Admin completes request → Status: DONE → Audit event logged
```

### 4. Cancel Request (Employee)
```
Employee cancels own request (if SUBMITTED) → Status: CANCELLED → Audit event logged
```

### Status Flow Diagram
```
                    ┌──────────┐
                    │SUBMITTED │
                    └────┬─────┘
                         │
            ┌────────────┼────────────┐
            │            │            │
            ▼            ▼            ▼
      ┌──────────┐ ┌──────────┐ ┌───────────┐
      │ APPROVED │ │ REJECTED │ │ CANCELLED │
      └────┬─────┘ └──────────┘ └───────────┘
           │
           ▼
     ┌────────────┐
     │IN_PROGRESS │
     └─────┬──────┘
           │
           ▼
      ┌─────────┐
      │  DONE   │
      └─────────┘
```

## Testing Strategy

### Backend Tests
```bash
cd backend
./mvnw test
```

- **Unit Tests**: Service layer logic
- **Integration Tests**: API endpoints with H2 in-memory database
- **Coverage**: Core workflow (create → approve → status change → done)

### Frontend Tests
```bash
cd frontend
npm run lint
npm run build
```

### E2E Tests (Playwright)
```bash
cd frontend
npm run test:e2e
```

E2E tests cover:
- Login with seeded credentials
- Create a new request
- Verify request appears in list
- Manager approval workflow
- Status verification

## Verification Checklist

Before considering the project complete, verify:

- [ ] `docker compose up` starts all services successfully
- [ ] Can log in as employee/manager/admin
- [ ] Employee can create and submit a request
- [ ] Employee can edit/cancel SUBMITTED requests
- [ ] Manager can view approval queue
- [ ] Manager can approve/reject with required comment
- [ ] Admin can view all requests
- [ ] Admin can change status to IN_PROGRESS and DONE
- [ ] Audit events are recorded and visible
- [ ] Comments can be added to requests
- [ ] Backend tests pass (`./mvnw test`)
- [ ] Frontend builds (`npm run build`)
- [ ] E2E tests pass
- [ ] CI pipeline is configured and passes

## Future Improvements

Documented enhancements for future iterations:

1. **Multi-step Approvals**: Support approval chains (Manager → Director → VP)
2. **Email Notifications**: Notify users on status changes
3. **File Attachments**: Upload supporting documents
4. **SSO Integration**: Support Okta/SAML authentication
5. **Advanced Reporting**: Dashboard analytics and CSV export
6. **Multi-tenant Support**: Organization-based data isolation
7. **Mobile App**: React Native companion app
8. **Audit Log Export**: Compliance reporting features

## Documentation

Detailed documentation is available in the `/docs` folder:

- [Problem and Goals](docs/problem-and-goals.md) - Project motivation and objectives
- [Requirements](docs/requirements.md) - Detailed scope, roles, and user stories
- [Architecture](docs/architecture.md) - Technical design and data model
- [Decisions and Tradeoffs](docs/decisions-and-tradeoffs.md) - Technical choices and rationale
- [AI Workflow](docs/ai-workflow.md) - AI-assisted development process

## Project Structure

```
ops-requests-dashboard/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/opsrequests/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── exception/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       ├── db/migration/
│   │   │       └── application.yml
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   ├── components/
│   │   ├── services/
│   │   └── types/
│   ├── package.json
│   └── Dockerfile
├── docs/
├── docker-compose.yml
├── .env.example
├── .github/workflows/ci.yml
└── README.md
```

## Production Deployment

The application is deployed on [Railway](https://railway.app) with the following services:

### Live URLs

| Service | URL |
|---------|-----|
| Frontend | https://frontend-production-133f.up.railway.app |
| Backend API | https://backend-production-c3d3.up.railway.app |

### Deployment Architecture

- **Frontend**: Next.js 14 with standalone output, deployed via Dockerfile
- **Backend**: Spring Boot 3 with Java 17, deployed via Dockerfile
- **Database**: PostgreSQL 15 managed by Railway

### Deploying Updates

The project uses `railway up` for deployments from the respective directories:

```bash
# Deploy frontend
cd frontend
railway up --service <frontend-service-id>

# Deploy backend
cd backend
railway up --service <backend-service-id>
```

### Environment Variables (Railway)

The following environment variables are configured in Railway:

| Variable | Description |
|----------|-------------|
| `DB_HOST` | PostgreSQL internal hostname |
| `DB_PORT` | PostgreSQL port (5432) |
| `DB_NAME` | Database name |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `SERVER_PORT` | Backend server port (8080) |
| `NEXT_PUBLIC_API_URL` | Backend API URL for frontend |

## License

This project is licensed under the MIT License - see below for details:

```
MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
