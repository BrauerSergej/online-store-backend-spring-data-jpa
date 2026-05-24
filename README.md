# Online Store Backend API

Educational backend project for an online store built with **Java** and **Spring Boot**.

The project implements product management, customer management, shopping cart logic, validation, custom exceptions, centralized REST error handling, database migrations with Liquibase, and business logging.

---

## Overview

This application is a REST API for a small online store.

It supports:

- managing products;
- managing customers;
- creating a cart automatically for every new customer;
- adding products to a customer's cart;
- increasing product quantity if the product already exists in the cart;
- removing selected quantities from the cart;
- clearing the whole cart;
- calculating cart total cost;
- calculating average product price in a cart;
- validating business data;
- handling errors globally with meaningful HTTP status codes.

---

## Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL**
- **Liquibase**
- **Swagger / OpenAPI**
- **SLF4J Logging**
- **Maven**
- **Bean Validation**

---

## Domain Model

The main domain structure is:

```text
Customer
 └── Cart
      └── Position
           └── Product
```

### Entities

| Entity | Description |
|---|---|
| `Customer` | Represents a shop customer |
| `Cart` | Represents a customer's shopping cart |
| `Position` | Represents a cart item: product + quantity |
| `Product` | Represents a product in the shop |

### Why `Position` is needed

A cart does not store products directly.  
It stores positions, because each product in the cart also needs a quantity.

Example:

```text
Cart
 ├── Position: Bread, quantity 2
 └── Position: Milk, quantity 3
```

---

## Features

### Product Features

- Create a new product.
- Get all active products.
- Get one active product by ID.
- Update product price.
- Soft delete product by setting `active = false`.
- Restore previously deactivated product.
- Count active products.
- Calculate total price of all active products.
- Calculate average price of active products.

### Customer Features

- Create a new customer.
- Automatically create an empty cart for every new customer.
- Get all active customers.
- Get one active customer by ID.
- Update customer name.
- Soft delete customer by setting `active = false`.
- Restore previously deactivated customer.
- Count active customers.

### Cart Features

- Add a product to a customer's cart.
- Increase quantity if the product already exists in the cart.
- Remove a selected quantity of a product from the cart.
- Completely remove a cart position when quantity becomes zero or lower.
- Clear all positions from a customer's cart.
- Calculate total cart cost.
- Calculate average product price in a customer's cart.

### Validation and Exception Handling

- Validate `Customer.name`.
- Validate `Position.quantity`.
- Handle invalid cart operations with `EntityUpdateException`.
- Handle missing entities with `EntityNotFoundException`.
- Handle validation errors globally.
- Return correct HTTP statuses: `400`, `404`, `500`.
- Log business errors and unexpected errors with appropriate log levels.

---

## Database Structure

The database is managed with Liquibase.

Main tables:

```text
customer
cart
product
position
```

### Example database representation

#### customer

| id | name | active |
|---:|---|---|
| 1 | Lars Chekoski | true |

#### cart

| id | customer_id |
|---:|---:|
| 1 | 1 |

#### product

| id | title | price | active |
|---:|---|---:|---|
| 2 | Bread | 1.49 | true |

#### position

| id | cart_id | product_id | quantity |
|---:|---:|---:|---:|
| 1 | 1 | 2 | 2 |

Meaning:

```text
In cart with ID 1, there is product with ID 2 in quantity 2.
```

---

## Validation

### Customer Name Validation

The `name` field is validated with Bean Validation annotations.

```java
@NotBlank(message = "Customer name cannot be empty")
@Pattern(
        regexp = "[A-Z][A-Za-z ]{2,49}",
        message = "Customer name should be at least 3 characters and start with capital letter"
)
@Column(name = "name", nullable = false, length = 50)
private String name;
```

Valid examples:

```text
John
John Doe
Anna Maria
```

Invalid examples:

```text
""
"   "
"jo"
"john"
```

### Position Quantity Validation

The `quantity` field must be greater than zero.

```java
@Positive(message = "The quantity must be greater than zero")
@Column(name = "quantity", nullable = false)
private int quantity;
```

Valid values:

```text
1
2
10
```

Invalid values:

```text
0
-1
-5
```

---

## Custom Exceptions

### EntityNotFoundException

Used when an entity cannot be found in the database.

Example:

```java
return repository.findByIdAndActiveTrue(id).orElseThrow(
        () -> new EntityNotFoundException(Customer.class, id)
);
```

Typical situations:

```text
Customer not found
Product not found
```

HTTP status:

```text
404 NOT_FOUND
```

---

### EntityUpdateException

Used when an entity exists, but the requested operation is invalid.

Example:

```java
if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
    throw new EntityUpdateException("Quantity must be positive");
}
```

Typical situations:

```text
quantity is null
quantity is 0
quantity is negative
product is not in the customer's cart
customer cart does not exist
operation cannot be completed because of invalid business data
```

HTTP status:

```text
400 BAD_REQUEST
```

---

## Global Exception Handling

All exceptions are handled centrally in `GlobalExceptionHandler`.

### HTTP Status Strategy

| Situation | Exception | HTTP Status |
|---|---|---|
| Customer or Product not found | `EntityNotFoundException` | `404_NOT_FOUND` |
| Invalid business operation | `EntityUpdateException` | `400_BAD_REQUEST` |
| Validation error | `ConstraintViolationException` | `400_BAD_REQUEST` |
| Unexpected server error | `NullPointerException` | `500_INTERNAL_SERVER_ERROR` |

### Logging Strategy

| Situation | Log Level | Reason |
|---|---|---|
| Entity not found | `WARN` | Client requested a missing resource |
| Invalid business operation | `WARN` | Client sent invalid business data |
| Validation error | `WARN` | Client sent invalid input |
| Unexpected server error | `ERROR` | Server-side problem |

---

## REST API Endpoints

### Products

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/products` | Create product |
| `GET` | `/products` | Get all active products |
| `GET` | `/products/{id}` | Get active product by ID |
| `PUT` | `/products/{id}` | Update product price |
| `DELETE` | `/products/{id}` | Soft delete product |
| `PUT` | `/products/{id}/restore` | Restore product |
| `GET` | `/products/count` | Count active products |
| `GET` | `/products/total-cost` | Get total cost of active products |
| `GET` | `/products/average-price` | Get average price of active products |

### Customers

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/customers` | Create customer |
| `GET` | `/customers` | Get all active customers |
| `GET` | `/customers/{id}` | Get active customer by ID |
| `PUT` | `/customers/{id}` | Update customer name |
| `DELETE` | `/customers/{id}` | Soft delete customer |
| `PUT` | `/customers/{id}/restore` | Restore customer |
| `GET` | `/customers/count` | Count active customers |

### Cart

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/customers/{customerId}/cart/items/{productId}` | Add product to cart |
| `DELETE` | `/customers/{customerId}/cart/items/{productId}` | Remove selected quantity from cart |
| `DELETE` | `/customers/{id}/cart` | Clear customer cart |
| `GET` | `/customers/{id}/cart/total-cost` | Get total cart cost |
| `GET` | `/customers/{id}/cart/average-price` | Get average product price in cart |

---

## Request Examples

### Create Product

```http
POST /products
Content-Type: application/json
```

```json
{
  "title": "Milk",
  "price": 2.99
}
```

---

### Create Customer

```http
POST /customers
Content-Type: application/json
```

```json
{
  "name": "John Doe"
}
```

When a customer is created, an empty cart is created automatically.

---

### Add Product to Cart

```http
POST /customers/1/cart/items/2
Content-Type: application/json
```

```json
{
  "quantity": 2
}
```

Meaning:

```text
Add product with ID 2 to the cart of customer with ID 1 in quantity 2.
```

If the product already exists in the cart, the quantity is increased.

---

### Remove Product from Cart

```http
DELETE /customers/1/cart/items/2
Content-Type: application/json
```

```json
{
  "quantity": 1
}
```

If the new quantity is greater than zero, the position remains in the cart with a reduced quantity.

If the new quantity is zero or lower, the position is removed completely.

---

### Clear Customer Cart

```http
DELETE /customers/1/cart
```

This removes all positions from the customer's cart.

---

## Example JSON Response

```json
{
  "id": 1,
  "name": "Lars Chekoski",
  "cart": {
    "positions": [
      {
        "id": 1,
        "quantity": 2,
        "product": {
          "id": 2,
          "title": "Bread",
          "price": 1.49
        }
      }
    ]
  }
}
```

Meaning:

```text
Customer Lars Chekoski has one cart position:
Bread, quantity 2.
```

---

## Example Error Responses

### Invalid Quantity

Request:

```http
POST /customers/1/cart/items/2
Content-Type: application/json
```

```json
{
  "quantity": 0
}
```

Response:

```text
400 BAD_REQUEST
Quantity must be positive
```

---

### Product Is Not in Customer's Cart

Request:

```http
DELETE /customers/1/cart/items/5
Content-Type: application/json
```

```json
{
  "quantity": 1
}
```

Response:

```text
400 BAD_REQUEST
Product is not in customer's cart
```

---

### Customer Not Found

Request:

```http
GET /customers/999
```

Response:

```text
404 NOT_FOUND
Customer with ID 999 not found
```

---

## Configuration

The application uses PostgreSQL.  
Database connection values are configured through environment variables.

Example `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Required environment variables:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=g_67_shop
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

Application port:

```text
8081
```

---

## Liquibase

Liquibase is used for database schema migration.

Main changelog file:

```text
src/main/resources/db/changelog/db.changelog-master.xml
```

Liquibase creates and manages the database schema for:

```text
customer
cart
product
position
```

---

## Swagger

Swagger UI is available after application startup:

```text
http://localhost:8081/swagger-ui/index.html
```

It can be used to test the REST API directly from the browser.

---

## How to Run

1. Create a PostgreSQL database.
2. Configure the required environment variables.
3. Start the Spring Boot application from IntelliJ IDEA or terminal.
4. Open Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

5. Create products and customers.
6. Add products to a customer's cart.
7. Test validation and exception handling.

---

## Project Status

The project is an educational backend application focused on:

```text
Spring Boot REST API
Spring Data JPA
PostgreSQL
Liquibase
DTO mapping
Bean Validation
Custom exceptions
Global exception handling
Business logging
Cart business logic
```
