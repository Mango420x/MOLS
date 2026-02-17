# MOLS – Multimodal Operative Logistic System

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

---

## Getting Started

1. Clone the repository.
2. Configure the database in `application.properties`.
3. Run the application and open http://localhost:8080.

---

## License

This project is for educational and portfolio purposes.

---

## Contributing

Contributions are welcome. Focus on clearer workflows, better validation, and stronger testing.
