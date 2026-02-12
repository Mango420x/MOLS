# MOLS — Project Overview

Purpose
- MOLS (Multimodal Logistics System) provides backend services and a simple web UI for managing logistics: units, warehouses, resources, stock, orders, shipments and vehicles.

Project status
- Codebase: Java Spring Boot, sources under `src/main/java/com/mls/logistics`.
- Entrypoint: `com.mls.logistics.LogisticsApplication`.
- Persistence: JPA entities and Spring Data repositories under `src/main/java/com/mls/logistics/domain` and `.../repository`.
- Tests: unit and integration tests in `src/test/java`; test reports in `target/surefire-reports`.
- Build: Maven wrapper present (`mvnw`, `mvnw.cmd`); build artifacts in `target/`.
- Note: an IDE run previously exited with a non-zero code; check runtime logs and `target/surefire-reports` for failures.

What this repository contains
- Backend: Spring Boot application, service layer, domain entities, repositories.
- Lightweight UI: static HTML/CSS/JS under `src/main/resources/static` (if present).
- Tests, configuration, and Maven build files (`pom.xml`).

Where to start (for a new developer)
1. Read this file and `README.md` for project conventions.
2. Inspect the domain model in `src/main/java/com/mls/logistics/domain` to understand entities and relationships.
3. Run tests locally:

```powershell
./mvnw.cmd test
```

4. Build and run the application locally:

```powershell
./mvnw.cmd clean install
./mvnw.cmd spring-boot:run
```

Key domain concepts (summary)
- `Unit` — organization/branch requesting resources.
- `Warehouse` — storage location for `Resource`s.
- `Resource` — item or part.
- `Stock` — quantity of a resource at a warehouse.
- `Order` / `OrderItem` — placed by `Unit` to request resources.
- `Vehicle` — transport asset (terrestrial/maritime/aerial).
- `Shipment` — resources assigned to a `Vehicle`.
- `Movement` — audit record for changes to `Stock`.

Important business rules
- Orders must not exceed available stock.
- Stock cannot be negative; each stock change produces a `Movement` record.
- Vehicles must be available and compatible with the shipment transport type.
- Orders are complete only when all items are delivered.

Developer guidelines
- Keep business logic in services; repositories should be focused on persistence queries.
- Add unit tests for new behavior under `src/test/java` and run `./mvnw.cmd test` before submitting changes.
- For database schema changes, include a migration strategy and update integration tests.

Troubleshooting
- If startup fails: check the IDE/terminal logs and `target/surefire-reports` for test failures.
- If DB issues appear, verify local PostgreSQL configuration or switch tests to H2 as configured.

Contacts and next steps
- See `README.md` and `pom.xml` for maintainer and project conventions.
- Recommended immediate actions for contributors: run the test suite, fix failing tests if any, or submit small PRs for documentation improvements.

Last updated: 2026-02-12
