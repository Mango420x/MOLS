# MOLS — Multimodal Operative Logistics System

<p align="center">
  <img src="https://github.com/user-attachments/assets/6c3cae38-df16-4c85-a334-6a53cb1f27fa" width="300" alt="Gemini_Generated_Image">
</p>

![CI](https://github.com/Mango420x/MOLS/actions/workflows/ci.yml/badge.svg)

Portfolio project: a web-based logistics system to manage resources, stock, orders, shipments, and movements with end-to-end traceability.

---

## Highlights

- Stock + audit trail: every change records a Movement (`ENTRY` / `EXIT`)
- Orders + items workflow (create, edit, inline item management)
- Shipments lifecycle including fulfillment on delivery
- Detail pages that link Orders/Shipments to related Movements (traceability)
- Operational dashboard (`/ui`) with KPIs, charts (Chart.js), recent activity, and proactive alerts
- First-run setup to create the initial admin user (no manual DB seeding)
- Admin-only user management in the UI (roles, password reset, enable/disable)

---

<details>
<summary>Resumen en español (🇪🇸)</summary>

MOLS es un proyecto de portfolio: un sistema web para gestionar stock, pedidos, envíos y auditoría de movimientos, con trazabilidad completa.

Para detalles técnicos y arquitectura, consulta [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md).
</details>

---

## Tech Stack

- Java 21, Spring Boot 4 (Spring MVC)
- PostgreSQL + Spring Data JPA (Hibernate)
- Spring Security (JWT for API + session login for UI)
- Thymeleaf + Bootstrap 5.3
- OpenAPI/Swagger (springdoc)
- Docker + Docker Compose

---

## Links

With the application running:

- UI dashboard: http://localhost:8080/ui
- First-run setup: http://localhost:8080/ui/setup
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## Dashboard (UI)

The dashboard at `/ui` is designed to work with an empty database (friendly empty states) and improve naturally as historical data grows.

Includes:

- KPI cards: total orders (with pending), stock quantity across warehouses, active shipments, low-stock alerts, recent movements (24h), fulfillment rate
- Charts: stock distribution by warehouse (bar), movements by type (doughnut), orders by status (pie)
- Alerts: low stock items (action link to adjust), stale pending orders (link to order detail)

Configuration (no hardcoded thresholds): `src/main/resources/application.properties`

- `mols.dashboard.low-stock-threshold` (default: 10)
- `mols.dashboard.critical-stock-threshold` (default: 5)
- `mols.dashboard.stale-order-days` (default: 3)
- `mols.dashboard.recent-activity-hours` (default: 24)
- `mols.dashboard.movement-chart-days` (default: 30)
- `mols.dashboard.fulfillment-target-percent` (default: 90)

---

## Docs

- Technical details: [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md)
- Local run / troubleshooting: [HELP.md](HELP.md)

---

## Run (quick)

```powershell
./mvnw.cmd spring-boot:run
```

Or with Docker:

```powershell
docker compose up --build -d
```

---

## License

This project is for educational and portfolio purposes.

---

## Contributing

Contributions are welcome. Focus on clearer workflows, better validation, and stronger testing.
