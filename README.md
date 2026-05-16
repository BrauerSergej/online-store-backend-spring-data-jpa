# Customer Service & Cart Logic Implementation / Implementierung von Customer Service & Warenkorb-Logik

This repository contains a Spring Boot backend implementation for managing customers, products, and shopping carts, utilizing MapStruct for clean data transfer object (DTO) mapping.
Dieses Repository enthält eine Spring Boot-Backend-Implementierung zur Verwaltung von Kunden, Produkten und Warenkörben unter Verwendung von MapStruct für ein sauberes DTO-Mapping (Data Transfer Object).

---

## 🇺🇸 English Version

### 🛠️ Tech Stack
* **Java 17**
* **Spring Boot (Data JPA, Web)**
* **MapStruct** — compilation-time code generation for Entity-to-DTO conversion
* **Lombok** — boilerplate reduction
* **Maven** — project build tool

### 🧩 Architecture & Project Structure
The project follows a solid Layered Architecture approach:
1. **Entity Layer (Domain):** Standard JPA entities representing the database schema (`Customer`, `Product`, `Position`, `Cart`).
2. **DTO Layer (Data Transfer Object):** Decoupled data structures for safe API contract fulfillment (`ProductDto`, `PositionDto`, `CustomerDto`).
3. **Mapping Layer:** Abstract interfaces where MapStruct automatically generates the underlying active implementations during the compilation phase.
4. **Repository Layer:** Spring Data JPA interfaces for database abstractions and operations.
5. **Service Layer:** Core business logic components (`CustomerServiceImpl`, `ProductServiceImpl`).

### 💡 MapStruct Technical Insights & `mvn clean compile`
**MapStruct** is a compile-time annotation processor. Unlike reflection-based mappers, it generates plain, fast Java code at compile time. When you write an abstract interface like `PositionMapper`, you only declare what needs to be mapped.

When executing the terminal command:
```bash
mvn clean compile
