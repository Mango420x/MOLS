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
- **Build**: Maven wrapper present (`mvnw`, `mvnw.cmd`); build artifacts in `target/`.
- **Testing**: Basic Spring context test only; no H2/Testcontainers integration yet.

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

**Note**: Full validation implementation is planned for future phases.

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
├── exception/       # Custom exceptions (planned)
├── dto/             # DTOs for API contracts (planned)
└── LogisticsApplication.java
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

### 🚧 Planned (In Order)
1. Enforce domain business rules (stock, order/stock constraints, movement audit)
2. Security (authentication/authorization)
3. Comprehensive testing (unit + integration)
4. Dockerization
5. CI/CD pipeline

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

## Troubleshooting

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

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.2 (Spring MVC, Spring Data JPA)
- **Validation**: Spring Boot Starter Validation (Bean Validation)
- **Observability**: Spring Boot Actuator
- **Database**: PostgreSQL
- **ORM**: Hibernate (JPA)
- **Build Tool**: Maven 3.x (wrapper included)
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