# MOLS — Quick Help

Short operational guide for local development and validation.

## Prerequisites

- Java 21+
- PostgreSQL running locally
- Database and credentials expected by default:
	- DB: logistics_db
	- User: logistics_user
	- Password: logistics123

Configuration lives in src/main/resources/application.properties.

## Run Locally

```powershell
./mvnw.cmd clean compile
./mvnw.cmd spring-boot:run
```

API and docs:

- API base: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

UI:

- UI entry: http://localhost:8080/ui
- Login page: http://localhost:8080/ui/login

## Authentication and Authorization (JWT)

Public endpoints:

- POST /api/auth/register
- POST /api/auth/login

Protected endpoint policy:

- GET /api/** requires authenticated token (ADMIN or OPERATOR)
- POST/PUT/PATCH/DELETE /api/** requires ADMIN

JWT settings in application.properties:

- security.jwt.secret-key
- security.jwt.expiration-ms

## UI Login (Session)

The `/ui/**` area uses form login + session authentication.

Application users are stored in PostgreSQL table `app_users` (not PostgreSQL roles).

Bootstrap admin (dev-only) can be configured via environment variables:

- `BOOTSTRAP_ADMIN_ENABLED`
- `BOOTSTRAP_ADMIN_USERNAME`
- `BOOTSTRAP_ADMIN_PASSWORD`

If you don't know an application user's password, reset it in DB (requires `pgcrypto`):

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE app_users
SET password = crypt('NEW_PASSWORD', gen_salt('bf', 10))
WHERE username = 'admin';
```

## Run Tests

Full suite:

```powershell
./mvnw.cmd test
```

Current verified local status: 118 tests passing.

Current workspace status: 141 tests passing.

## CI Pipeline

GitHub Actions workflow:

- .github/workflows/ci.yml

Triggers and checks:

- Runs on push and pull_request to main
- Executes:
	- ./mvnw clean compile -B
	- ./mvnw test -B

## Useful References

- README.md
- PROJECT_OVERVIEW.md

