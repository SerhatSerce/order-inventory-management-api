# Order & Inventory Management API

A production-oriented REST API built with Java, Spring Boot, PostgreSQL, and Spring Data JPA.

This project simulates a real-world order and inventory management system. It includes product management, customer management, order creation, stock control, payment simulation, order lifecycle management, validation, global exception handling, pagination, filtering, logging, and Swagger/OpenAPI documentation.

---

## Project Purpose

The purpose of this project is to demonstrate backend development skills using clean layered architecture and real business rules.

Unlike a simple CRUD application, this API includes transactional business logic such as:

- Decreasing product stock when an order is created
- Restoring stock when an order is cancelled
- Preventing orders when stock is insufficient
- Changing order status after successful payment
- Managing order lifecycle transitions
- Blocking invalid status changes
- Returning meaningful error responses
- Documenting endpoints with Swagger/OpenAPI

---

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Lombok
- Bean Validation
- Swagger / OpenAPI
- SLF4J Logging

---

## Requirements

Before running the project locally, make sure you have:

- Java 17
- Maven or Maven Wrapper
- PostgreSQL
- IntelliJ IDEA or any Java IDE

---

## Main Features

### Product Management

- Create product
- List products
- Get product by ID
- Update product
- Update product stock
- Soft delete product
- Optimistic locking support with `@Version`

### Customer Management

- Create customer
- List customers
- Get customer by ID
- Email uniqueness control
- Validation for invalid email format

### Order Management

- Create order
- List orders with pagination
- Filter orders by status
- Filter orders by customer ID
- Get order by ID
- Cancel order
- Ship order
- Deliver order

### Inventory Management

- Stock decreases automatically when an order is created
- Stock is restored automatically when an order is cancelled
- Orders cannot be created if stock is insufficient

### Payment Management

- Simulated payment process
- Successful payment changes order status from `PENDING` to `PAID`
- Failed payment returns meaningful error response
- Already paid orders cannot be paid again
- Payment reference number generation

---

## Order Lifecycle

Supported order status flow:

```text
PENDING -> PAID -> SHIPPED -> DELIVERED
```

Cancellation rules:

```text
PENDING -> CANCELLED
PAID -> CANCELLED
```

Invalid transitions are blocked.

Examples:

```text
PENDING -> SHIPPED      Not allowed
PAID -> DELIVERED       Not allowed
DELIVERED -> CANCELLED  Not allowed
CANCELLED -> PAID       Not allowed
```

---

## Architecture

The project follows a layered architecture:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Entity
   ↓
Database
```

### Package Structure

```text
com.serhat.orderinventory
├── config
├── controller
├── dto
│   ├── customer
│   ├── order
│   ├── payment
│   └── product
├── entity
├── enums
├── exception
├── repository
└── service
```

---

## Database Entities

### Product

Represents a product in inventory.

Main fields:

- `id`
- `name`
- `description`
- `price`
- `stockQuantity`
- `version`
- `createdAt`
- `updatedAt`
- `deleted`

### Customer

Represents a customer.

Main fields:

- `id`
- `fullName`
- `email`
- `phone`
- `createdAt`
- `updatedAt`
- `deleted`

### Order

Represents a customer order.

Main fields:

- `id`
- `customer`
- `status`
- `totalAmount`
- `items`
- `payment`
- `createdAt`
- `updatedAt`
- `deleted`

### OrderItem

Represents a product line inside an order.

Main fields:

- `id`
- `order`
- `product`
- `quantity`
- `unitPrice`
- `lineTotal`

### Payment

Represents payment information for an order.

Main fields:

- `id`
- `order`
- `amount`
- `method`
- `status`
- `referenceNumber`
- `paidAt`
- `failureReason`

---

## API Endpoints

### Product Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/products` | Create product |
| GET | `/products` | Get all products |
| GET | `/products/{id}` | Get product by ID |
| PUT | `/products/{id}` | Update product |
| PATCH | `/products/{id}/stock` | Update product stock |
| DELETE | `/products/{id}` | Soft delete product |

### Customer Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/customers` | Create customer |
| GET | `/customers` | Get all customers |
| GET | `/customers/{id}` | Get customer by ID |

### Order Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/orders` | Create order |
| GET | `/orders` | Get orders with pagination and filtering |
| GET | `/orders/{id}` | Get order by ID |
| POST | `/orders/{id}/cancel` | Cancel order |
| POST | `/orders/{id}/ship` | Ship order |
| POST | `/orders/{id}/deliver` | Deliver order |

Order filtering examples:

```http
GET /orders?page=0&size=10
GET /orders?status=PENDING&page=0&size=10
GET /orders?customerId=1&page=0&size=10
GET /orders?status=DELIVERED&customerId=1&page=0&size=10
```

### Payment Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/payments` | Create payment |
| GET | `/payments` | Get all payments |
| GET | `/payments/{id}` | Get payment by ID |

---

## Swagger Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/v3/api-docs
```

Swagger provides interactive API documentation and allows testing endpoints directly from the browser.

---

## Example Requests

### Create Product

```http
POST /products
Content-Type: application/json
```

```json
{
  "name": "Keyboard",
  "description": "Mechanical keyboard",
  "price": 1500.00,
  "stockQuantity": 20
}
```

Example response:

```json
{
  "id": 1,
  "name": "Keyboard",
  "description": "Mechanical keyboard",
  "price": 1500.00,
  "stockQuantity": 20,
  "version": 0,
  "createdAt": "2026-04-30T17:19:58.9217083",
  "updatedAt": "2026-04-30T17:19:58.9217083"
}
```

### Create Customer

```http
POST /customers
Content-Type: application/json
```

```json
{
  "fullName": "Serhat Yilmaz",
  "email": "serhat@example.com",
  "phone": "05551234567"
}
```

Example response:

```json
{
  "id": 1,
  "fullName": "Serhat Yilmaz",
  "email": "serhat@example.com",
  "phone": "05551234567",
  "createdAt": "2026-04-30T17:20:00",
  "updatedAt": "2026-04-30T17:20:00"
}
```

### Create Order

```http
POST /orders
Content-Type: application/json
```

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

Example response:

```json
{
  "id": 1,
  "customerId": 1,
  "customerName": "Serhat Yilmaz",
  "status": "PENDING",
  "totalAmount": 3000.00,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Keyboard",
      "quantity": 2,
      "unitPrice": 1500.00,
      "lineTotal": 3000.00
    }
  ],
  "createdAt": "2026-04-30T17:25:00",
  "updatedAt": "2026-04-30T17:25:00"
}
```

When this order is created, product stock decreases automatically:

```text
Initial stock: 20
Ordered quantity: 2
Remaining stock: 18
```

### Create Payment

```http
POST /payments
Content-Type: application/json
```

```json
{
  "orderId": 1,
  "method": "CREDIT_CARD",
  "successful": true
}
```

Example response:

```json
{
  "id": 1,
  "orderId": 1,
  "amount": 3000.00,
  "method": "CREDIT_CARD",
  "status": "SUCCESS",
  "referenceNumber": "PAY-550e8400-e29b-41d4-a716-446655440000",
  "paidAt": "2026-04-30T17:30:00",
  "failureReason": null,
  "createdAt": "2026-04-30T17:30:00",
  "updatedAt": "2026-04-30T17:30:00"
}
```

After a successful payment, order status changes:

```text
PENDING -> PAID
```

---

## Error Handling

The project uses global exception handling with meaningful HTTP responses.

### Product Not Found

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999"
}
```

### Customer Not Found

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999"
}
```

### Order Not Found

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 999"
}
```

### Insufficient Stock

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock for product: Keyboard. Requested: 999, available: 18"
}
```

### Duplicate Email

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: serhat@example.com"
}
```

### Invalid Order Status Transition

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Order cannot be cancelled from status: DELIVERED"
}
```

### Payment Already Completed

```json
{
  "timestamp": "2026-04-30T17:35:00",
  "status": 409,
  "error": "Conflict",
  "message": "Payment has already been completed for order id: 1"
}
```

---

## Validation

The project uses Bean Validation to validate incoming request bodies.

Examples:

- Product name cannot be blank
- Product price must be greater than 0
- Stock quantity cannot be negative
- Customer email format must be valid
- Order items cannot be empty
- Order quantity must be at least 1
- Payment method cannot be null

Example invalid email response:

```json
{
  "email": "Email format is invalid"
}
```

---

## Transaction Management

The project uses `@Transactional` for business-critical operations.

Main transactional operations:

- Creating an order
- Decreasing stock during order creation
- Cancelling an order
- Restoring stock during cancellation
- Creating payment
- Updating order status after payment

This ensures that database operations remain consistent.

Example:

If an order cannot be created because of insufficient stock:

```text
Order is not created.
Stock is not changed.
Database remains consistent.
```

---

## Logging

Important business events are logged with SLF4J.

Examples:

```text
Product created. productId=1, name=Keyboard, stockQuantity=20
Stock decreased for order creation. productId=1, oldStock=20, quantity=2, newStock=18
Order created. orderId=1, customerId=1, status=PENDING, totalAmount=3000.00
Payment completed successfully. paymentId=1, orderId=1, amount=3000.00, method=CREDIT_CARD
Order shipped. orderId=1, status=SHIPPED
Order delivered. orderId=1, status=DELIVERED
```

---

## Pagination and Filtering

Orders can be listed with pagination and optional filters.

```http
GET /orders?page=0&size=10
GET /orders?status=PENDING&page=0&size=10
GET /orders?customerId=1&page=0&size=10
GET /orders?status=DELIVERED&customerId=1&page=0&size=10
```

Example paginated response structure:

```json
{
  "content": [
    {
      "id": 1,
      "customerId": 1,
      "customerName": "Serhat Yilmaz",
      "status": "DELIVERED",
      "totalAmount": 3000.00,
      "items": []
    }
  ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

## Database Configuration

Example `application.properties` configuration:

```properties
spring.application.name=order-inventory-management-api

server.port=${SERVER_PORT:8080}

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/order_inventory_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres123}

spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
spring.jpa.show-sql=${SHOW_SQL:true}
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

---

## How to Run Locally

### 1. Clone the Repository

```bash
git clone https://github.com/serhatserce/order-inventory-management-api.git
cd order-inventory-management-api
```

### 2. Create PostgreSQL Database

Create a PostgreSQL database named:

```text
order_inventory_db
```

Example SQL:

```sql
CREATE DATABASE order_inventory_db;
```

If PostgreSQL template encoding causes an error, use:

```sql
CREATE DATABASE order_inventory_db
WITH
    TEMPLATE = template0
    ENCODING = 'UTF8';
```

### 3. Configure Database Credentials

Open:

```text
src/main/resources/application.properties
```

The project uses environment variables with default local values:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/order_inventory_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres123}
```

By default, the application connects to:

```text
Database URL: jdbc:postgresql://localhost:5432/order_inventory_db
Username: postgres
Password: postgres123
```

You can keep these default values for local development.

If you want to override them, set environment variables such as:

```properties
DB_URL=jdbc:postgresql://localhost:5432/order_inventory_db
DB_USERNAME=postgres
DB_PASSWORD=your_postgresql_password
```

### 4. Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Or run the main class from IntelliJ IDEA:

```text
OrderInventoryManagementApiApplication
```

### 5. Open Swagger

```text
http://localhost:8080/swagger-ui.html
```

---

## Business Rules

- Product stock cannot be negative.
- Order cannot be created if stock is insufficient.
- Product stock decreases when an order is created.
- Product stock is restored when an order is cancelled.
- Only `PENDING` orders can be paid.
- Successful payment changes order status to `PAID`.
- Already paid orders cannot be paid again.
- Only `PAID` orders can be shipped.
- Only `SHIPPED` orders can be delivered.
- Delivered orders cannot be cancelled.
- Cancelled orders cannot be paid, shipped, or delivered.
- Customer email must be unique.
- Deleted products are hidden using soft delete.

---

## Project Highlights

This project demonstrates practical backend development concepts such as layered architecture, DTO-based API design, PostgreSQL integration, transaction management, stock consistency, order lifecycle management, payment simulation, global exception handling, pagination, filtering, Swagger documentation, logging, soft delete, and optimistic locking.

---

## Author

Developed by Serhat Serçe.

---

## License

This project is developed for educational and portfolio purposes.