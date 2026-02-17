# MOLS — Multimodal Operative Logistics System

![CI](https://github.com/Mango420x/MOLS/actions/workflows/ci.yml/badge.svg)

**Short Description:**  
A web-based logistics system for tracking resources, stock, and transport across land, sea, and air. Built for clear workflows and full traceability.

---

## Overview

MOLS helps units request resources, warehouses manage stock, and teams coordinate shipments and vehicles. It keeps a complete history of what happened, when, and where.

This is a portfolio-grade backend project focused on clean structure, traceability, and real-world logistics workflows.

For deeper technical details, see [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md).

---

<details>
<summary>Resumen en español (🇪🇸)</summary>

MOLS es un proyecto de portfolio profesional que demuestra una arquitectura backend clara y mantenible para operaciones logísticas reales. Permite gestionar pedidos, stock, almacenes, vehículos y envíos, con trazabilidad completa de movimientos.

La arquitectura se organiza en capas (API, servicios, datos y dominio), lo que facilita el mantenimiento, el testeo y la evolución del sistema. Para más detalle tecnico, consulta [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md).
</details>

---

## Technologies (High-Level)

MOLS is built as a modern backend-first system:

- **Java + Spring Boot** provide the core REST API and service layer.
- **PostgreSQL** stores operational data with strong consistency.
- **Maven** manages builds and dependencies.
- **OpenAPI (Springdoc)** provides interactive API documentation via Swagger UI.
- **Spring Security + JWT** secure the API with stateless authentication and role-based authorization.
- **Docker + Docker Compose** provide reproducible local runtime for app + PostgreSQL.

These choices emphasize reliability, traceability, and long-term maintainability.

---

## Architecture

MOLS follows a clean, layered architecture that separates responsibilities and keeps the system predictable:

- **API Layer**: REST controllers expose endpoints and handle HTTP concerns.
- **Service Layer**: business rules and workflows live here.
- **Data Layer**: repositories manage persistence and database access.
- **Domain Model**: entities represent real-world logistics concepts.

This structure keeps logic centralized, makes testing easier, and supports future growth.

---

## What You Can Do

- Create and track orders from units
- Manage warehouses and stock levels
- Assign vehicles and follow shipments
- Keep a full audit trail of movements
- Explore and test all endpoints through Swagger UI

---

## Business Rules Status

Currently enforced in the service layer:

- Stock cannot go negative (invalid adjustments return HTTP 409)
- Stock changes automatically create movement audit records (`ENTRY`/`EXIT`)
- Order item quantity cannot exceed available stock (returns HTTP 409)

---

## Security (JWT + Roles)

MOLS now uses stateless JWT authentication:

- Public endpoints:
	- `POST /api/auth/register`
	- `POST /api/auth/login`
- Protected API rules:
	- `GET /api/**` → authenticated users (`ADMIN` or `OPERATOR`)
	- `POST/PUT/PATCH/DELETE /api/**` → `ADMIN` only

Verified locally:

- No token on protected GET returns `403`
- Valid token on GET returns `200`
- `OPERATOR` token on protected POST returns `403`

---

## API Documentation (Swagger)

With the application running:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

Controllers are documented with OpenAPI annotations (`@Tag`, `@Operation`, `@ApiResponses`) for consistent endpoint docs.

Notable stock operation:

- `PATCH /api/stocks/{id}/adjust` adjusts quantity by delta and records movement automatically.

---

## Getting Started

1. Clone the repository.
2. Configure the database in `application.properties`.
3. Run:
	```powershell
	./mvnw.cmd clean compile
	./mvnw.cmd spring-boot:run
	```
4. Open:
	- API base: http://localhost:8080
	- Swagger UI: http://localhost:8080/swagger-ui.html

---

## Run with Docker

This repository includes:

- `Dockerfile` (multi-stage image build)
- `docker-compose.yml` (app + PostgreSQL stack)

Run everything with:

```powershell
docker compose up --build -d
```

Useful commands:

```powershell
docker compose ps
docker compose logs -f app
docker compose down
```

Ports:

- App: `8080` (host) → `8080` (container)
- Postgres: `5433` (host) → `5432` (container)

---

## Testing

Run all tests with:

```powershell
./mvnw.cmd test
```

Current local status: full suite passing (`118` tests).

---

## License

This project is for educational and portfolio purposes.

---

## Contributing

Contributions are welcome. Focus on clearer workflows, better validation, and stronger testing.
