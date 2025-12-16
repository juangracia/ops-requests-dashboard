# Requirements Specification

## Table of Contents
- [Project Scope](#project-scope)
- [User Roles](#user-roles)
- [User Stories](#user-stories)
  - [Employee Stories](#employee-stories)
  - [Manager Stories](#manager-stories)
  - [Operations Admin Stories](#operations-admin-stories)
  - [Cross-Role Stories](#cross-role-stories)
- [Request Status Model](#request-status-model)
- [Business Rules](#business-rules)
- [Request Types](#request-types)
- [Data Validation Rules](#data-validation-rules)
- [Non-Functional Requirements](#non-functional-requirements)
- [Out of Scope](#out-of-scope)

## Project Scope

The Ops Requests Dashboard is an MVP (Minimum Viable Product) for managing internal operational requests within a small to medium-sized company. The system handles the complete lifecycle from request submission through approval, fulfillment, and completion.

**In Scope for MVP:**
- User authentication and role-based authorization
- Request submission, viewing, editing, and cancellation
- Single-step manager approval workflow
- Operations team fulfillment tracking
- Audit trail for all state changes
- Comment system for decisions and updates
- Search and filter capabilities

**Key Workflows:**
1. Employee submits request → Manager approves/rejects → Ops Admin fulfills
2. Request state transitions with audit logging
3. Role-based permissions enforcement

## User Roles

### Employee (ROLE_EMPLOYEE)
**Description**: Regular company employees who submit operational requests

**Capabilities:**
- Submit new requests
- View own requests
- Edit own requests (only in SUBMITTED status)
- Cancel own requests (only in SUBMITTED or APPROVED status)
- Add comments to own requests

**Limitations:**
- Cannot view other employees' requests
- Cannot approve or reject requests
- Cannot change request status
- Cannot reassign requests

### Manager (ROLE_MANAGER)
**Description**: Team leads or department managers who approve requests from their direct reports

**Capabilities:**
- All employee capabilities
- View requests from direct reports
- Approve requests from direct reports
- Reject requests from direct reports
- Must provide comments when approving or rejecting

**Limitations:**
- Cannot approve own requests
- Cannot move requests to IN_PROGRESS or DONE
- Cannot view requests outside their team
- Cannot edit requests they don't own

### Operations Admin (ROLE_ADMIN)
**Description**: HR, Finance, IT, or Operations staff who fulfill approved requests and manage the system

**Capabilities:**
- View all requests across the organization
- Move approved requests to IN_PROGRESS
- Move in-progress requests to DONE
- Add comments to any request
- Manage request types (create, deactivate)
- Manage users (create, deactivate, assign managers)

**Limitations:**
- Cannot approve or reject requests (that's the manager's role)
- Cannot cancel requests they don't own

## User Stories

### Employee Stories

#### US-E-001: Submit Purchase Request
**As an** employee
**I want to** submit a purchase request with title, description, amount, and priority
**So that** my manager can review and approve the purchase

**Acceptance Criteria:**
- Form includes: title (required), description (required), amount (optional), priority (LOW/MEDIUM/HIGH), request type
- Title must be 5-200 characters
- Description must be 10-2000 characters
- Amount must be positive if provided
- Request is created in SUBMITTED status
- My manager is automatically assigned based on my user profile
- I receive confirmation after successful submission
- Audit event is created for submission

#### US-E-002: View My Requests
**As an** employee
**I want to** view a list of all my requests
**So that** I can track their status

**Acceptance Criteria:**
- List shows: title, type, status, priority, created date, last updated date
- List is sorted by created date (newest first) by default
- I can filter by status (all, submitted, approved, rejected, in progress, done, cancelled)
- I can filter by request type
- I can search by title or description
- Clicking a request shows full details

#### US-E-003: View Request Details
**As an** employee
**I want to** view complete details of my request
**So that** I can see all information, comments, and history

**Acceptance Criteria:**
- Details page shows: all request fields, current status, manager name, all comments with timestamps and authors
- Audit history is visible showing: who did what when
- Available actions are shown based on current status (edit, cancel)

#### US-E-004: Edit Submitted Request
**As an** employee
**I want to** edit my request while it's still in SUBMITTED status
**So that** I can correct mistakes before manager review

**Acceptance Criteria:**
- Edit button is only visible for requests in SUBMITTED status
- I can modify: title, description, amount, priority, request type
- I cannot modify: requester, manager, status, timestamps
- Audit event is created for the edit
- Manager sees the updated information

#### US-E-005: Cancel My Request
**As an** employee
**I want to** cancel my request if I no longer need it
**So that** I don't waste my manager's time

**Acceptance Criteria:**
- Cancel button is visible for requests in SUBMITTED or APPROVED status
- System prompts for cancellation reason (comment)
- Request status changes to CANCELLED
- Audit event is created with cancellation reason
- Cannot cancel requests in IN_PROGRESS, DONE, REJECTED, or already CANCELLED

#### US-E-006: Add Comment to My Request
**As an** employee
**I want to** add comments to my request
**So that** I can provide additional context or updates

**Acceptance Criteria:**
- Comment form is available on request details page
- Comment must be 1-1000 characters
- Comment shows my name and timestamp
- All parties (employee, manager, admin) can see the comment
- Audit event is created for new comment

### Manager Stories

#### US-M-001: View Team Requests
**As a** manager
**I want to** view all requests from my direct reports
**So that** I can see what needs my attention

**Acceptance Criteria:**
- Dashboard shows requests from all my direct reports
- Pending requests (SUBMITTED status) are highlighted or prioritized
- I can filter by: employee, status, request type, priority, date range
- I can sort by: created date, updated date, priority, amount
- Badge or count shows number of pending approvals

#### US-M-002: Approve Request
**As a** manager
**I want to** approve a request from my team member with a comment
**So that** the operations team can fulfill it

**Acceptance Criteria:**
- Approve button is only visible for requests in SUBMITTED status from my direct reports
- System requires me to add a comment explaining my approval
- Comment must be at least 10 characters
- Request status changes to APPROVED
- Audit event is created with my comment
- Operations team can now see and act on the request

#### US-M-003: Reject Request
**As a** manager
**I want to** reject a request with a clear explanation
**So that** the employee understands why it was denied

**Acceptance Criteria:**
- Reject button is only visible for requests in SUBMITTED status from my direct reports
- System requires me to add a comment explaining my rejection
- Comment must be at least 10 characters
- Request status changes to REJECTED
- Audit event is created with my comment
- Employee can see the rejection reason
- Request cannot be edited or resubmitted (employee must create new request)

#### US-M-004: View Request Details
**As a** manager
**I want to** view complete request details including history
**So that** I can make informed approval decisions

**Acceptance Criteria:**
- Details page shows: all request fields, requester information, all comments, full audit history
- If request was edited, I can see what changed and when
- I can see previous requests from this employee for context

#### US-M-005: Cannot Approve Own Requests
**As a** manager
**I want** the system to prevent me from approving my own requests
**So that** proper separation of duties is maintained

**Acceptance Criteria:**
- When viewing my own requests, approve/reject buttons are not shown
- API rejects any attempt to approve my own request with 403 Forbidden
- My manager (if I have one) must approve my requests
- Clear message indicates I cannot approve my own requests

### Operations Admin Stories

#### US-A-001: View All Requests
**As an** operations admin
**I want to** view all requests across the organization
**So that** I can manage the fulfillment queue

**Acceptance Criteria:**
- Dashboard shows all requests regardless of requester or manager
- Default view shows approved requests needing action
- I can filter by: status, requester, manager, request type, priority, date range, amount range
- I can sort by: created date, approved date, priority, amount
- Export to CSV option is available

#### US-A-002: Move Request to In Progress
**As an** operations admin
**I want to** mark an approved request as in progress
**So that** everyone knows I'm actively working on it

**Acceptance Criteria:**
- "Start Work" button is visible for requests in APPROVED status
- Optional comment can be added (e.g., "Ordered from Vendor X, ETA 5 days")
- Request status changes to IN_PROGRESS
- Audit event is created
- Requester and manager can see the status update

#### US-A-003: Mark Request as Done
**As an** operations admin
**I want to** mark an in-progress request as done
**So that** everyone knows the request is fulfilled

**Acceptance Criteria:**
- "Complete" button is visible for requests in IN_PROGRESS status
- Optional comment can be added (e.g., "Laptop delivered to desk, tracking #12345")
- Request status changes to DONE
- Audit event is created
- Request is removed from active work queue
- Requester and manager can see completion

#### US-A-004: Manage Request Types
**As an** operations admin
**I want to** create and deactivate request types
**So that** the system reflects current company needs

**Acceptance Criteria:**
- Admin panel shows all request types with active/inactive status
- I can create new request type with: code (unique), name
- I can deactivate (not delete) request types
- Inactive types don't appear in employee dropdown but existing requests still show the type
- Cannot delete request types that have existing requests

#### US-A-005: Manage Users
**As an** operations admin
**I want to** create users and assign managers
**So that** the approval workflow works correctly

**Acceptance Criteria:**
- Admin panel shows all users with role and active status
- I can create new user with: email, temporary password, role, manager assignment
- I can assign or change manager for any employee
- I can deactivate (not delete) users
- Deactivated users cannot log in but their historical data is preserved
- System prevents circular manager assignments (A reports to B, B reports to A)

### Cross-Role Stories

#### US-X-001: Secure Login
**As a** user
**I want to** log in with email and password
**So that** my requests and data are secure

**Acceptance Criteria:**
- Login form accepts email and password
- Passwords are hashed with BCrypt (never stored plain text)
- Invalid credentials return generic error (don't reveal if email exists)
- Successful login returns JWT token
- Token expires after 24 hours
- Token includes user ID, email, and role

#### US-X-002: View Audit History
**As a** user
**I want to** see complete audit history for requests I can access
**So that** I have transparency into all changes

**Acceptance Criteria:**
- Audit history shows: timestamp, actor name, action type, status changes, comments/notes
- Events are sorted chronologically (oldest to newest)
- All state transitions are logged
- All comment additions are logged
- Edits to request fields are logged
- System changes (like auto-assignment) are logged with actor "SYSTEM"

## Request Status Model

The request lifecycle follows this state machine:

```
                    SUBMITTED
                        |
        +---------------+---------------+
        |                               |
    APPROVED                        REJECTED
        |                           (terminal)
        |
   IN_PROGRESS
        |
      DONE
   (terminal)

   CANCELLED can occur from SUBMITTED or APPROVED
   (terminal)
```

### Status Definitions

| Status | Description | Who Can Set | Next Valid States |
|--------|-------------|-------------|-------------------|
| SUBMITTED | Initial state when employee creates request | System (on creation) | APPROVED, REJECTED, CANCELLED |
| APPROVED | Manager has approved the request | Manager only | IN_PROGRESS, CANCELLED |
| REJECTED | Manager has rejected the request | Manager only | (terminal - no transitions) |
| IN_PROGRESS | Admin is actively working on fulfillment | Admin only | DONE |
| DONE | Request is completely fulfilled | Admin only | (terminal - no transitions) |
| CANCELLED | Requester cancelled before completion | Requester only | (terminal - no transitions) |

### State Transition Rules

1. **SUBMITTED → APPROVED**: Manager approves with mandatory comment
2. **SUBMITTED → REJECTED**: Manager rejects with mandatory comment
3. **SUBMITTED → CANCELLED**: Requester cancels with mandatory reason
4. **APPROVED → IN_PROGRESS**: Admin starts work with optional comment
5. **APPROVED → CANCELLED**: Requester cancels with mandatory reason
6. **IN_PROGRESS → DONE**: Admin completes work with optional comment

**Invalid Transitions** (system must prevent):
- Cannot go from REJECTED, DONE, or CANCELLED to any other state
- Cannot skip states (e.g., SUBMITTED → IN_PROGRESS)
- Cannot reverse states (e.g., IN_PROGRESS → APPROVED)

## Business Rules

### BR-001: Manager Assignment
- Every request must have a manager assigned at creation time
- Manager is snapshot from requester's user profile at submission time
- If requester's manager changes later, existing requests keep original manager
- If requester has no manager, they cannot submit requests (system must enforce)

### BR-002: Self-Approval Prevention
- Users cannot approve their own requests, even if they have manager role
- API must validate that request.managerId ≠ current user ID for approve/reject actions

### BR-003: Mandatory Comments on Decisions
- Managers must provide comment when approving or rejecting
- Comment minimum length: 10 characters
- Comment is stored as RequestComment and referenced in audit event

### BR-004: Edit Restrictions
- Requesters can only edit requests in SUBMITTED status
- Allowed edits: title, description, amount, priority, request type
- Forbidden edits: status, requester, manager, timestamps
- Each edit creates audit event

### BR-005: Cancellation Rules
- Requesters can cancel only from SUBMITTED or APPROVED status
- Cannot cancel from IN_PROGRESS, DONE, REJECTED, or already CANCELLED
- Cancellation requires mandatory reason (comment)
- Cancellation creates audit event with reason

### BR-006: Status Transition Authority
- Only managers can set APPROVED or REJECTED
- Only admins can set IN_PROGRESS or DONE
- Only requesters can set CANCELLED
- System role must be enforced at API level

### BR-007: Audit Trail Completeness
- Every state change creates audit event
- Every comment creates audit event
- Audit events are immutable (insert-only, never update/delete)
- Audit events include: request ID, actor ID, timestamp, event type, old status, new status, optional note

### BR-008: Data Retention
- Soft delete only (users, request types set active=false)
- Never hard delete requests, comments, or audit events
- Deactivated users cannot log in but their data remains

### BR-009: Amount Handling
- Amount is optional (not all requests have costs)
- If provided, must be positive number
- Stored as decimal with 2 decimal places precision
- Currency is USD (implicit, no multi-currency support in MVP)

### BR-010: Priority Defaults
- If not specified, default priority is MEDIUM
- Valid values: LOW, MEDIUM, HIGH
- Priority affects display order but not workflow logic in MVP

## Request Types

Initial request types for seeding:

| Code | Name | Description |
|------|------|-------------|
| PURCHASE | Purchase Request | Equipment, software, supplies |
| IT_ACCESS | IT Access Request | System access, permissions, credentials |
| HR | HR Request | Time off, training, benefits |
| TRAVEL | Travel Request | Business travel, conferences, client visits |
| OTHER | Other | Miscellaneous operational requests |

**Rules:**
- Code must be unique, uppercase with underscores
- Name must be unique
- Admins can add more types via admin panel
- Cannot delete types, only deactivate

## Data Validation Rules

### User
- Email: required, valid format, unique, max 255 chars
- Password: min 8 chars, must have uppercase, lowercase, number
- Role: required, must be ROLE_EMPLOYEE, ROLE_MANAGER, or ROLE_ADMIN
- Manager ID: optional (required for employees, null for top-level)

### Request
- Title: required, 5-200 characters
- Description: required, 10-2000 characters
- Amount: optional, if provided must be > 0, max 999999.99
- Priority: required, must be LOW, MEDIUM, or HIGH
- Request Type: required, must reference active RequestType
- Status: required, must be valid enum value
- Requester: required, must reference active User
- Manager: required, must reference active User with ROLE_MANAGER

### Comment
- Comment text: required, 1-1000 characters
- Author: required, must reference active User
- Request: required, must reference existing Request

### Audit Event
- Event type: required, must be valid enum (CREATED, UPDATED, STATUS_CHANGED, COMMENTED, etc.)
- Actor: required, must reference User or "SYSTEM"
- Timestamp: required, auto-generated
- Request: required, must reference existing Request

## Non-Functional Requirements

### NFR-001: Performance
- Request list page loads in < 2 seconds for up to 1000 requests
- Request details page loads in < 1 second
- Search and filter results return in < 1 second

### NFR-002: Security
- All passwords hashed with BCrypt strength 12
- JWT tokens signed with secure secret
- All endpoints except /login require valid JWT
- Role-based authorization enforced on every endpoint
- SQL injection prevented via parameterized queries
- XSS prevented via output encoding

### NFR-003: Data Integrity
- Foreign key constraints enforced at database level
- Transaction boundaries around multi-step operations
- Unique constraints on email, request type codes
- Not-null constraints on required fields

### NFR-004: Usability
- Forms show clear validation errors
- Success and error messages are user-friendly
- Current user's role and permissions are clear
- Loading states shown during async operations

### NFR-005: Maintainability
- Code follows consistent style guide
- Business logic separated from controllers
- DTOs used for API requests/responses
- Database migrations are versioned and tracked

### NFR-006: Testability
- Unit tests for service layer business logic
- Integration tests for API endpoints
- Test database separate from development database
- Test data builders for easy fixture creation

## Out of Scope

The following are explicitly NOT included in the MVP:

### Workflow Features
- Multi-step approval chains (e.g., manager → director → VP)
- Parallel approvals (multiple approvers must all approve)
- Conditional approval routing based on amount or type
- Approval delegation (manager assigns approval to another manager)
- Escalation rules (auto-approve if manager doesn't respond in X days)

### Notification Features
- Email notifications for status changes
- Slack or Teams integration
- In-app notification center
- SMS notifications
- Push notifications

### Attachment Features
- File uploads on requests
- Image attachments
- Document management
- Receipt scanning

### Advanced Features
- Request templates for common requests
- Recurring requests (e.g., monthly subscriptions)
- Bulk operations (approve multiple at once)
- Advanced analytics dashboard
- Budget tracking and enforcement
- Department or cost center management
- Reporting and exports beyond basic CSV

### Multi-Tenant Features
- Support for multiple companies in one instance
- Company-specific branding
- Per-tenant configuration

### Authentication Features
- Single Sign-On (OAuth, SAML)
- Multi-factor authentication
- Social login (Google, Microsoft)
- Password reset via email (requires email integration)

### Internationalization
- Multi-language support
- Timezone handling (all times UTC in MVP)
- Multi-currency support

### Real-Time Features
- WebSocket updates
- Live status changes without refresh
- Online/offline indicators
- Real-time collaboration on requests
