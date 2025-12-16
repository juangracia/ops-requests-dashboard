# Technical Decisions and Tradeoffs

## Table of Contents
- [Technology Stack Decisions](#technology-stack-decisions)
- [Architecture Decisions](#architecture-decisions)
- [Feature Scope Decisions](#feature-scope-decisions)
- [Data Model Decisions](#data-model-decisions)
- [Security Decisions](#security-decisions)
- [Testing Decisions](#testing-decisions)
- [Development Workflow Decisions](#development-workflow-decisions)
- [Future Considerations](#future-considerations)

## Technology Stack Decisions

### Decision 1: Spring Boot 3 for Backend

**Decision**: Use Spring Boot 3 with Java 17 for the backend API

**Rationale**:
- **Enterprise Patterns**: Spring Boot provides mature, well-tested patterns for building REST APIs
- **Developer Familiarity**: Wide adoption means easier onboarding and abundant resources
- **Ecosystem**: Rich ecosystem of libraries (Security, Data JPA, Validation)
- **Production Ready**: Built-in features for monitoring, health checks, and deployment
- **Type Safety**: Java's static typing catches errors at compile time

**Alternatives Considered**:
- **Node.js/Express**:
  - Pros: JavaScript full-stack, lighter weight
  - Cons: Less structured, more boilerplate for enterprise patterns, weaker typing
- **Django/Python**:
  - Pros: Rapid development, great ORM
  - Cons: Less common for microservices, Python's typing is newer
- **Go**:
  - Pros: Excellent performance, simple deployment
  - Cons: Less mature ecosystem for enterprise features, more manual coding

**Tradeoffs**:
- **Accepted**: Higher memory footprint, longer startup time, JVM overhead
- **Gained**: Robust dependency injection, extensive middleware, mature tooling
- **Mitigated**: Use Spring Boot 3 native compilation for production if startup time is critical

---

### Decision 2: Next.js 14 for Frontend

**Decision**: Use Next.js 14 with TypeScript and App Router

**Rationale**:
- **Modern React**: Next.js is the leading React framework with best practices built-in
- **TypeScript**: Strongly typed frontend code prevents runtime errors
- **Developer Experience**: Hot reload, file-based routing, great tooling
- **SEO Capability**: Server-side rendering available if public pages are needed
- **Ecosystem**: Tailwind CSS, React Hook Form, and other modern tools integrate seamlessly

**Alternatives Considered**:
- **Create React App**:
  - Pros: Simpler, less opinionated
  - Cons: No SSR, more configuration needed, less actively maintained
- **Vue.js/Nuxt**:
  - Pros: Simpler learning curve, similar features
  - Cons: Smaller ecosystem, less demand in job market
- **Angular**:
  - Pros: Full-featured framework, good for large teams
  - Cons: Steeper learning curve, heavier weight, more complex

**Tradeoffs**:
- **Accepted**: Next.js learning curve (App Router is new), framework lock-in
- **Gained**: Excellent DX, strong typing, modern features, future-proof
- **Mitigated**: Comprehensive documentation and clear file structure

---

### Decision 3: PostgreSQL for Database

**Decision**: Use PostgreSQL 15 as the relational database

**Rationale**:
- **ACID Compliance**: Strong transactional integrity for financial data (amounts, approvals)
- **Referential Integrity**: Foreign keys enforce data consistency at database level
- **Mature and Reliable**: Battle-tested in production for decades
- **JSON Support**: Flexibility to store semi-structured data in future if needed
- **Industry Standard**: Most employers use PostgreSQL or similar (MySQL, Oracle)

**Alternatives Considered**:
- **MySQL**:
  - Pros: Similar features, widely used
  - Cons: Slightly less feature-rich than PostgreSQL
- **MongoDB**:
  - Pros: Flexible schema, fast for reads
  - Cons: Weak consistency, harder to model relationships, not ideal for transactions
- **SQLite**:
  - Pros: Zero configuration, embedded
  - Cons: No concurrency support, not production-ready

**Tradeoffs**:
- **Accepted**: Requires database server (can't embed), more setup than SQLite
- **Gained**: Production-grade reliability, scalability, advanced querying
- **Mitigated**: Docker Compose makes setup trivial

---

## Architecture Decisions

### Decision 4: Stateless JWT Authentication

**Decision**: Use JWT tokens for authentication instead of session-based auth

**Rationale**:
- **Scalability**: Stateless tokens don't require server-side session storage
- **Decoupled**: Frontend and backend can be deployed independently
- **Mobile Friendly**: Tokens work seamlessly with mobile apps if added later
- **Standard**: JWT is industry standard for modern APIs

**Alternatives Considered**:
- **Session-based auth (cookies)**:
  - Pros: Server controls revocation, simpler client
  - Cons: Requires session store (Redis), harder to scale, CSRF concerns
- **OAuth2/OpenID Connect**:
  - Pros: Federated identity, SSO support
  - Cons: Overkill for MVP, requires external provider or complex implementation

**Tradeoffs**:
- **Accepted**: Tokens can't be revoked before expiration (need short expiry), slightly larger payload
- **Gained**: Horizontal scalability, simpler deployment, better API design
- **Mitigated**: Short token expiration (24 hours), refresh token flow deferred to future

---

### Decision 5: RESTful API over GraphQL

**Decision**: Build a RESTful JSON API instead of GraphQL

**Rationale**:
- **Simplicity**: REST is simpler to implement and understand
- **Tooling**: Better debugging tools (browser, Postman), HTTP caching
- **Standard**: REST is more widely known and expected in backend jobs
- **Adequate**: The data model is simple enough that GraphQL's flexibility isn't needed

**Alternatives Considered**:
- **GraphQL**:
  - Pros: Flexible querying, no over/under-fetching, strong typing
  - Cons: More complex setup, harder to cache, steeper learning curve
- **gRPC**:
  - Pros: High performance, strong contracts
  - Cons: Not browser-friendly, overkill for CRUD app

**Tradeoffs**:
- **Accepted**: Some endpoints may over-fetch or under-fetch data
- **Gained**: Faster development, easier debugging, wider understanding
- **Mitigated**: Use DTOs to shape responses, minimize round trips

---

### Decision 6: Monolithic Backend (Not Microservices)

**Decision**: Single Spring Boot application, not microservices

**Rationale**:
- **Appropriate Scale**: MVP with simple domain doesn't justify microservices
- **Faster Development**: No distributed system complexity
- **Easier Debugging**: Single codebase, single deployment
- **Transactional Integrity**: ACID transactions work within single database

**Alternatives Considered**:
- **Microservices** (User Service, Request Service, Notification Service):
  - Pros: Independent scaling, separate deployment
  - Cons: Distributed transactions, service mesh, much higher complexity

**Tradeoffs**:
- **Accepted**: All components scale together, shared database
- **Gained**: Simplicity, faster iteration, easier testing
- **Mitigated**: Clean package structure allows future extraction if needed

---

## Feature Scope Decisions

### Decision 7: Single-Step Approval Only

**Decision**: Manager approval is single-step; no multi-level or parallel approvals

**Rationale**:
- **MVP Focus**: Multi-step approvals add significant complexity
- **Common Case**: Most small companies have flat hierarchies
- **Iterative**: Can add multi-step in v2 if needed

**Alternatives Considered**:
- **Multi-step approval chain** (Manager → Director → VP):
  - Pros: Handles complex org structures
  - Cons: Complex state machine, routing logic, UI for configuring chains
- **Parallel approvals** (HR + Finance both approve):
  - Pros: Handles cross-functional requests
  - Cons: Requires approval group concept, voting logic

**Tradeoffs**:
- **Accepted**: Cannot model complex approval hierarchies in MVP
- **Gained**: Fast implementation, clear UX, simple state machine
- **Deferred**: Multi-step approvals to v2, can be added without breaking changes

---

### Decision 8: No Email Notifications

**Decision**: No email or Slack notifications for status changes

**Rationale**:
- **MVP Focus**: Notifications are nice-to-have, not core workflow
- **Complexity**: Requires email service (SendGrid, SES), template management
- **Testability**: Email testing adds complexity (mock services, test inboxes)
- **Local Development**: Email providers require API keys, breaking offline development

**Alternatives Considered**:
- **Email via SMTP**:
  - Pros: Standard protocol
  - Cons: Requires email server, deliverability issues
- **Slack webhooks**:
  - Pros: Modern, real-time
  - Cons: Requires Slack workspace, webhooks, company-specific

**Tradeoffs**:
- **Accepted**: Users must check dashboard for updates
- **Gained**: Simpler architecture, no external dependencies, faster dev
- **Deferred**: Add notification service in v2 with pluggable providers

---

### Decision 9: No File Attachments

**Decision**: Requests cannot include file attachments (receipts, quotes, etc.)

**Rationale**:
- **Complexity**: File upload requires storage (S3, local filesystem), virus scanning, size limits
- **Security**: File uploads are common attack vector
- **Scope**: Not essential for MVP, can describe in text field
- **Storage**: Avoids needing object storage service

**Alternatives Considered**:
- **Local file storage**:
  - Pros: Simple, no external service
  - Cons: Not scalable, lost on container restart
- **S3/Cloud Storage**:
  - Pros: Scalable, durable
  - Cons: Requires AWS account, costs, API keys

**Tradeoffs**:
- **Accepted**: Users paste URLs or describe items in text
- **Gained**: Simpler backend, no storage service, faster implementation
- **Deferred**: Add attachments in v2 with cloud storage

---

### Decision 10: Manager Assigned at Request Creation (Snapshot)

**Decision**: Manager is snapshot from requester's profile at request creation time

**Rationale**:
- **Consistency**: Prevents confusion if manager changes during approval process
- **Audit**: Clear record of who was responsible at time of request
- **Simplicity**: No need to handle manager reassignment logic

**Alternatives Considered**:
- **Dynamic manager lookup**:
  - Pros: Always uses current manager
  - Cons: Confusing if manager changes mid-approval, breaks audit trail
- **Approval reassignment**:
  - Pros: Can handle manager changes
  - Cons: Complex workflow, who approves if manager leaves company?

**Tradeoffs**:
- **Accepted**: If requester's manager changes, old requests stay with old manager
- **Gained**: Clear audit trail, no edge cases, simpler logic
- **Mitigated**: Admins can manually reassign if needed (deferred feature)

---

## Data Model Decisions

### Decision 11: Soft Deletes for Users and Request Types

**Decision**: Use `active` boolean flag instead of hard deletes

**Rationale**:
- **Data Integrity**: Preserve historical records (who approved what)
- **Audit Trail**: Deleted users should still appear in audit logs
- **Reversible**: Can reactivate users if mistake was made
- **Compliance**: May be legally required to retain employment records

**Alternatives Considered**:
- **Hard delete**:
  - Pros: Simpler queries, truly removes data
  - Cons: Breaks foreign keys, loses audit trail, not reversible
- **Archive table**:
  - Pros: Keeps main table clean
  - Cons: Complex queries, data duplication

**Tradeoffs**:
- **Accepted**: `WHERE active = true` needed on most queries
- **Gained**: Preserved history, reversible actions, compliance-friendly
- **Mitigated**: Database indexes on `active` column for performance

---

### Decision 12: Separate Audit Event Table

**Decision**: `request_audit_events` table separate from requests

**Rationale**:
- **Immutability**: Audit events never change, requests do
- **Query Performance**: Don't bloat requests table with history
- **Compliance**: Audit logs often have different retention policies
- **Flexibility**: Can audit non-request events in future (user actions, etc.)

**Alternatives Considered**:
- **JSON column on requests**:
  - Pros: Keeps data together
  - Cons: Harder to query, not normalized, grows unbounded
- **Event sourcing**:
  - Pros: Full replay capability, temporal queries
  - Cons: Much more complex, overkill for MVP

**Tradeoffs**:
- **Accepted**: Extra table, join needed for audit display
- **Gained**: Clean separation, scalable, query performance
- **Mitigated**: Indexed foreign key makes joins fast

---

### Decision 13: Enum Values Stored as Strings

**Decision**: Store status, priority, role as VARCHAR instead of separate tables

**Rationale**:
- **Simplicity**: No joins needed for common lookups
- **Readability**: Database values are human-readable
- **Performance**: Faster queries (no joins)
- **Static**: These values rarely change

**Alternatives Considered**:
- **Separate lookup tables** (status_types, priority_types):
  - Pros: Normalized, can add metadata (display name, description)
  - Cons: Extra joins, more tables, overkill for static enums
- **Integer codes**:
  - Pros: Smaller storage
  - Cons: Not readable, need mapping layer

**Tradeoffs**:
- **Accepted**: Slightly more storage (VARCHAR vs INT), risk of typos
- **Gained**: Simpler schema, faster queries, readable data
- **Mitigated**: CHECK constraints enforce valid values, JPA enums prevent typos

---

## Security Decisions

### Decision 14: BCrypt Password Hashing

**Decision**: Use BCrypt with strength 12 for password hashing

**Rationale**:
- **Industry Standard**: BCrypt is proven and recommended (OWASP)
- **Adaptive**: Strength parameter allows increasing cost as hardware improves
- **Salted**: Automatic random salt prevents rainbow table attacks
- **Slow**: Intentionally slow to prevent brute force

**Alternatives Considered**:
- **PBKDF2**:
  - Pros: NIST standard
  - Cons: Less adoption in modern frameworks
- **Argon2**:
  - Pros: Won password hashing competition
  - Cons: Less mature library support, overkill for MVP
- **SHA256 (plain)**:
  - Pros: Fast
  - Cons: NOT SECURE, no salt, rainbow tables

**Tradeoffs**:
- **Accepted**: Slower login (100-300ms hash time)
- **Gained**: Strong security, industry best practice
- **Mitigated**: Hash time is negligible compared to network latency

---

### Decision 15: Role-Based Access Control (Not RBAC)

**Decision**: Simple role enum (EMPLOYEE, MANAGER, ADMIN) instead of permissions-based RBAC

**Rationale**:
- **Simplicity**: Three roles cover all use cases in MVP
- **Understandable**: Clear role hierarchy easy to explain
- **Fast Authorization**: Simple if/else checks in code
- **Adequate**: No need for fine-grained permissions in small companies

**Alternatives Considered**:
- **Full RBAC** (roles + permissions matrix):
  - Pros: Flexible, fine-grained control
  - Cons: Complex schema (role_permissions table), overkill for 3 roles
- **Attribute-Based Access Control (ABAC)**:
  - Pros: Very flexible (rules engine)
  - Cons: Extremely complex, hard to debug

**Tradeoffs**:
- **Accepted**: Cannot create custom roles or permissions
- **Gained**: Fast implementation, simple debugging, clear to users
- **Deferred**: RBAC to v2 if custom roles are needed

---

## Testing Decisions

### Decision 16: H2 In-Memory Database for Tests

**Decision**: Use H2 for integration tests instead of Testcontainers

**Rationale**:
- **Speed**: H2 starts instantly, Testcontainers requires Docker spin-up
- **Simplicity**: No Docker dependency for running tests
- **CI Friendly**: Works in any CI environment without Docker daemon
- **Sufficient**: H2 is PostgreSQL-compatible for most SQL

**Alternatives Considered**:
- **Testcontainers with PostgreSQL**:
  - Pros: Tests against real database, exact production environment
  - Cons: Slower tests, Docker dependency, more complex setup
- **Mock repositories**:
  - Pros: Fastest tests
  - Cons: Doesn't test SQL queries, integration gaps

**Tradeoffs**:
- **Accepted**: Slight risk of H2/PostgreSQL incompatibility (e.g., JSON functions)
- **Gained**: Fast test runs, simple CI, no Docker
- **Mitigated**: Run manual tests against real PostgreSQL before release

---

### Decision 17: Manual Testing over E2E Automation (Initially)

**Decision**: Focus on unit and integration tests; defer Selenium/Cypress E2E tests

**Rationale**:
- **MVP Priority**: Core logic correctness more important than UI automation
- **Complexity**: E2E tests are brittle and time-consuming to maintain
- **Manual Feasible**: Small feature set can be manually tested quickly
- **ROI**: Unit + integration tests provide 80% of value with 20% of effort

**Alternatives Considered**:
- **Full E2E suite** (Cypress, Playwright):
  - Pros: Catches integration bugs, tests real user flows
  - Cons: Slow, flaky, expensive to maintain
- **No tests**:
  - Pros: Fastest development
  - Cons: Regressions, low confidence

**Tradeoffs**:
- **Accepted**: Manual regression testing needed before releases
- **Gained**: Faster initial development, focus on business logic
- **Deferred**: Add E2E tests in v2 for critical paths (login, submit request, approve)

---

## Development Workflow Decisions

### Decision 18: Docker Compose for Local Development

**Decision**: Use Docker Compose to run all services locally

**Rationale**:
- **Consistency**: Same environment for all developers
- **Easy Setup**: Single `docker-compose up` command
- **Production Parity**: Closely matches production deployment
- **Isolation**: No global PostgreSQL installation needed

**Alternatives Considered**:
- **Local installation** (Java, Node, PostgreSQL installed globally):
  - Pros: Faster startup, easier debugging
  - Cons: Version conflicts, "works on my machine" issues
- **Kubernetes (Minikube)**:
  - Pros: Matches production if using K8s
  - Cons: Overkill for local dev, slower, more complex

**Tradeoffs**:
- **Accepted**: Docker required, slightly slower startup
- **Gained**: Consistent environment, easy onboarding, production-like
- **Mitigated**: Volume mounts for hot reload, dev mode containers

---

### Decision 19: Monorepo Structure

**Decision**: Single repository with `/backend` and `/frontend` directories

**Rationale**:
- **Atomic Changes**: Related frontend/backend changes in single commit/PR
- **Simpler Versioning**: One version for the whole system
- **Easier Navigation**: All code in one place for learning/demo purposes
- **Tooling**: Docker Compose naturally works with monorepo

**Alternatives Considered**:
- **Separate repositories**:
  - Pros: Independent versioning, clearer ownership
  - Cons: Coordinating changes is harder, two PRs for one feature
- **Polyrepo with submodules**:
  - Pros: Reusable components
  - Cons: Git submodule complexity, overkill for single project

**Tradeoffs**:
- **Accepted**: Mixed languages/tools in one repo, larger clone
- **Gained**: Single source of truth, easier demos, coordinated releases
- **Mitigated**: Clear directory structure, separate README per component

---

### Decision 20: Custom Ports to Avoid Conflicts

**Decision**: Backend on 8080 (not 8080 if common conflict), Frontend on 3000

**Rationale**:
- **Avoid Conflicts**: Many devs run multiple services
- **Clear Separation**: Different ports make it obvious which service you're hitting
- **Standard**: 3000 is Next.js default, 8080 is common for Spring Boot

**Alternatives Considered**:
- **Standard ports** (80, 443):
  - Pros: No port in URL
  - Cons: Requires root/admin, conflicts with system services
- **Random high ports** (8743, 9182):
  - Pros: No conflicts
  - Cons: Hard to remember, not discoverable

**Tradeoffs**:
- **Accepted**: Must specify port in URLs (http://localhost:8080)
- **Gained**: No conflicts, works out of the box
- **Mitigated**: Environment variable configuration if ports are taken

---

## Future Considerations

### What Could Change in V2

1. **Multi-Step Approvals**: Add approval chain configuration (manager → director → VP)
2. **Notifications**: Email/Slack integration for status changes
3. **Attachments**: File upload with S3 storage
4. **Refresh Tokens**: Long-lived sessions with token refresh flow
5. **Advanced Filtering**: Saved filters, dashboard customization
6. **Analytics Dashboard**: Charts for request volume, approval times, spending
7. **Mobile App**: React Native app with shared API
8. **SSO Integration**: OAuth2 with Google, Microsoft, Okta
9. **Audit Export**: Compliance reports in CSV/PDF
10. **Request Templates**: Predefined templates for common requests

### What Won't Change (Stable Decisions)

1. **RESTful API**: Unlikely to move to GraphQL or gRPC
2. **PostgreSQL**: Database choice is solid
3. **JWT Auth**: Standard approach for APIs
4. **Separate Frontend/Backend**: Architecture will remain decoupled
5. **Role-Based Security**: Core model of Employee/Manager/Admin

### Migration Paths

If the project grows beyond MVP:

- **Scale Backend**: Add Redis for caching, read replicas for PostgreSQL
- **Scale Frontend**: Deploy to Vercel/Netlify with CDN
- **Extract Microservices**: Notification service could be separate if high volume
- **Add Message Queue**: RabbitMQ or Kafka for async processing (emails, exports)
- **Advanced RBAC**: Replace simple roles with permissions matrix if needed

### Lessons Learned (To Document After Implementation)

This section will be filled in after the project is built:
- What took longer than expected?
- What was easier than anticipated?
- What would we do differently?
- What external libraries were most helpful?
- What bugs were hardest to track down?
