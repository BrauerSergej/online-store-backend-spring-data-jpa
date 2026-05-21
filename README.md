# Online Store Backend (Spring Data JPA & AOP Logging) 🛒

Educational backend project for an online store, developed with Spring Boot, Spring Data JPA, and Aspect-Oriented Programming (AOP).
Ein cloudbasiertes Lernprojekt für ein Online-Shop-Backend, entwickelt mit Spring Boot, Spring Data JPA und AOP-Logging.

---

## 🇺🇸 English Version

### 📦 Core Features
* **Product Management:** Save, update, delete, and retrieve products using robust business flow.
* **Customer Management:** Handle customer profiles, multi-state activation, and secure business logs.
* **Cart Logic:** Modernized cart flow utilizing dedicated Data Transfer Objects (DTOs), position adjustments, cost evaluation, and full cleanup operations.

### 🛠️ Tech Stack & Architecture
* **Java 17+** | **Spring Boot (Data JPA, Web, AOP)** | **PostgreSQL / H2**
* **MapStruct** — Compilation-time mapping layer for safe Entity-to-DTO conversion.
* **Lombok** & **Maven**

The system architecture is structured to ensure strict Separation of Concerns (SoC):
1. **Controller Layer:** Exposes pure REST endpoints using standardized JSON bodies.
2. **Service Layer (`Impl`):** Built around Clean Code guidelines. Methods utilize *Guard Clauses* to fail early, eliminate deeply nested `if-else` blocks, and delegate core domain transformations straight to entities.
3. **DTO Layer:** Decoupled data payload containers, including the precise `PositionUpdateDto` for handling unified cart data transfer.
4. **Exception Handling:** Custom specialized runtime exceptions (`CustomerNotFoundException`, `ProductNotFoundException`, `CartEmptyException`) to isolate business logic failures from technical environment flaws.

### ⚡ Aspect-Oriented Programming (AOP) Configuration
Technical logging infrastructure is fully decoupled via Spring AOP. The `AspectLogging` component dynamically intercepts service operations.

#### Comprehensive Package Coverage
The pointcut expression uses the double-dot `..` wildcard syntax:
```java
@Pointcut("execution(* de.ait.g_67_shop.service..*.*(..))")