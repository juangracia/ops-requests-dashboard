# Architecture Documentation

## Table of Contents
- [System Overview](#system-overview)
- [Architecture Diagram](#architecture-diagram)
- [Component Architecture](#component-architecture)
  - [Backend: Spring Boot Application](#backend-spring-boot-application)
  - [Frontend: Next.js Application](#frontend-nextjs-application)
  - [Database: PostgreSQL](#database-postgresql)
- [Data Model](#data-model)
- [API Design](#api-design)
- [Security Architecture](#security-architecture)
- [Request Workflow](#request-workflow)
- [Deployment Architecture](#deployment-architecture)

## System Overview

The Ops Requests Dashboard is a three-tier web application following a classic separation of concerns:

- **Presentation Layer**: Next.js frontend with React and TypeScript
- **Business Logic Layer**: Spring Boot 3 REST API with Java 17
- **Data Layer**: PostgreSQL relational database

**Key Architectural Principles:**
- Clean separation between frontend and backend (independent deployments)
- RESTful API design with JSON payloads
- Stateless authentication using JWT tokens
- Role-based access control enforced at API layer
- Database-first data integrity with foreign keys and constraints

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         User's Browser                          │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │            Next.js Frontend (Port 3000)                   │ │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐  │ │
│  │  │   Pages     │  │  Components  │  │  API Client     │  │ │
│  │  │  - Login    │  │  - Forms     │  │  - HTTP calls   │  │ │
│  │  │  - Dashboard│  │  - Tables    │  │  - Auth headers │  │ │
│  │  │  - Requests │  │  - Modals    │  │  - Error handle │  │ │
│  │  └─────────────┘  └──────────────┘  └─────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/REST (JSON)
                              │ JWT in Authorization header
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│           Spring Boot Backend (Port 8080)                       │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │                    Security Layer                         │ │
│  │  ┌──────────────────┐         ┌─────────────────────┐    │ │
│  │  │  JWT Filter      │────────▶│  Authentication     │    │ │
│  │  │  - Validate token│         │  - Load user        │    │ │
│  │  │  - Extract claims│         │  - Check permissions│    │ │
│  │  └──────────────────┘         └─────────────────────┘    │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │                   Controller Layer                        │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │ │
│  │  │AuthController│ │RequestController│ │AdminController│ │ │
│  │  │  - /login   │  │  - CRUD ops │  │  - User mgmt    │  │ │
│  │  │  - /register│  │  - Approve  │  │  - Type mgmt    │  │ │
│  │  └─────────────┘  │  - Reject   │  └─────────────────┘  │ │
│  │                   │  - Comment  │                        │ │
│  │                   └─────────────┘                        │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │                    Service Layer                          │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │ │
│  │  │ UserService │  │RequestService│ │ AuditService    │  │ │
│  │  │  - Auth     │  │  - Business │  │  - Log events   │  │ │
│  │  │  - Password │  │    logic    │  │  - Track changes│  │ │
│  │  │  - JWT gen  │  │  - Validation│ │                 │  │ │
│  │  └─────────────┘  │  - State    │  └─────────────────┘  │ │
│  │                   │    machine  │                        │ │
│  │                   └─────────────┘                        │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │                 Repository Layer (JPA)                    │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │ │
│  │  │UserRepository│ │RequestRepo  │  │ AuditEventRepo  │  │ │
│  │  │             │  │             │  │                 │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              PostgreSQL Database (Port 5432)                    │
│                                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐  │
│  │  users   │  │ requests │  │ comments │  │ audit_events │  │
│  │          │  │          │  │          │  │              │  │
│  │  roles   │  │          │  │          │  │              │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘  │
│                                                                 │
│  ┌──────────────┐                                              │
│  │ request_types│                                              │
│  └──────────────┘                                              │
└─────────────────────────────────────────────────────────────────┘
```

## Component Architecture

### Backend: Spring Boot Application

**Technology Stack:**
- Spring Boot 3.2.x
- Java 17
- Spring Security 6
- Spring Data JPA
- PostgreSQL JDBC Driver
- JWT (jjwt library)
- Lombok (reduce boilerplate)
- Maven for build

**Package Structure:**
```
com.company.opsrequests/
├── config/
│   ├── SecurityConfig.java          # Security configuration, CORS, JWT
│   └── DataSourceConfig.java        # Database connection config
├── controller/
│   ├── AuthController.java          # Login, register endpoints
│   ├── RequestController.java       # Request CRUD, approve, reject
│   ├── CommentController.java       # Comment operations
│   └── AdminController.java         # User and type management
├── service/
│   ├── UserService.java             # User authentication, management
│   ├── RequestService.java          # Request business logic
│   ├── CommentService.java          # Comment handling
│   ├── AuditService.java            # Audit logging
│   └── RequestTypeService.java      # Request type management
├── repository/
│   ├── UserRepository.java          # JPA repository for User
│   ├── RequestRepository.java       # JPA repository for Request
│   ├── CommentRepository.java       # JPA repository for Comment
│   ├── AuditEventRepository.java    # JPA repository for AuditEvent
│   └── RequestTypeRepository.java   # JPA repository for RequestType
├── model/
│   ├── User.java                    # User entity
│   ├── Request.java                 # Request entity
│   ├── RequestComment.java          # Comment entity
│   ├── RequestAuditEvent.java       # Audit event entity
│   ├── RequestType.java             # Request type entity
│   └── enums/
│       ├── UserRole.java            # EMPLOYEE, MANAGER, ADMIN
│       ├── RequestStatus.java       # SUBMITTED, APPROVED, etc.
│       ├── Priority.java            # LOW, MEDIUM, HIGH
│       └── AuditEventType.java      # CREATED, UPDATED, etc.
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── CreateRequestRequest.java
│   │   ├── UpdateRequestRequest.java
│   │   └── ApproveRejectRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── RequestResponse.java
│       ├── UserResponse.java
│       └── ErrorResponse.java
├── security/
│   ├── JwtTokenProvider.java        # JWT generation and validation
│   ├── JwtAuthenticationFilter.java # Filter to validate JWT on requests
│   └── CustomUserDetailsService.java # Load user for authentication
├── exception/
│   ├── GlobalExceptionHandler.java  # Centralized error handling
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   └── ResourceNotFoundException.java
└── OpsRequestsApplication.java      # Spring Boot main class
```

**Key Design Patterns:**

1. **Controller-Service-Repository Pattern**: Clear separation of concerns
   - Controllers handle HTTP, validation, response formatting
   - Services contain business logic, transaction boundaries
   - Repositories abstract data access

2. **DTO Pattern**: Separate API contracts from domain models
   - Request DTOs for incoming payloads
   - Response DTOs for outgoing data
   - Prevents over-fetching and under-fetching

3. **Dependency Injection**: Spring manages all component lifecycles

4. **Transaction Management**: `@Transactional` on service methods ensures data consistency

### Frontend: Next.js Application

**Technology Stack:**
- Next.js 14 (App Router)
- React 18
- TypeScript
- Tailwind CSS for styling
- Axios for HTTP client
- React Hook Form for forms
- Zod for validation
- SWR or React Query for data fetching

**Directory Structure:**
```
src/
├── app/
│   ├── layout.tsx                   # Root layout with auth context
│   ├── page.tsx                     # Home/landing page
│   ├── login/
│   │   └── page.tsx                 # Login page
│   ├── dashboard/
│   │   └── page.tsx                 # Main dashboard (role-specific)
│   ├── requests/
│   │   ├── page.tsx                 # Request list
│   │   ├── [id]/page.tsx            # Request details
│   │   ├── new/page.tsx             # Create request form
│   │   └── [id]/edit/page.tsx       # Edit request form
│   └── admin/
│       ├── users/page.tsx           # User management
│       └── types/page.tsx           # Request type management
├── components/
│   ├── ui/                          # Reusable UI components
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Select.tsx
│   │   ├── Modal.tsx
│   │   └── Table.tsx
│   ├── forms/
│   │   ├── RequestForm.tsx          # Request create/edit form
│   │   ├── CommentForm.tsx
│   │   └── LoginForm.tsx
│   ├── layout/
│   │   ├── Header.tsx               # Navigation bar
│   │   ├── Sidebar.tsx
│   │   └── Footer.tsx
│   └── request/
│       ├── RequestList.tsx          # List of requests
│       ├── RequestCard.tsx          # Request summary card
│       ├── RequestDetails.tsx       # Full request details
│       ├── StatusBadge.tsx          # Status visualization
│       └── AuditTimeline.tsx        # Audit history display
├── lib/
│   ├── api/
│   │   ├── client.ts                # Axios instance with auth interceptor
│   │   ├── auth.ts                  # Login, logout functions
│   │   ├── requests.ts              # Request API calls
│   │   └── admin.ts                 # Admin API calls
│   ├── auth/
│   │   ├── AuthContext.tsx          # React context for auth state
│   │   └── ProtectedRoute.tsx       # Route guard component
│   └── utils/
│       ├── formatters.ts            # Date, currency formatting
│       └── validators.ts            # Client-side validation
└── types/
    ├── user.ts                      # User TypeScript types
    ├── request.ts                   # Request TypeScript types
    └── api.ts                       # API response types
```

**Key Design Patterns:**

1. **Component Composition**: Small, reusable components composed into pages

2. **Custom Hooks**: Encapsulate data fetching and state management
   ```typescript
   useAuth()              // Current user, login, logout
   useRequests(filters)   // Fetch and filter requests
   useRequest(id)         // Fetch single request
   ```

3. **Context for Global State**: Auth state shared via React Context

4. **Protected Routes**: Higher-order component checks authentication before rendering

### Database: PostgreSQL

**Why PostgreSQL:**
- ACID compliance for transactional integrity
- Robust foreign key support for referential integrity
- JSON support for flexible data if needed in future
- Excellent performance for relational queries
- Wide industry adoption and tooling

**Database Configuration:**
- Version: PostgreSQL 15
- Port: 5432
- Database name: ops_requests_db
- Schema: public (default)
- Encoding: UTF-8
- Timezone: UTC

## Data Model

### Entity Relationship Diagram

```
┌─────────────────────────────────────┐
│             User                    │
├─────────────────────────────────────┤
│ id (PK)              BIGSERIAL      │
│ email                VARCHAR(255) UK│
│ password_hash        VARCHAR(255)   │
│ first_name           VARCHAR(100)   │
│ last_name            VARCHAR(100)   │
│ role                 VARCHAR(20)    │
│ manager_id (FK)      BIGINT         │───┐
│ active               BOOLEAN        │   │
│ created_at           TIMESTAMP      │   │
│ updated_at           TIMESTAMP      │   │
└─────────────────────────────────────┘   │
         │                                 │
         │ (self-reference)                │
         └─────────────────────────────────┘
         │
         │ 1:N (as requester)
         │
         ▼
┌─────────────────────────────────────┐         ┌─────────────────────────────┐
│           Request                   │         │      RequestType            │
├─────────────────────────────────────┤         ├─────────────────────────────┤
│ id (PK)              BIGSERIAL      │         │ id (PK)         BIGSERIAL   │
│ requester_id (FK)    BIGINT         │────┐    │ code            VARCHAR(50) │
│ manager_id (FK)      BIGINT         │────┼──▶ │ name            VARCHAR(100)│
│ request_type_id (FK) BIGINT         │────┼──▶ │ active          BOOLEAN     │
│ title                VARCHAR(200)   │    │    │ created_at      TIMESTAMP   │
│ description          TEXT           │    │    └─────────────────────────────┘
│ amount               DECIMAL(10,2)  │    │
│ priority             VARCHAR(10)    │    │
│ status               VARCHAR(20)    │    │
│ created_at           TIMESTAMP      │    │
│ updated_at           TIMESTAMP      │    │
└─────────────────────────────────────┘    │
         │                                  │
         │ 1:N                              │ (as manager)
         ├──────────────────────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────────────────────────┐
│       RequestComment                │
├─────────────────────────────────────┤
│ id (PK)              BIGSERIAL      │
│ request_id (FK)      BIGINT         │
│ author_id (FK)       BIGINT         │────────▶ User
│ comment_text         TEXT           │
│ created_at           TIMESTAMP      │
└─────────────────────────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────────────────────────┐
│      RequestAuditEvent              │
├─────────────────────────────────────┤
│ id (PK)              BIGSERIAL      │
│ request_id (FK)      BIGINT         │
│ actor_id (FK)        BIGINT         │────────▶ User (nullable for SYSTEM)
│ event_type           VARCHAR(50)    │
│ from_status          VARCHAR(20)    │
│ to_status            VARCHAR(20)    │
│ note                 TEXT           │
│ created_at           TIMESTAMP      │
└─────────────────────────────────────┘
```

### Table Definitions

#### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN')),
    manager_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_manager_id ON users(manager_id);
CREATE INDEX idx_users_active ON users(active);
```

#### request_types
```sql
CREATE TABLE request_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_request_types_code ON request_types(code);
CREATE INDEX idx_request_types_active ON request_types(active);
```

#### requests
```sql
CREATE TABLE requests (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    manager_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_type_id BIGINT NOT NULL REFERENCES request_types(id),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    amount DECIMAL(10, 2),
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' CHECK (
        status IN ('SUBMITTED', 'APPROVED', 'REJECTED', 'IN_PROGRESS', 'DONE', 'CANCELLED')
    ),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_requests_requester_id ON requests(requester_id);
CREATE INDEX idx_requests_manager_id ON requests(manager_id);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_requests_created_at ON requests(created_at);
CREATE INDEX idx_requests_type_id ON requests(request_type_id);
```

#### request_comments
```sql
CREATE TABLE request_comments (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    comment_text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_request_comments_request_id ON request_comments(request_id);
CREATE INDEX idx_request_comments_author_id ON request_comments(author_id);
```

#### request_audit_events
```sql
CREATE TABLE request_audit_events (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL REFERENCES requests(id) ON DELETE CASCADE,
    actor_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(50) NOT NULL CHECK (
        event_type IN ('CREATED', 'UPDATED', 'STATUS_CHANGED', 'COMMENTED', 'CANCELLED')
    ),
    from_status VARCHAR(20),
    to_status VARCHAR(20),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_events_request_id ON request_audit_events(request_id);
CREATE INDEX idx_audit_events_created_at ON request_audit_events(created_at);
```

### Data Model Notes

1. **Soft Deletes**: `active` flag on users and request_types prevents deletion while preserving history

2. **Timestamps**: All tables have `created_at`, requests also have `updated_at` for tracking modifications

3. **Cascading**: Request deletion cascades to comments and audit events (orphan prevention)

4. **Constraints**: CHECK constraints enforce enum values at database level

5. **Indexes**: Strategic indexes on foreign keys and frequently queried columns

6. **Referential Integrity**: All foreign keys enforced at database level

## API Design

### RESTful Endpoints

#### Authentication
```
POST   /api/auth/login          # Login with email/password, returns JWT
POST   /api/auth/register       # Register new user (admin only)
GET    /api/auth/me             # Get current user info
```

#### Requests
```
GET    /api/requests                          # List requests (filtered by role)
GET    /api/requests/{id}                     # Get request details
POST   /api/requests                          # Create new request
PUT    /api/requests/{id}                     # Update request (SUBMITTED only)
POST   /api/requests/{id}/approve             # Approve request (manager only)
POST   /api/requests/{id}/reject              # Reject request (manager only)
POST   /api/requests/{id}/start               # Move to IN_PROGRESS (admin only)
POST   /api/requests/{id}/complete            # Move to DONE (admin only)
POST   /api/requests/{id}/cancel              # Cancel request (requester only)
```

#### Comments
```
GET    /api/requests/{id}/comments            # Get all comments for request
POST   /api/requests/{id}/comments            # Add comment to request
```

#### Audit
```
GET    /api/requests/{id}/audit               # Get audit history for request
```

#### Admin
```
GET    /api/admin/users                       # List all users
POST   /api/admin/users                       # Create user
PUT    /api/admin/users/{id}                  # Update user
DELETE /api/admin/users/{id}                  # Deactivate user

GET    /api/admin/request-types               # List request types
POST   /api/admin/request-types               # Create request type
PUT    /api/admin/request-types/{id}          # Update request type
DELETE /api/admin/request-types/{id}          # Deactivate request type
```

### API Conventions

1. **HTTP Methods**: Standard REST verbs (GET, POST, PUT, DELETE)

2. **Status Codes**:
   - 200 OK: Successful GET, PUT
   - 201 Created: Successful POST
   - 204 No Content: Successful DELETE
   - 400 Bad Request: Validation error
   - 401 Unauthorized: Missing or invalid JWT
   - 403 Forbidden: Valid JWT but insufficient permissions
   - 404 Not Found: Resource doesn't exist
   - 500 Internal Server Error: Unexpected server error

3. **Request Format**: JSON body for POST/PUT

4. **Response Format**: JSON with consistent structure
   ```json
   {
     "data": { ... },           // Success response
     "error": "message",        // Error response
     "timestamp": "2024-01-15T10:30:00Z"
   }
   ```

5. **Authentication**: JWT in `Authorization: Bearer <token>` header

6. **Pagination**: Query params `page`, `size`, `sort` for list endpoints

7. **Filtering**: Query params like `status`, `priority`, `requesterId` for requests

## Security Architecture

### Authentication Flow

```
1. User submits email + password to /api/auth/login
2. Backend validates credentials against password_hash
3. If valid, generate JWT token with claims:
   - sub: user ID
   - email: user email
   - role: user role
   - exp: expiration (24 hours)
4. Return JWT to client
5. Client stores JWT (localStorage or httpOnly cookie)
6. Client includes JWT in Authorization header for all requests
7. Backend validates JWT on every request via filter
8. Backend extracts user info from JWT claims
9. Backend checks role-based permissions
```

### Authorization Model

**Role Hierarchy:**
- ADMIN: Full access to all resources
- MANAGER: Can manage team requests, has all employee permissions
- EMPLOYEE: Can manage own requests only

**Permission Matrix:**

| Action | Employee | Manager | Admin |
|--------|----------|---------|-------|
| Create request | Own only | Own only | Any |
| View request | Own only | Own + team | Any |
| Edit request | Own (SUBMITTED) | Own (SUBMITTED) | Any (SUBMITTED) |
| Cancel request | Own (SUBMITTED/APPROVED) | Own (SUBMITTED/APPROVED) | Any (SUBMITTED/APPROVED) |
| Approve request | No | Team only (not own) | No |
| Reject request | No | Team only (not own) | No |
| Start work (→IN_PROGRESS) | No | No | Yes |
| Complete (→DONE) | No | No | Yes |
| View all requests | No | No | Yes |
| Manage users | No | No | Yes |
| Manage request types | No | No | Yes |

**Enforcement Points:**
1. **JwtAuthenticationFilter**: Validates token, loads user into SecurityContext
2. **@PreAuthorize annotations**: Method-level role checks
3. **Service layer**: Business logic checks (e.g., manager can't approve own request)

### Security Best Practices

1. **Password Security**:
   - BCrypt hashing with strength 12
   - Minimum 8 characters, complexity requirements
   - Never log or expose passwords

2. **JWT Security**:
   - Signed with HS256 and secret key
   - Short expiration (24 hours)
   - Claims include only necessary data (no sensitive info)

3. **SQL Injection Prevention**:
   - JPA/Hibernate parameterized queries
   - No raw SQL with string concatenation

4. **XSS Prevention**:
   - React escapes output by default
   - No dangerouslySetInnerHTML

5. **CORS**:
   - Configured to allow frontend origin only
   - Credentials allowed for cookie-based auth (if used)

6. **HTTPS**:
   - Required in production
   - Development can use HTTP

## Request Workflow

### Happy Path: Purchase Request

```
┌────────────┐
│  Employee  │
└─────┬──────┘
      │
      │ 1. Submit request (title, description, amount, type=PURCHASE)
      ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: RequestService.createRequest()                    │
│  - Validate input                                           │
│  - Assign manager from employee.manager_id                  │
│  - Set status = SUBMITTED                                   │
│  - Save to database                                         │
│  - Create audit event: CREATED                              │
└─────┬───────────────────────────────────────────────────────┘
      │
      │ 2. Manager receives notification (future feature)
      │    For now: Manager checks dashboard
      ▼
┌────────────┐
│   Manager  │
└─────┬──────┘
      │
      │ 3. Manager views request, adds comment, clicks Approve
      ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: RequestService.approveRequest(id, managerId, comment)│
│  - Verify request.manager_id == managerId                   │
│  - Verify requester_id != managerId (no self-approval)      │
│  - Verify status == SUBMITTED                               │
│  - Add comment with manager as author                       │
│  - Update status = APPROVED                                 │
│  - Create audit event: STATUS_CHANGED (SUBMITTED→APPROVED)  │
└─────┬───────────────────────────────────────────────────────┘
      │
      │ 4. Ops Admin checks approved requests queue
      ▼
┌────────────┐
│ Ops Admin  │
└─────┬──────┘
      │
      │ 5. Admin clicks "Start Work", adds note about vendor
      ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: RequestService.startWork(id, adminId, note)       │
│  - Verify user has ROLE_ADMIN                               │
│  - Verify status == APPROVED                                │
│  - Update status = IN_PROGRESS                              │
│  - Add comment if note provided                             │
│  - Create audit event: STATUS_CHANGED (APPROVED→IN_PROGRESS)│
└─────┬───────────────────────────────────────────────────────┘
      │
      │ 6. Admin processes purchase, receives item
      │
      │ 7. Admin clicks "Mark Complete", adds delivery note
      ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: RequestService.completeRequest(id, adminId, note) │
│  - Verify user has ROLE_ADMIN                               │
│  - Verify status == IN_PROGRESS                             │
│  - Update status = DONE                                     │
│  - Add comment if note provided                             │
│  - Create audit event: STATUS_CHANGED (IN_PROGRESS→DONE)    │
└─────┬───────────────────────────────────────────────────────┘
      │
      │ 8. Employee sees request marked DONE in dashboard
      ▼
┌────────────┐
│  Employee  │
│  (Satisfied)│
└────────────┘
```

### Alternative Path: Rejection

```
Employee submits → Manager reviews → Manager clicks Reject with reason
→ Status = REJECTED (terminal state)
→ Employee sees rejection reason
→ Employee submits new request if needed
```

### Alternative Path: Cancellation

```
Employee submits → Employee changes mind → Employee clicks Cancel
→ Status = CANCELLED (terminal state)
→ Audit event logs cancellation

OR

Employee submits → Manager approves → Employee no longer needs it
→ Employee clicks Cancel with reason
→ Status = CANCELLED
→ Audit event logs cancellation
```

## Deployment Architecture

### Docker Compose Stack

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: ops_requests_db
      POSTGRES_USER: opsuser
      POSTGRES_PASSWORD: opspass
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ops_requests_db
      SPRING_DATASOURCE_USERNAME: opsuser
      SPRING_DATASOURCE_PASSWORD: opspass
      JWT_SECRET: your-secret-key-change-in-production
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_URL: http://localhost:8080/api
    depends_on:
      - backend

volumes:
  postgres_data:
```

### Port Allocation

- **Frontend**: 3000 (Next.js default)
- **Backend**: 8080 (Spring Boot custom to avoid conflicts)
- **Database**: 5432 (PostgreSQL default)

### Environment Variables

**Backend (.env or docker-compose.yml):**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ops_requests_db
SPRING_DATASOURCE_USERNAME=opsuser
SPRING_DATASOURCE_PASSWORD=opspass
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION_MS=86400000
```

**Frontend (.env.local):**
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Startup Sequence

1. **postgres**: Starts first, initializes database
2. **backend**: Waits for postgres, runs migrations, starts API server
3. **frontend**: Waits for backend, starts Next.js dev server

### Data Initialization

**Database Migrations**: Use Flyway or Liquibase for version-controlled schema changes

**Seed Data**: SQL script to create initial data:
- Admin user (admin@company.com)
- Sample manager (manager@company.com)
- Sample employees
- Request types (PURCHASE, IT_ACCESS, HR, TRAVEL, OTHER)

### Development vs Production

**Development:**
- Docker Compose for local stack
- Hot reload enabled for frontend
- Spring Boot DevTools for backend
- Database data in Docker volume (persisted)

**Production (Future):**
- Kubernetes or cloud platform
- Frontend built and served via CDN
- Backend as containerized service
- Managed PostgreSQL (RDS, Cloud SQL, etc.)
- HTTPS enforced
- Environment-specific secrets management
