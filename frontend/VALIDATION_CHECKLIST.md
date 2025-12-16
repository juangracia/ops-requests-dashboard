# Frontend Validation Checklist

Use this checklist to validate the frontend application is working correctly.

## Pre-Deployment Checks

### Build and Configuration
- [ ] `npm install` completes successfully
- [ ] `npm run build` completes without errors
- [ ] `.env.local` contains `NEXT_PUBLIC_API_URL`
- [ ] Backend API is running on configured port
- [ ] No TypeScript compilation errors
- [ ] ESLint shows only warnings (no errors)

### File Structure
- [ ] All page files exist in `src/app/`
- [ ] All components exist in `src/components/`
- [ ] API service exists in `src/services/`
- [ ] Types defined in `src/types/`
- [ ] Utilities in `src/lib/`

## Functional Testing

### Authentication Flow
- [ ] Can access login page at `/login`
- [ ] Can register new user at `/register`
- [ ] Registration redirects to dashboard
- [ ] Login with valid credentials works
- [ ] Login with invalid credentials shows error
- [ ] Logout clears session and redirects to login
- [ ] Accessing protected routes without auth redirects to login

### Employee Workflow
- [ ] Can view "My Requests" page
- [ ] Filter bar appears and functions
- [ ] Can click "New Request" button
- [ ] Create request form loads
- [ ] Can select request type
- [ ] Can enter title, description, amount, priority
- [ ] Form validation works (required fields)
- [ ] Submit creates request successfully
- [ ] Redirects to request detail after creation
- [ ] Can view request detail page
- [ ] Request details display correctly
- [ ] Can add comments to request
- [ ] Comments appear in list
- [ ] Can cancel SUBMITTED request
- [ ] Cancel button not visible for other statuses
- [ ] Audit trail displays events

### Manager Workflow
- [ ] Can access "Approvals" page
- [ ] Pending requests display
- [ ] Filter by status works
- [ ] Can click on request to view details
- [ ] Approve button appears for SUBMITTED requests
- [ ] Reject button appears for SUBMITTED requests
- [ ] Clicking approve shows modal
- [ ] Comment field is required
- [ ] Approval succeeds with comment
- [ ] Rejection succeeds with comment
- [ ] Status updates after approval/rejection
- [ ] Approve/reject buttons hidden after action

### Admin Workflow
- [ ] Can access "Admin Dashboard"
- [ ] All requests visible (not just own)
- [ ] Filter bar functions correctly
- [ ] "Manage Request Types" button visible
- [ ] Can access request types page
- [ ] Request types table displays
- [ ] Can click "Add New Type"
- [ ] Create form appears
- [ ] Can enter code and name
- [ ] Create request type succeeds
- [ ] New type appears in table
- [ ] Can edit existing type
- [ ] Edit form pre-populates
- [ ] Update succeeds
- [ ] Can deactivate type
- [ ] Can activate type
- [ ] Status badge shows active/inactive
- [ ] Can change APPROVED request to IN_PROGRESS
- [ ] Can change IN_PROGRESS request to DONE
- [ ] Status change buttons only show for valid transitions

### UI/UX Validation
- [ ] Navbar displays user email
- [ ] Navbar displays user role badge
- [ ] Navigation links appropriate for role
- [ ] Status badges show correct colors
- [ ] Priority badges show correct colors
- [ ] Loading states appear during async operations
- [ ] Error messages display in red boxes
- [ ] Success messages display in green boxes
- [ ] Forms disable submit button while loading
- [ ] Confirmation dialogs appear for destructive actions
- [ ] Empty states show helpful messages
- [ ] Mobile responsive (test on small screen)
- [ ] All text readable and properly formatted
- [ ] Dates formatted correctly
- [ ] Currency formatted correctly

### Error Handling
- [ ] 401 response redirects to login
- [ ] API errors display user-friendly messages
- [ ] Network errors handled gracefully
- [ ] Form validation errors show
- [ ] Missing required fields prevent submission
- [ ] Invalid data shows error message

### Integration Testing
- [ ] Create request as employee
- [ ] Approve as manager (different user)
- [ ] Change status as admin
- [ ] Add comments from different roles
- [ ] Filter requests by various criteria
- [ ] Full workflow: create → approve → in progress → done

## Performance Checks
- [ ] Page load time acceptable (<3s)
- [ ] No console errors in browser
- [ ] No console warnings (except React hooks)
- [ ] Images load properly (if any)
- [ ] Transitions smooth
- [ ] No memory leaks (check DevTools)

## Security Validation
- [ ] JWT token stored in localStorage
- [ ] Token sent in Authorization header
- [ ] Token cleared on logout
- [ ] Protected routes inaccessible without auth
- [ ] Role-based features hidden appropriately
- [ ] No sensitive data in console logs
- [ ] No API credentials in frontend code

## Browser Compatibility
- [ ] Works in Chrome
- [ ] Works in Firefox
- [ ] Works in Safari
- [ ] Works in Edge
- [ ] Works on mobile browsers

## Docker Validation
- [ ] Docker build succeeds
- [ ] Container runs on port 23000
- [ ] Application accessible in container
- [ ] Environment variables work in container

## Documentation Review
- [ ] README.md is clear and accurate
- [ ] QUICKSTART.md provides valid instructions
- [ ] IMPLEMENTATION.md matches actual code
- [ ] All commands in docs work correctly

## Code Quality
- [ ] No TODO comments left in code
- [ ] No console.log statements (except error logging)
- [ ] No commented-out code blocks
- [ ] Consistent code formatting
- [ ] TypeScript types used throughout
- [ ] Imports optimized (no unused imports)
- [ ] No hardcoded values (use constants/env vars)

## Deployment Readiness
- [ ] Production build succeeds
- [ ] Standalone mode works
- [ ] PORT environment variable respected
- [ ] API_URL configurable
- [ ] No development-only code in production
- [ ] Error boundaries in place
- [ ] Logging appropriate for production

## Known Issues to Verify

These are expected limitations (not bugs):
- [ ] No server-side auth middleware (client-side only)
- [ ] No pagination (loads all data)
- [ ] No request editing (only cancel)
- [ ] useEffect warnings in ESLint (acceptable)
- [ ] localStorage only (no session cookies)

## Final Sign-Off

- [ ] All critical features tested
- [ ] No blocking bugs found
- [ ] Documentation accurate
- [ ] Ready for handoff
- [ ] Backend integration verified
- [ ] User flows complete end-to-end

## Notes

Record any issues found during validation:

```
Issue 1:
Description:
Severity:
Resolution:

Issue 2:
Description:
Severity:
Resolution:
```

## Tester Information

- Tester Name: _______________
- Test Date: _______________
- Environment: _______________
- Backend Version: _______________
- Frontend Version: _______________
- Browser: _______________
- OS: _______________

## Approval

- [ ] All tests passed
- [ ] Ready for production deployment
- [ ] Approved by: _______________
- [ ] Date: _______________
