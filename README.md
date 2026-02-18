# MOLS — Multimodal Operative Logistics System

![CI](https://github.com/Mango420x/MOLS/actions/workflows/ci.yml/badge.svg)

Portfolio project: a web-based logistics system to manage resources, stock, orders, shipments, and movements with end-to-end traceability.

---

## Highlights

- Stock + audit trail: every change records a Movement (`ENTRY` / `EXIT`)
- Orders + items workflow (create, edit, inline item management)
- Shipments lifecycle including fulfillment on delivery
- Detail pages that link Orders/Shipments to related Movements (traceability)

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

- UI: http://localhost:8080/ui
- Swagger UI: http://localhost:8080/swagger-ui.html

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
