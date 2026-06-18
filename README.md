<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" />
  <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Keycloak-4D4D4D?style=for-the-badge&logo=keycloak&logoColor=white" />
  <img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white" />
</p>

# 🛒 E-Commerce Microservices Platform

A **production-grade**, event-driven e-commerce backend built with **Spring Boot 3.5** and **Java 21**, demonstrating real-world microservices architecture patterns including API Gateway routing, OAuth2 security, asynchronous event streaming with Kafka, circuit breakers, distributed tracing, and a full observability stack.

---

## 📑 Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Tech Stack](#-tech-stack)
- [Microservices](#-microservices)
  - [API Gateway](#1-api-gateway-port-8083)
  - [Product Service](#2-product-service-port-8080)
  - [Order Service](#3-order-service-port-8081)
  - [Inventory Service](#4-inventory-service-port-8082)
  - [Notification Service](#5-notification-service-port-8090)
- [Event-Driven Architecture](#-event-driven-architecture)
- [Security](#-security)
- [Resilience Patterns](#-resilience-patterns)
- [Observability Stack](#-observability-stack)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Contributing](#-contributing)

---

## 🏗 Architecture Overview

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT REQUEST                                  │
└──────────────────────────────┬───────────────────────────────────────────────┘
                               │
                               ▼
                 ┌─────────────────────────────┐
                 │      🔐 API GATEWAY         │
                 │     (Spring Cloud MVC)       │
                 │   OAuth2 + Keycloak Auth     │
                 │  Circuit Breaker (Res4j)     │
                 │       Port: 8083             │
                 └──┬──────────┬──────────┬─────┘
                    │          │          │
          ┌─────────┘          │          └──────────┐
          ▼                    ▼                     ▼
┌─────────────────┐  ┌─────────────────┐  ┌──────────────────┐
│ 📦 PRODUCT SVC  │  │ 🛍️ ORDER SVC   │  │ 📊 INVENTORY SVC │
│   Port: 8080    │  │   Port: 8081    │  │    Port: 8082    │
│   MongoDB       │  │   MySQL         │  │    MySQL         │
└─────────────────┘  └───────┬─────────┘  └──────────────────┘
                             │ REST call ▲
                             │           │ (stock check)
                             │           └──────────────────────
                             │
                             │ Kafka (Avro)
                             ▼
                 ┌─────────────────────────────┐
                 │    📧 NOTIFICATION SVC      │
                 │       Port: 8090            │
                 │   Kafka Consumer + SMTP     │
                 └─────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────────┐
│                        🔭 OBSERVABILITY STACK                                │
│  Prometheus (Metrics) │ Grafana (Dashboards) │ Tempo (Traces) │ Loki (Logs) │
│      :9090            │      :3000           │    :3110       │   :3100     │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.2, Spring Cloud 2025.0.0 |
| **API Gateway** | Spring Cloud Gateway MVC |
| **Security** | OAuth2 Resource Server, Keycloak 24.0.1 |
| **Databases** | MongoDB 7.0 (Products), MySQL 8.x (Orders, Inventory) |
| **Messaging** | Apache Kafka (Confluent 7.5), Avro serialization, Schema Registry |
| **Resilience** | Resilience4j (Circuit Breaker, Retry, Time Limiter) |
| **Inter-Service Comm.** | Spring RestClient with `HttpServiceProxyFactory` |
| **Observability** | Micrometer, Prometheus, Grafana, Grafana Tempo, Grafana Loki |
| **Distributed Tracing** | Micrometer Tracing (Brave) → Zipkin / Tempo |
| **API Docs** | SpringDoc OpenAPI 2.8.9 (Swagger UI) |
| **Containerization** | Docker & Docker Compose |
| **Email** | Spring Boot Mail (Mailtrap SMTP) |
| **Build Tool** | Maven (Multi-module) |
| **Testing** | JUnit 5, Testcontainers |

---

## 🧩 Microservices

### 1. API Gateway (Port `8083`)

The single entry point for all client requests. Routes traffic to downstream services, enforces authentication, and provides resilience.

**Key Responsibilities:**
- **Request Routing** — Routes `/api/product`, `/api/order`, and `/api/inventory` to their respective services
- **Authentication** — Validates JWT tokens issued by Keycloak (OAuth2 Resource Server)
- **Circuit Breaking** — Wraps each route with a Resilience4j circuit breaker; returns `503 Service Unavailable` on fallback
- **Aggregated Swagger UI** — Exposes a unified Swagger UI at `/swagger-ui.html` that aggregates API docs from all services
- **CORS** — Configured to allow cross-origin requests

**Key Files:**
```
api-gateway/
├── config/
│   ├── SecurityConfig.java       # OAuth2 + CORS configuration
│   └── ObservationConfig.java    # Micrometer observation setup
└── routes/
    └── Routes.java               # Gateway route definitions with circuit breakers
```

---

### 2. Product Service (Port `8080`)

Manages the product catalog with full CRUD operations, backed by **MongoDB**.

**Key Responsibilities:**
- Create new products with name, description, SKU code, and price
- Retrieve all products from the catalog
- Exposes OpenAPI documentation

**Data Model — `Product`:**
| Field | Type | Description |
|---|---|---|
| `id` | `String` | MongoDB auto-generated ID |
| `name` | `String` | Product name |
| `description` | `String` | Product description |
| `skuCode` | `String` | Stock Keeping Unit code |
| `price` | `BigDecimal` | Product price |

**Key Files:**
```
product-service/
├── controller/ProductController.java    # REST endpoints
├── service/ProductService.java          # Business logic
├── model/Product.java                   # MongoDB document
├── dto/
│   ├── ProductRequest.java              # Request DTO (Java Record)
│   └── ProductResponse.java             # Response DTO (Java Record)
├── repository/ProductRepository.java    # Spring Data MongoDB
└── config/
    ├── OpenAPIConfig.java               # Swagger/OpenAPI metadata
    └── ObservationConfig.java           # Distributed tracing
```

---

### 3. Order Service (Port `8081`)

Handles order placement with inventory validation and asynchronous event publishing.

**Key Responsibilities:**
- Validates product stock availability by calling the Inventory Service (via `RestClient` + `HttpServiceProxyFactory`)
- Persists orders to **MySQL** upon successful validation
- Publishes `OrderPlacedEvent` to Kafka topic `order-placed` using **Avro** serialization
- Implements **Circuit Breaker**, **Retry**, and **Time Limiter** patterns for the inventory call

**Data Model — `Order`:**
| Field | Type | Description |
|---|---|---|
| `id` | `Long` | Auto-generated primary key |
| `orderNumber` | `String` | UUID-based order number |
| `skuCode` | `String` | Product SKU code |
| `price` | `BigDecimal` | Order price |
| `quantity` | `Integer` | Quantity ordered |

**Order Flow:**
```
Client POST /api/order
        │
        ▼
  OrderController
        │
        ▼
  OrderService.placeOrder()
        │
        ├──► InventoryClient.isInStock(skuCode, qty)  ──► Inventory Service
        │         (with Circuit Breaker + Retry)
        │
        ├──► orderRepository.save(order)               ──► MySQL
        │
        └──► kafkaTemplate.send("order-placed", event)  ──► Kafka Topic
```

**Key Files:**
```
order-service/
├── controller/OrderController.java      # REST endpoint for placing orders
├── service/OrderService.java            # Core order logic + Kafka publishing
├── client/InventoryClient.java          # Declarative HTTP client with resilience
├── model/Order.java                     # JPA entity
├── dto/OrderRequest.java                # Nested record with UserDetails
├── event/OrderPlacedEvent.java          # Avro-generated event class
├── config/
│   ├── RestClientConfig.java            # RestClient → InventoryClient proxy
│   ├── ObservationConfig.java           # Tracing configuration
│   └── OpenAPIConfig.java               # Swagger metadata
└── resources/avro/
    └── order-placed.avsc                # Avro schema definition
```

---

### 4. Inventory Service (Port `8082`)

Manages product stock levels and provides real-time availability checks.

**Key Responsibilities:**
- Checks if a product (by SKU code) has sufficient stock quantity
- Backed by **MySQL** with JPA/Hibernate

**Data Model — `Inventory`:**
| Field | Type | Description |
|---|---|---|
| `id` | `Long` | Auto-generated primary key |
| `skuCode` | `String` | Product SKU code |
| `quantity` | `Integer` | Available stock quantity |

**Key Files:**
```
inventory-service/
├── controller/InventoryController.java    # GET /api/inventory?skuCode=X&quantity=Y
├── service/InventoryService.java          # Stock availability check
├── model/Inventory.java                   # JPA entity
├── repository/InventoryRepository.java    # Custom query method
└── config/
    ├── OpenAPIConfig.java                 # Swagger metadata
    └── ObservationConfig.java             # Tracing configuration
```

---

### 5. Notification Service (Port `8090`)

An event-driven service that listens for order events and sends email notifications.

**Key Responsibilities:**
- **Kafka Consumer** — Listens on `order-placed` topic for `OrderPlacedEvent` (Avro deserialization)
- **Email Notifications** — Sends order confirmation emails via SMTP (Mailtrap)
- Fully decoupled from the Order Service (asynchronous, event-driven)

**Key Files:**
```
notification-service/
├── service/NotificationService.java          # @KafkaListener + email sender
├── NotificationServiceApplication.java       # Spring Boot application
└── resources/
    ├── application.properties                # Kafka consumer + SMTP config
    └── avro/order-placed.avsc                # Shared Avro schema
```

---

## 📨 Event-Driven Architecture

The system uses **Apache Kafka** with **Avro** serialization and **Confluent Schema Registry** for reliable, schema-enforced event streaming.

### Event Flow

```
┌──────────────────┐    Avro Event     ┌───────────────────┐
│   Order Service  │ ───────────────►  │   Kafka Broker    │
│   (Producer)     │  "order-placed"   │  (Confluent 7.5)  │
└──────────────────┘                   └────────┬──────────┘
                                                │
                                     ┌──────────▼──────────┐
                                     │  Schema Registry     │
                                     │  (Avro validation)   │
                                     └──────────┬──────────┘
                                                │
                                     ┌──────────▼──────────┐
                                     │ Notification Service │
                                     │    (Consumer)        │
                                     │  → Send Email        │
                                     └─────────────────────┘
```

### Avro Schema — `OrderPlacedEvent`

```json
{
  "type": "record",
  "name": "OrderPlacedEvent",
  "namespace": "com.ankitshiksharthi.microserviceproject.order_service.event",
  "fields": [
    { "name": "orderNumber", "type": "string" },
    { "name": "email",       "type": "string" },
    { "name": "firstName",   "type": "string" },
    { "name": "lastName",    "type": "string" }
  ]
}
```

---

## 🔐 Security

Authentication and authorization are handled via **Keycloak** (OpenID Connect / OAuth2).

| Component | Details |
|---|---|
| **Identity Provider** | Keycloak 24.0.1 (Dockerized) |
| **Realm** | `E-Commerce-Microservices-Project` |
| **Protocol** | OAuth2 / OpenID Connect |
| **Token Type** | JWT (RS256) |
| **Gateway Role** | OAuth2 Resource Server — validates JWT on every request |
| **Public Endpoints** | `/swagger-ui/**`, `/v3/api-docs/**`, `/aggregate/**` |
| **Keycloak UI** | `http://localhost:8181` (admin/admin) |

### Auth Flow

```
Client ──► Keycloak (/realms/.../token) ──► JWT Token
  │
  └──► API Gateway (Bearer Token) ──► Validates JWT ──► Routes to Service
```

---

## 🛡 Resilience Patterns

Implemented using **Resilience4j** across the API Gateway and Order Service.

### Circuit Breaker

| Property | Value |
|---|---|
| Sliding Window Type | `COUNT_BASED` |
| Sliding Window Size | `10` (Gateway) / `5` (Order) |
| Failure Rate Threshold | `50%` |
| Wait Duration in Open State | `5s` |
| Permitted Calls in Half-Open | `3` |
| Minimum Number of Calls | `5` |
| Auto Transition to Half-Open | `true` |

### Retry Policy
| Property | Value |
|---|---|
| Max Attempts | `3` |
| Wait Duration | `5s` |

### Time Limiter
| Property | Value |
|---|---|
| Timeout Duration | `3s` |

### Fallback Behavior
When a downstream service is unavailable, the gateway returns:
```
HTTP 503 — "Service Unavailable, please try again later"
```

---

## 🔭 Observability Stack

A full-fledged observability stack is integrated for production-level monitoring.

```
┌───────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│  Application  │────►│  Prometheus  │────►│   Grafana    │◄────│   Loki   │
│  (Micrometer) │     │   :9090      │     │   :3000      │     │  :3100   │
└───────┬───────┘     └──────────────┘     └──────────────┘     └──────────┘
        │                                         ▲
        │         ┌──────────────┐                │
        └────────►│    Tempo     │────────────────┘
                  │ (Zipkin fmt) │
                  │  :9411       │
                  └──────────────┘
```

| Tool | Purpose | Port |
|---|---|---|
| **Prometheus** | Metrics collection & storage | `9090` |
| **Grafana** | Dashboards & visualization | `3000` |
| **Grafana Tempo** | Distributed trace storage | `3110` (Tempo) / `9411` (Zipkin) |
| **Grafana Loki** | Log aggregation | `3100` |
| **Micrometer** | Application metrics (Prometheus exporter) | Embedded |
| **Loki Logback Appender** | Ship application logs to Loki | Embedded |
| **Spring Boot Actuator** | Health checks, metrics endpoints | `/actuator/**` |

All services export:
- ✅ Prometheus metrics via `/actuator/prometheus`
- ✅ Distributed traces via Zipkin format to Tempo
- ✅ Structured logs to Loki
- ✅ 100% trace sampling (`management.tracing.sampling.probability=1.0`)

---

## 📖 API Documentation

Swagger UI is available at the **API Gateway** level, aggregating docs from all services:

| Service | Direct Swagger | Aggregated (via Gateway) |
|---|---|---|
| Product Service | `http://localhost:8080/swagger-ui.html` | `http://localhost:8083/swagger-ui.html` → Select "Product Service" |
| Order Service | `http://localhost:8081/swagger-ui.html` | `http://localhost:8083/swagger-ui.html` → Select "Order Service" |
| Inventory Service | `http://localhost:8082/swagger-ui.html` | `http://localhost:8083/swagger-ui.html` → Select "Inventory Service" |

---

## 📁 Project Structure

```
e-commerce/                              # Parent Maven project (POM aggregator)
│
├── api-gateway/                         # 🔐 API Gateway Service
│   ├── src/main/java/.../api_gateway/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java      # OAuth2 + CORS
│   │   │   └── ObservationConfig.java   # Micrometer tracing
│   │   └── routes/
│   │       └── Routes.java              # Gateway route definitions
│   ├── docker-compose.yaml              # Keycloak + Observability stack
│   └── docker/
│       ├── keycloak/realms/             # Keycloak realm import
│       ├── prometheus/prometheus.yaml    # Prometheus scrape config
│       ├── tempo/tempo.yaml             # Tempo trace config
│       └── grafana/                     # Grafana datasource provisioning
│
├── product-service/                     # 📦 Product Catalog Service
│   ├── src/main/java/.../product_service/
│   │   ├── controller/ProductController.java
│   │   ├── service/ProductService.java
│   │   ├── model/Product.java           # MongoDB @Document
│   │   ├── dto/
│   │   │   ├── ProductRequest.java      # Java Record
│   │   │   └── ProductResponse.java     # Java Record
│   │   ├── repository/ProductRepository.java
│   │   └── config/
│   ├── docker-compose.yaml              # MongoDB container
│   └── src/test/                        # Testcontainers-based tests
│
├── order-service/                       # 🛍️ Order Management Service
│   ├── src/main/java/.../order_service/
│   │   ├── controller/OrderController.java
│   │   ├── service/OrderService.java    # Inventory check + Kafka publish
│   │   ├── client/InventoryClient.java  # Declarative HTTP + Circuit Breaker
│   │   ├── model/Order.java             # JPA @Entity
│   │   ├── dto/OrderRequest.java        # Nested record with UserDetails
│   │   ├── event/OrderPlacedEvent.java  # Avro-generated
│   │   └── config/
│   │       └── RestClientConfig.java    # Spring RestClient proxy factory
│   ├── src/main/resources/avro/
│   │   └── order-placed.avsc            # Avro schema
│   └── docker-compose.yaml              # MySQL + Kafka + Schema Registry + Kafka UI
│
├── inventory-service/                   # 📊 Stock Management Service
│   ├── src/main/java/.../inventory_service/
│   │   ├── controller/InventoryController.java
│   │   ├── service/InventoryService.java
│   │   ├── model/Inventory.java         # JPA @Entity
│   │   ├── repository/InventoryRepository.java
│   │   └── config/
│   └── docker-compose.yaml              # MySQL container
│
├── notification-service/                # 📧 Event-Driven Notification Service
│   ├── src/main/java/.../notification_service/
│   │   └── service/NotificationService.java  # @KafkaListener + Email
│   └── src/main/resources/avro/
│       └── order-placed.avsc            # Shared Avro schema
│
├── pom.xml                              # Parent POM (module aggregator)
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| Docker & Docker Compose | Latest |

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/e-commerce-microservices.git
cd e-commerce-microservices
```

### 2. Start Infrastructure (Docker)

Start all infrastructure services (databases, Kafka, Keycloak, observability):

```bash
# Start Keycloak + Observability Stack
cd api-gateway && docker compose up -d && cd ..

# Start MongoDB (Product Service)
cd product-service && docker compose up -d && cd ..

# Start MySQL (Inventory Service)
cd inventory-service && docker compose up -d && cd ..

# Start MySQL + Kafka ecosystem (Order Service)
cd order-service && docker compose up -d && cd ..
```

### 3. Build & Run Services

```bash
# Build entire project from root
./mvnw clean install -DskipTests

# Run each service (in separate terminals)
./mvnw spring-boot:run -pl product-service
./mvnw spring-boot:run -pl order-service
./mvnw spring-boot:run -pl inventory-service
./mvnw spring-boot:run -pl notification-service
./mvnw spring-boot:run -pl api-gateway
```

### 4. Access the Application

| Service | URL |
|---|---|
| **API Gateway** | `http://localhost:8083` |
| **Swagger UI (Aggregated)** | `http://localhost:8083/swagger-ui.html` |
| **Keycloak Admin** | `http://localhost:8181` (admin/admin) |
| **Grafana Dashboard** | `http://localhost:3000` |
| **Prometheus** | `http://localhost:9090` |
| **Kafka UI** | `http://localhost:8086` |

---

## 📡 API Endpoints

### Product Service

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/product` | Create a new product | 🔒 JWT |
| `GET` | `/api/product` | Get all products | 🔒 JWT |

**Sample Request — Create Product:**
```json
POST /api/product
{
  "name": "iPhone 16 Pro",
  "description": "Latest Apple smartphone",
  "price": 999.99
}
```

### Order Service

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/order` | Place a new order | 🔒 JWT |

**Sample Request — Place Order:**
```json
POST /api/order
{
  "skuCode": "iphone_16_pro",
  "price": 999.99,
  "quantity": 1,
  "userDetails": {
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

### Inventory Service

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/inventory?skuCode={code}&quantity={qty}` | Check stock availability | 🔒 JWT |

---

## 🏆 Key Design Decisions & Patterns

| Pattern | Implementation |
|---|---|
| **API Gateway** | Centralized routing, auth, and resilience via Spring Cloud Gateway MVC |
| **Event-Driven** | Kafka + Avro for async order notifications (loose coupling) |
| **Schema Evolution** | Confluent Schema Registry enforces Avro schema compatibility |
| **Circuit Breaker** | Resilience4j protects inter-service calls with fallbacks |
| **Declarative HTTP Clients** | `HttpServiceProxyFactory` + `RestClient` (Spring 6.1+ pattern) |
| **Polyglot Persistence** | MongoDB for flexible product data, MySQL for relational order/inventory data |
| **DTO Pattern** | Java Records for immutable, concise data transfer objects |
| **Distributed Tracing** | End-to-end request tracing across services via Micrometer Brave + Tempo |
| **Centralized Logging** | Loki + Grafana for log aggregation across all services |
| **Infrastructure as Code** | Docker Compose for reproducible local environments |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the Apache License 2.0.

---

<p align="center">
  Built with ❤️ by <strong>Ankit Shiksharthi</strong>
</p>
