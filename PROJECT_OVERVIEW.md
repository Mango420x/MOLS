# MOLS — Project Overview

## Purpose
- MOLS (Multimodal Operative Logistics System) is a backend-centric logistics management system designed with an industrial/defense-grade engineering mindset.
- Provides REST API services for managing logistics operations: units, warehouses, resources, stock, orders, shipments, vehicles, and movement auditing.
- Portfolio-grade backend demonstrating clean architecture, strict layering, auditability, and deterministic behavior.

## Project Status
- **Runtime**: Spring Boot 4.0.2 (Spring MVC) running on Java 21, connected to PostgreSQL.
- **Codebase**: Java sources under `src/main/java/com/mls/logistics`.
- **Entry Point**: `com.mls.logistics.LogisticsApplication`.
- **Architecture**: Classic four-layer architecture (Controllers → Services → Repositories → Database) with DTOs and a global exception handler.
- **Database**: PostgreSQL (`logistics_db` database, `logistics_user` credentials configured).
- **API Status**: CRUD REST API implemented and operational at `http://localhost:8080/api/*` (GET/POST/PUT/DELETE with DTOs + validation).
- **API Docs**: OpenAPI + Swagger UI configured (`OpenApiConfig`) and available at `/swagger-ui.html` and `/v3/api-docs`.
- **Build**: Maven wrapper present (`mvnw`, `mvnw.cmd`); build artifacts in `target/`.
- **Testing**: Controller and service test suites implemented and passing locally via Maven.
- **Containerization**: Docker multi-stage build + Docker Compose orchestration available for app + database.
- **Business Rules**: Core stock/order constraints already enforced in services (non-negative stock, automatic movement audit, order item stock ceiling).

## What This Repository Contains

### Backend: Four-Layer Architecture

#### 1. **REST Controller Layer** (`controller/`)
REST API endpoints exposing HTTP interfaces for all domain entities:
- `WarehouseController` — `/api/warehouses`
- `UnitController` — `/api/units`
- `ResourceController` — `/api/resources`
- `StockController` — `/api/stocks`
- `OrderController` — `/api/orders`
- `OrderItemController` — `/api/order-items`
- `VehicleController` — `/api/vehicles`
- `ShipmentController` — `/api/shipments`
- `MovementController` — `/api/movements`

**Controller Responsibilities**:
- HTTP request/response handling only
- No business logic (delegated to services)
- No direct repository access
- Constructor-based dependency injection
- Proper HTTP status codes (200 OK, 201 Created, 404 Not Found)
- Optional resolution at HTTP boundary
- Uses request DTOs with `@Valid` validation
- Exposes CRUD endpoints (GET, POST, PUT, DELETE)

#### 2. **Service Layer** (`service/`)
Business logic services for each domain entity. Services are the source of truth for all business operations.

**Standard Service Methods**:
- `getAll{Entity}()` — retrieves all records
- `get{Entity}ById(Long id)` — returns `Optional<Entity>`
- `create{Entity}(Entity entity)` — creates new record
- `create{Entity}(Create{Entity}Request request)` — creates from DTO
- `update{Entity}(Long id, Update{Entity}Request request)` — updates from DTO
- `delete{Entity}(Long id)` — deletes by id

**Service Classes**:
- `WarehouseService`, `UnitService`, `ResourceService`, `StockService`
- `OrderService`, `OrderItemService`, `VehicleService`, `ShipmentService`, `MovementService`

**Note**: `@Transactional` boundaries are implemented with read-only defaults.

#### 3. **Repository Layer** (`repository/`)
Spring Data JPA repositories handling only persistence operations.
- 9 repository interfaces extending `CrudRepository`
- No business logic allowed

#### 4. **Domain Layer** (`domain/`)
JPA entities representing the core business model. All entities exist and are mapped:
- `Unit`, `Warehouse`, `Resource`, `Stock`
- `Order`, `OrderItem`, `Vehicle`, `Shipment`, `Movement`

#### 5. **DTO Layer** (`dto/`)
Request/response DTOs define API contracts:
- Request DTOs for create/update operations (validation annotations included)
- Response DTOs for API output mapping

#### 6. **Exception Layer** (`exception/`)
Global error handling and standardized error responses:
- `GlobalExceptionHandler` with 400/404/500 handling
- `ErrorResponse`, `ResourceNotFoundException`, `InvalidRequestException`

### Configuration
- **Database**: PostgreSQL configured in `src/main/resources/application.properties`
- **Credentials**: Hardcoded for development (no environment variables yet)
- **Hibernate**: `ddl-auto=update` — auto-creates schema on startup
- **SQL Logging**: `spring.jpa.show-sql=true`
- **Port**: Application runs on `8080`
- **OpenAPI/Swagger**:
   - UI: `http://localhost:8080/swagger-ui.html`
   - JSON spec: `http://localhost:8080/v3/api-docs`
   - Springdoc properties configured in `application.properties`
- **Docker**:
   - `Dockerfile` uses multi-stage build (`eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre`)
   - `docker-compose.yml` orchestrates `mols-app` + `mols-db` with healthcheck
   - PostgreSQL host port mapping: `5433:5432`

## Where to Start (For a New Developer)

1. **Read Documentation**:
   - This file and `README.md` for project conventions
   - Review the `PROJECT_CONTEXT.md` for detailed architecture rules

2. **Understand the Domain Model**:
   - Inspect entities in `src/main/java/com/mls/logistics/domain`
   - All entities use constructor-based relationships

3. **Review the API**:
   - All controllers follow identical patterns
   - Check `WarehouseController` as the reference implementation
   - Explore endpoints in Swagger UI: `http://localhost:8080/swagger-ui.html`

4. **Setup Database**:
   ```powershell
   # Ensure PostgreSQL is running with logistics_db database
   # User: logistics_user, Password: logistics123
   ```

5. **Build and Run**:
   ```powershell
   ./mvnw.cmd clean install
   ./mvnw.cmd spring-boot:run
   ```

   **or with Docker Compose**:
   ```powershell
   docker compose up --build -d
   docker compose logs -f app
   ```

6. **Test the API**:
   ```bash
   # Get all warehouses
   curl http://localhost:8080/api/warehouses
   
   # Create a warehouse
   curl -X POST http://localhost:8080/api/warehouses \
     -H "Content-Type: application/json" \
     -d '{"name":"Central","location":"Madrid"}'
   ```

7. **Run Tests** (when available):
   ```powershell
   ./mvnw.cmd test
   ```

## Key Domain Concepts

- **`Unit`** — Organizational branch requesting resources (has location, name)
- **`Warehouse`** — Physical storage location for resources (has location, name)
- **`Resource`** — Item, part, or material (has type, criticality)
- **`Stock`** — Quantity of a resource in a warehouse (links Resource ↔ Warehouse)
- **`Order`** — Request placed by a Unit (has status, date)
- **`OrderItem`** — Individual line item in an order (links Order ↔ Resource, has quantity)
- **`Vehicle`** — Transport asset: land/air/sea (has type, capacity, status)
- **`Shipment`** — Assignment of resources to a vehicle (links Order ↔ Vehicle ↔ Warehouse)
- **`Movement`** — Audit record of stock changes (tracks type, quantity, datetime)

## Important Business Rules (Conceptual)

These rules are **enforced in services**, not controllers:

1. Stock must never go negative
2. Orders must not exceed available stock
3. Every stock change must generate a Movement record
4. Vehicles must be compatible with shipment transport type
5. Orders are complete only when fully delivered

### Current Enforcement Status

- ✅ Rule 1 implemented in `StockService.adjustStock()` and `StockService.createStock()`
   - Rejects negative initial quantity
   - Rejects adjustments that would produce negative stock (`InsufficientStockException`, HTTP 409)
- ✅ Rule 2 implemented in `StockService`
   - Every stock increase/decrease records a `Movement` (`ENTRY`/`EXIT`) automatically
- ✅ Rule 3 implemented in `OrderItemService.createOrderItem(CreateOrderItemRequest)`
   - Rejects order items where requested quantity exceeds total available stock (`InsufficientStockException`, HTTP 409)
- 🚧 Rules 4 and 5 remain planned for future phases.

## Developer Guidelines

### Architecture Rules (STRICTLY ENFORCED)

**Controllers**:
- ❌ NO business logic
- ❌ NO validation
- ❌ NO repository access
- ✅ ONLY HTTP request/response handling
- ✅ MUST use `ResponseEntity`
- ✅ MUST resolve `Optional` at HTTP boundary

**Services**:
- ✅ ALL business logic here
- ✅ Constructor-based dependency injection
- ✅ Method names must match existing pattern
- ❌ NO direct HTTP concerns

**Repositories**:
- ✅ ONLY persistence operations
- ✅ Spring Data JPA interfaces
- ❌ NO business logic

**Domain Entities**:
- ✅ JPA annotations only
- ✅ Relationships mapped with `@ManyToOne`, `@OneToMany`, etc.
- ❌ NO business logic

### Code Style

- **Language**: All code and comments in English
- **Injection**: Constructor-based only (no field injection)
- **Naming**: Explicit naming preferred over abstractions
- **Comments**: Explain WHY, not WHAT
- **Philosophy**: Clarity over cleverness, determinism over convenience

### Git Discipline

- Small, meaningful commits
- Descriptive commit messages
- Each commit represents a coherent change
- Follow conventional commits format when possible

### Package Structure

```
src/main/java/com/mls/logistics/
├── controller/      # REST API controllers (9 files)
├── domain/          # JPA entities (9 entities)
├── repository/      # Spring Data repositories (9 interfaces)
├── service/         # Business logic services (9 services)
├── exception/       # Global exception handling and error contracts
├── dto/             # Request/response DTO contracts
├── config/          # OpenAPI and app configuration classes
└── LogisticsApplication.java
```

### Containerization

```
.
├── Dockerfile
├── docker-compose.yml
└── .dockerignore
```

## Current Implementation Status

### ✅ Completed
- [x] Domain model (all 9 entities)
- [x] Repository layer (all 9 repositories)
- [x] Service layer (all 9 services)
- [x] Controller layer (all 9 controllers)
- [x] PostgreSQL database configuration
- [x] Hibernate schema auto-generation
- [x] CRUD REST API (GET, POST, PUT, DELETE)
- [x] DTOs for request/response contracts
- [x] Global exception handling (`@RestControllerAdvice`)
- [x] Input validation (`@Valid`, Bean Validation)
- [x] Transactional boundaries (`@Transactional`)
- [x] OpenAPI configuration (`OpenApiConfig`) and Swagger UI
- [x] Endpoint documentation annotations (`@Tag`, `@Operation`, `@ApiResponses`)
- [x] Automated test suite (controller + service)
- [x] Dockerization (multi-stage image build + compose stack)

### 🚧 Planned (In Order)
1. Enforce remaining domain rules (vehicle compatibility and order completion lifecycle)
2. Security (authentication/authorization)
3. Deeper integration testing (e.g., Testcontainers)
4. CI/CD pipeline

## API Endpoints Reference

All endpoints follow RESTful conventions:

| Entity | Base Path | GET All | GET by ID | POST Create | PUT Update | DELETE |
|--------|-----------|---------|-----------|-------------|------------|--------|
| Warehouse | `/api/warehouses` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Unit | `/api/units` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Resource | `/api/resources` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Stock | `/api/stocks` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Order | `/api/orders` | ✅ | ✅ | ✅ | ✅ | ✅ |
| OrderItem | `/api/order-items` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Vehicle | `/api/vehicles` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Shipment | `/api/shipments` | ✅ | ✅ | ✅ | ✅ | ✅ |
| Movement | `/api/movements` | ✅ | ✅ | ✅ | ✅ | ✅ |

Additional stock operation:
- `PATCH /api/stocks/{id}/adjust` — adjusts stock by delta and auto-creates movement audit record.

## Troubleshooting

### Business Rule Validation (2026-02-17)
- Stock negative prevention validated: adjusting below zero returns HTTP 409.
- Automatic movement audit validated: stock adjustment creates `ENTRY` movement.
- Order item stock ceiling validated: excessive quantity returns HTTP 409.

### Application Won't Start
- Check PostgreSQL is running: `psql -U logistics_user -d logistics_db`
- Verify credentials in `application.properties`
- Check logs for Hibernate schema creation errors

### Database Permission Errors
```sql
-- Grant schema privileges (as postgres user)
GRANT ALL ON SCHEMA public TO logistics_user;
ALTER DATABASE logistics_db OWNER TO logistics_user;
```

### Port Already in Use
- Change port in `application.properties`: `server.port=8081`
- Or kill process using port 8080

### Maven Build Fails
```powershell
./mvnw.cmd clean install -U  # Force update dependencies
```

### Docker Compose Issues
- Check containers:
   ```powershell
   docker compose ps
   ```
- Check app logs:
   ```powershell
   docker compose logs -f app
   ```
- Stop stack:
   ```powershell
   docker compose down
   ```

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.2 (Spring MVC, Spring Data JPA)
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Validation**: Spring Boot Starter Validation (Bean Validation)
- **Observability**: Spring Boot Actuator
- **Database**: PostgreSQL
- **ORM**: Hibernate (JPA)
- **Build Tool**: Maven 3.x (wrapper included)
- **Containerization**: Docker + Docker Compose
- **IDE**: VS Code (NOT IntelliJ)
- **OS**: Windows 11
- **Version Control**: GitHub (via GitHub Desktop)

## Contacts and Next Steps

- **Maintainer**: See `pom.xml` for project details
- **Documentation**: `README.md`, `PROJECT_CONTEXT.md`
- **Recommended Actions**:
  - Test all API endpoints with Postman/cURL
   - Enforce core business rules in services
   - Add unit/integration tests (controller + service + repository)
   - Review validation rules for all request DTOs

**Last updated**: 2026-02-17