# Ops Requests Dashboard - Frontend

Next.js 14 frontend application for managing operational requests.

## Tech Stack

- Next.js 14 (App Router)
- TypeScript
- Tailwind CSS
- Axios for API calls

## Getting Started

### Install Dependencies

```bash
npm install
```

### Environment Setup

Create a `.env.local` file:

```
NEXT_PUBLIC_API_URL=http://localhost:28080/api
```

### Development

```bash
npm run dev
```

The app will run on http://localhost:23000

### Production Build

```bash
npm run build
npm start
```

## Features

### Authentication
- Login and registration pages
- JWT token storage
- Automatic redirect on 401

### Employee View
- View all personal requests
- Create new requests
- Filter by status, type, priority
- View request details
- Add comments
- Cancel requests (if SUBMITTED)

### Manager View
- Approval queue for team requests
- Approve/reject with required comments
- View request details and audit trail

### Admin View
- View all requests in the system
- Change request status (APPROVED → IN_PROGRESS → DONE)
- Manage request types (CRUD)

## Project Structure

```
src/
├── app/                    # Next.js App Router pages
│   ├── login/             # Login page
│   ├── register/          # Registration page
│   ├── dashboard/         # Main dashboard
│   ├── requests/          # Employee requests
│   │   ├── new/          # Create request
│   │   └── [id]/         # Request detail
│   ├── approvals/         # Manager approvals
│   └── admin/             # Admin pages
│       └── request-types/ # Request type management
├── components/            # React components
├── services/             # API service layer
├── types/                # TypeScript types
└── lib/                  # Utility functions
```

## Routes

- `/login` - Login page
- `/register` - Registration page
- `/dashboard` - Main dashboard (redirects based on role)
- `/requests` - My requests list (employee view)
- `/requests/new` - Create new request form
- `/requests/[id]` - Request detail
- `/approvals` - Manager approval queue
- `/admin` - Admin dashboard
- `/admin/request-types` - Manage request types

## Docker

Build and run with Docker:

```bash
docker build -t ops-requests-frontend .
docker run -p 23000:23000 ops-requests-frontend
```
