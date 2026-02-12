# MOLS – Multimodal Operative Logistic System

**Short Description:**  
Auditable web-based system for managing logistics across land, air, and sea, with complete stock and transport tracking. Designed for robustness, traceability, and real-world operational workflows.

---

## Overview

MOLS is a web application that allows units (branches/sucursals) to request resources, manage warehouse stock, assign vehicles for transportation, and track shipments with full historical traceability.

Supports multiple types of vehicles: TERRESTRIAL, MARITIME, and AERIAL.  
Focuses on functional, industrial-grade workflows rather than flashy UI.

---

## Key Features

- **Unit & Order Management:** Create, validate, and track resource orders.  
- **Warehouse & Stock Management:** Track stock levels, prevent negative inventory, and log movements.  
- **Vehicle & Shipment Tracking:** Assign vehicles, track shipment status, and handle multimodal logistics.  
- **Audit & History:** Complete traceability for all actions.  

---

## Technologies

- **Backend:** Java 17+, Spring Boot (REST API, Service Layer, Validation)  
- **Database:** PostgreSQL (H2 for testing)  
- **Frontend:** HTML, CSS, JavaScript (Fetch API)  
- **Build & Tools:** Maven, Git, JUnit 5  

---

## Architecture

Browser (UI: HTML/CSS/JS) -> Spring Boot REST API -> Service Layer (business logic) -> Domain Entities & Repositories (JPA/Hibernate) -> PostgreSQL Database

- Functional UI consumes API endpoints for managing units, orders, stock, vehicles, and shipments.  
- Full audit trails and validations ensure a robust, industrial-grade system.

---

## Getting Started

1. Clone the repository

       git clone https://github.com/Mango420x/MOLS.git

2. Configure PostgreSQL in application.properties.

3. Build and run the Spring Boot application

        mvn clean install
        mvn spring-boot:run

4. Access the application opening your browser at http://localhost:8080

## License

This project is for educational and portfolio purposes.

## Contributing

Contributions welcome! Focus on backend robustness, frontend improvements, or additional logistic rules. Ensure all movements are audited and validated.
