# Default Credentials

## Test Users

### Admin
- **Email**: admin@example.com
- **Password**: admin123
- **Role**: ADMIN
- **Permissions**: Full access to all operations

### Manager
- **Email**: manager@example.com
- **Password**: manager123
- **Role**: MANAGER
- **Permissions**: Approve/reject requests assigned to them

### Employee 1
- **Email**: employee1@example.com
- **Password**: employee123
- **Role**: EMPLOYEE
- **Manager**: manager@example.com
- **Permissions**: Create and manage own requests

### Employee 2
- **Email**: employee2@example.com
- **Password**: employee123
- **Role**: EMPLOYEE
- **Manager**: manager@example.com
- **Permissions**: Create and manage own requests

## Database

- **Host**: localhost
- **Port**: 25432
- **Database**: opsrequests
- **User**: postgres
- **Password**: postgres

## API

- **Base URL**: http://localhost:28080
- **Swagger UI**: http://localhost:28080/swagger-ui.html
- **API Docs**: http://localhost:28080/api-docs

## Request Types

1. **PURCHASE** - Purchase Request
2. **IT_ACCESS** - IT Access Request
3. **HR** - HR Request
4. **TRAVEL** - Travel Request
