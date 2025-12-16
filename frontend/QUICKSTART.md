# Quick Start Guide

## Prerequisites

- Node.js 20+ installed
- Backend API running on http://localhost:28080

## Setup

1. Install dependencies:
```bash
npm install
```

2. Environment is already configured in `.env.local`:
```
NEXT_PUBLIC_API_URL=http://localhost:28080/api
```

3. Start the development server:
```bash
npm run dev
```

4. Open http://localhost:23000 in your browser

## First Steps

### 1. Register a New Account
- Navigate to http://localhost:23000/register
- Enter email and password
- Click "Register"
- You'll be automatically logged in and redirected

### 2. Create a Request (Employee)
- Go to "My Requests"
- Click "New Request"
- Fill out the form:
  - Request Type (select from dropdown)
  - Title
  - Description
  - Amount (optional)
  - Priority
- Click "Submit Request"

### 3. View Request Details
- Click on any request card
- View full details, comments, and audit trail
- Add comments
- Cancel request (if status is SUBMITTED)

### 4. Manager Approval (if you're a manager)
- Go to "Approvals"
- View pending requests
- Click on a request
- Click "Approve" or "Reject"
- Enter required comment
- Confirm

### 5. Admin Functions (if you're an admin)
- Go to "Admin Dashboard"
- View all requests
- Click "Manage Request Types"
- Create/Edit/Deactivate request types
- Change request status on detail page

## User Roles

The backend assigns roles. To test different roles:

1. **EMPLOYEE** - Can create and view own requests
2. **MANAGER** - Can approve/reject team requests
3. **ADMIN** - Can manage all requests and request types

## Common Commands

```bash
# Development
npm run dev

# Production build
npm run build
npm start

# Lint
npm run lint

# Docker build
docker build -t ops-requests-frontend .
docker run -p 23000:23000 ops-requests-frontend
```

## Troubleshooting

### API Connection Issues
- Verify backend is running on port 28080
- Check `.env.local` has correct API URL
- Check browser console for CORS errors

### Build Errors
- Clear `.next` folder: `rm -rf .next`
- Reinstall dependencies: `rm -rf node_modules && npm install`
- Check Node.js version: `node --version` (should be 20+)

### Authentication Issues
- Clear localStorage in browser DevTools
- Check JWT token is being sent in Network tab
- Verify backend authentication endpoints are working

## File Locations

- **Pages:** `src/app/`
- **Components:** `src/components/`
- **API Service:** `src/services/api.ts`
- **Types:** `src/types/index.ts`
- **Utilities:** `src/lib/`
- **Styles:** `src/app/globals.css`

## Next Steps

- Test all user flows
- Add more request types via admin panel
- Create sample requests
- Test approval workflow
- Test admin status changes
