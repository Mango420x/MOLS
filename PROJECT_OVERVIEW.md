# MOLS - Project Overview
**Purpose:** Internal web-based application to manage multimodal logistics operations (units, stock, orders, vehicles, shipments) in a defense/industrial style.  

---

## Domain Entities

- **Unit**: Branch or operational unit requesting resources  
- **Warehouse**: Physical storage location for resources  
- **Resource**: Material, equipment, parts, electronics  
- **Stock**: Quantity of a resource in a warehouse  
- **Order**: Resource request by a unit  
- **OrderItem**: Individual resource entry in an order  
- **Vehicle**: Transport medium (TERRESTRIAL, MARITIME, AERIAL)  
- **Shipment**: Transport of resources assigned to a vehicle  
- **Movement**: Audit trail of stock changes  

---

## Business Rules

- Orders cannot exceed available stock  
- Vehicles must be available and compatible with transport type  
- Stock cannot be negative  
- Every stock change generates a Movement entry  
- Orders complete only when all resources are delivered  

---

## System Flow

Browser (UI: HTML/CSS/JS)  
↓  
Spring Boot REST API  
↓  
Service Layer (business logic)  
↓  
Domain Entities & Repositories (JPA/Hibernate)  
↓  
PostgreSQL Database  

Functional UI consumes API endpoints for units, orders, stock, vehicles, shipments. Full audit trails and validations are included.

---

## Tech Stack

- Backend: Java 17+, Spring Boot, Spring Data JPA, Hibernate  
- Database: PostgreSQL (H2 for testing)  
- Frontend: HTML/CSS/JS, Fetch API  
- Testing: JUnit 5  
- Build: Maven  
- Version Control: Git  

---

## MVP Scope

- CRUD for Units, Warehouses, Resources, Vehicles  
- Create and track Orders and Shipments  
- Stock movements and audit trails  
- Simple vehicle assignment rules  
- Minimal functional frontend  

---

## Expansion Notes

- Offline support and network sync (future)  
- Transport optimization (future)  
- Advanced reporting dashboards (future)  
- Multi-branch collaboration (future)  

---

**Last Updated:** 12-02-2026  
