# FixFlow

### Secure Role-Based Property Maintenance Management System

FixFlow is a **Spring Boot backend application** for managing property maintenance across **Tenants, Vendors, and Managers**.

```text
Tenant reports issue
        ↓
Manager reviews request
        ↓
Manager assigns Vendor
        ↓
Vendor accepts / rejects
        ↓
Vendor tracks work
        ↓
Request completed
        ↓
Tenant rates Vendor
```

The project focuses on **REST API design, JWT authentication, Spring Security, role-based authorization, ownership and assignment checks, business-rule enforcement, validation, exception handling, and relational data management**.

---

## Overview

### Core Capabilities

- User registration and authentication
- JWT-based stateless authentication
- BCrypt password hashing
- Role-based, ownership-based, and assignment-based authorization
- Property and unit management
- Maintenance request management
- Vendor assignment and status management
- Work progress updates and comments
- Attachment metadata management
- Notifications and vendor ratings
- Explicit state transitions
- Request and business-rule validation
- Centralized exception handling

### Backend Architecture

```text
Client
  ↓
Spring Security / JWT
  ↓
Controller
  ↓
DTO + Validation
  ↓
Mapper
  ↓
Service
  ↓
Repository
  ↓
JPA / Hibernate
  ↓
MySQL
```

---

## Core Workflow

```text
                         TENANT
                           │
                           │ Report Issue
                           ▼
                ┌──────────────────────┐
                │ Maintenance Request  │
                │      SUBMITTED       │
                └──────────┬───────────┘
                           │
                           │ Manager Review
                           ▼
                ┌──────────────────────┐
                │    UNDER_REVIEW      │
                └──────────┬───────────┘
                           │
             ┌─────────────┼─────────────┐
             │             │             │
          Assign         Reject        Cancel
             │             │             │
             ▼             ▼             ▼
         ASSIGNED       REJECTED      CANCELLED
             │
             │ REQUEST_ASSIGNED
             ▼
          VENDOR
             │
        ┌────┴────┐
     Accept      Reject
        │           │
        ▼           ▼
   ACCEPTED      REJECTED
        │
        ▼
   IN_PROGRESS
        │
   ┌────┼──────────────┐
   │    │              │
Work  Comments    Attachments
Updates
   │    │              │
   └────┴──────┬───────┘
               │
               │ Complete Work
               ▼
          COMPLETED
               │
               ▼
       Tenant Rates Vendor
```

---

## Maintenance Request Lifecycle

Maintenance requests use explicit state transitions.

```text
SUBMITTED
   ├──→ UNDER_REVIEW
   ├──→ ASSIGNED
   └──→ CANCELLED

UNDER_REVIEW
   ├──→ ASSIGNED
   ├──→ REJECTED
   └──→ CANCELLED

ASSIGNED
   ├──→ IN_PROGRESS
   └──→ CANCELLED

IN_PROGRESS
   ├──→ COMPLETED
   └──→ CANCELLED
```

### Terminal States

`COMPLETED` · `CANCELLED` · `REJECTED`

Operations not allowed by the business rules are rejected once a request reaches a terminal state.

---

## Assignment Lifecycle

```text
PENDING
   ├──→ ACCEPTED ──→ COMPLETED
   │       │
   │       └──────→ CANCELLED
   │
   ├──→ REJECTED
   └──→ CANCELLED
```

| Current State | Allowed States |
|---|---|
| `PENDING` | `ACCEPTED`, `REJECTED`, `CANCELLED` |
| `ACCEPTED` | `COMPLETED`, `CANCELLED` |

Invalid transitions are rejected by the service layer.

---

## Vendor Assignment

When a Manager assigns a Vendor, FixFlow performs multiple business checks.

```text
Manager
   ↓
Select Request + Vendor
   ↓
Vendor Exists?
   ├── NO  → 404 Not Found
   ↓ YES
Vendor Role = VENDOR?
   ├── NO  → 400 Bad Request
   ↓ YES
Request Terminal?
   ├── YES → 400 Bad Request
   ↓ NO
Active Assignment Exists?
   ├── YES → 400 Bad Request
   ↓ NO
Create Assignment = PENDING
   ↓
MaintenanceRequest = ASSIGNED
   ↓
Save
   ↓
REQUEST_ASSIGNED Notification
```

An active duplicate assignment is prevented when an existing assignment is already `PENDING` or `ACCEPTED`.

---

## Vendor Work

```text
Vendor
  ↓
PENDING Assignment
  ├── Accept → ACCEPTED → IN_PROGRESS
  └── Reject → REJECTED

IN_PROGRESS
  ├── Work Updates
  ├── Comments
  ├── Attachments
  └── Notifications
          ↓
     Complete Work
          ↓
Assignment = COMPLETED
          ↓
MaintenanceRequest = COMPLETED
          ↓
Tenant can rate Vendor
```

Managers can cancel eligible assignments according to the assignment state rules.

---

## Work Updates & Comments

### Work Update

```text
Vendor JWT
    ↓
Assigned to Request?
    ├── NO  → 403 Forbidden
    ↓ YES
Request Terminal?
    ├── YES → 400 Bad Request
    ↓ NO
Create WorkUpdate
    ↓
Save
    ↓
WORK_UPDATE Notification
    ↓
Tenant
```

A `WorkUpdate` is associated with a `MaintenanceRequest` and `Vendor`.

### Comments

**Tenant:**

```text
Tenant JWT
  ↓
Owns Request?
  ├── NO → 403
  ↓ YES
Create Comment → Save → NEW_COMMENT → Vendor
```

**Vendor:**

```text
Vendor JWT
  ↓
Assigned to Request?
  ├── NO → 403
  ↓ YES
Create Comment → Save → NEW_COMMENT → Tenant
```

Comment update/delete is allowed for the **Manager or original author**.

---

## Attachments

FixFlow currently manages **attachment metadata**, not actual file storage.

Stored metadata:

- `name`
- `contentType`
- `size`
- `description`
- `maintenanceRequestId`

```text
User
  ↓
Authorization
  ├── Tenant  → Own Request?
  ├── Vendor  → Valid Assignment?
  └── Manager → Management Access
  ↓
Request Terminal?
  ├── YES → 400
  ↓ NO
Create / Update Metadata
  ↓
MySQL
```

Actual file upload and storage are future improvements.

---

## Notifications

Notifications are generated by important business events.

| Event | Notification |
|---|---|
| Manager assigns Vendor | `REQUEST_ASSIGNED` |
| Vendor accepts assignment | `ASSIGNMENT_ACCEPTED` |
| Vendor rejects assignment | `ASSIGNMENT_REJECTED` |
| Tenant/Vendor creates comment | `NEW_COMMENT` |
| Vendor creates work update | `WORK_UPDATE` |
| Request/assignment completed | `REQUEST_COMPLETED` |

Notifications are persisted and associated with users.

---

## Rating

A Tenant can rate a Vendor only after the maintenance request is completed.

```text
Tenant
  ↓
Rate Vendor
  ↓
Tenant is Reporter?
  ├── NO → Authorization / Business Failure
  ↓ YES
Request = COMPLETED?
  ├── NO → 400
  ↓ YES
Vendor has COMPLETED Assignment?
  ├── NO → 400
  ↓ YES
Already Rated?
  ├── YES → 400
  ↓ NO
Score Valid? 1–5
  ├── NO → 400
  ↓ YES
Create Rating
  ↓
MySQL
```

Only **one rating is allowed per maintenance request**.

---

# Security Architecture

FixFlow uses **Spring Security with JWT-based stateless authentication**.

## Authentication

```text
Client
  ↓ Login
AuthenticationManager
  ↓
UserDetailsService
  ↓
BCrypt Password Check
  ├── Invalid → Authentication Failure
  └── Valid → Generate JWT
                 ↓
              Client
                 ↓
          Protected Request
                 ↓
       JwtAuthenticationFilter
                 ↓
        SecurityContextHolder
                 ↓
          Role Authorization
                 ↓
             Controller
```

For protected requests, `JwtAuthenticationFilter` extracts and validates the Bearer token, creates the Authentication object, and places it in the `SecurityContextHolder`.

## Password Security

Passwords are never stored as plain text.

```text
Registration:
Plain Password → BCryptPasswordEncoder → BCrypt Hash → MySQL

Login:
Password → BCryptPasswordEncoder.matches()
         → Stored BCrypt Hash
         → Authentication Result
```

Passwords are not exposed through response DTOs.

## Authorization

```text
JWT Authentication
        ↓
Role-Based Authorization
        ↓
Ownership / Assignment Authorization
        ↓
Business Rule Validation
        ↓
Operation
```

### Roles

| Role | Main Responsibilities |
|---|---|
| `TENANT` | Register, create/update own requests, access own resources, comment, view relevant updates, rate completed requests |
| `VENDOR` | View assigned work, accept/reject assignments, add work updates/comments, complete accepted assignments |
| `MANAGER` | Manage properties/units/users, review requests, assign vendors, perform supported management operations |

### Ownership & Assignment

A Tenant must be the reporter/owner of a request for protected tenant operations.

A Vendor must have the required assignment for protected vendor operations.

```text
Tenant → Request → Is Reporter?
                    └── NO → 403

Vendor → Request → Required Assignment?
                    └── NO → 403
```

This prevents Vendors from accessing another Vendor's assigned work.

---

## Global Request Pipeline

```text
HTTP Request
    ↓
JWT Authentication Filter
    ↓
SecurityContext
    ↓
Role Authorization
    ↓
Controller
    ↓
DTO Validation
    ↓
Mapper
    ↓
Service
    ├── Ownership / Assignment Check
    └── Business Rule Check
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
MySQL
    ↓
Response
```

---

# Validation & Exception Handling

## Validation

Request DTOs are validated before business processing.

```text
HTTP Request
    ↓
@Valid
    ↓
Validation
 ┌──┴──┐
NO    YES
│       │
▼       ▼
400   Service
```

Validation covers required fields, non-blank strings, required IDs, positive values, rating scores `1–5`, and string length constraints.

## Exception Mapping

| Status | Handler | Examples |
|---|---|---|
| `400` | `GlobalExceptionHandler` | Validation and business-rule errors |
| `403` | `CustomAccessDeniedHandler` | Insufficient role/ownership/assignment |
| `404` | `GlobalExceptionHandler` | Resource not found |

### Common Business Errors

- Duplicate active assignment
- Invalid status transition
- Operation on terminal request
- Rating before completion
- Duplicate rating
- Invalid reporter/unit relationship
- Invalid vendor assignment

### Common Authorization Errors

- Insufficient role
- Tenant accessing another user's resource
- Vendor accessing another Vendor's assignment
- Unauthorized comment modification

---

# User Registration

Public registration creates **TENANT accounts only**.

```text
POST /api/users
      ↓
Validate DTO
      ↓
Force Role = TENANT
      ↓
BCrypt Password Hashing
      ↓
Save User
```

`MANAGER` and `VENDOR` roles cannot be selected through public registration.

---

# Property & Unit Management

Managers manage properties and units.

```text
Manager
  ├── Create / Update / Delete Property
  └── Create / Update / Delete Unit
```

Relationship:

```text
Property
   └── Unit
        └── User
```

Maintenance requests are associated with units.

---

# Backend Architecture

FixFlow follows a layered architecture with clear separation of responsibilities.

| Layer | Responsibility |
|---|---|
| Controller | REST endpoints and HTTP concerns |
| DTO | API request/response models and validation |
| Mapper | DTO ↔ Entity conversion |
| Service | Business logic, authorization, workflow rules |
| Repository | Database access through Spring Data JPA |
| Entity | Persistent domain model |
| Security | JWT authentication and access control |
| Exception | Centralized application-level error handling |

### Typical Feature Flow

```text
HTTP Request
     ↓
Controller
     ↓
Request DTO
     ↓
Validation
     ↓
Service
     ↓
Business / Authorization Checks
     ↓
Mapper
     ↓
Entity
     ↓
Repository
     ↓
MySQL
```

### Controller

Handles HTTP requests, receives DTOs, triggers validation, calls services, and returns response DTOs.

### DTO

Controls data entering and leaving the API.

Examples:

```text
UserRegistrationDto
UserResponseDto
MaintenanceRequestDto
MaintenanceRequestResponseDto
AssignmentRequestDto
AssignmentUpdateDto
AssignmentResponseDto
WorkUpdateRequestDto
WorkUpdateResponseDto
CommentRequestDto
CommentResponseDto
AttachmentRequestDto
AttachmentResponseDto
RatingRequestDto
RatingResponseDto
```

### Mapper

```text
Request DTO → Mapper → Entity
Entity      → Mapper → Response DTO
```

JPA entities are not directly exposed through the API.

### Service

Contains business rules, authorization checks, ownership/assignment validation, state transitions, duplicate prevention, relationship validation, notifications, transactions, and repository coordination.

### Repository

Provides entity persistence, retrieval, updates, deletion, derived queries, and custom queries where required.

### Security

Handles JWT authentication, password encoding, authentication context, role authorization, access-denied handling, and security configuration.

### Exception

Important exceptions include:

- `ResourceNotFoundException`
- `BusinessException`
- `MethodArgumentNotValidException`

Spring Security access-denied situations are handled separately through the custom access-denied handler.

---

# Data Model

### Main Entities

```text
User
Property
Unit
MaintenanceRequest
Assignment
WorkUpdate
Comment
Attachment
Notification
Rating
```

### Main Relationships

```text
Property
   └── Unit
        └── User

User
 ├── reports ────────→ MaintenanceRequest
 ├── vendor ─────────→ Assignment
 ├── author ─────────→ Comment
 ├── vendor ─────────→ WorkUpdate
 ├── receives ───────→ Notification
 └── tenant/vendor ──→ Rating

MaintenanceRequest
 ├── has ────────────→ Assignment
 ├── has ────────────→ WorkUpdate
 ├── has ────────────→ Comment
 ├── has ────────────→ Attachment
 └── has ────────────→ Rating
```

---

# API Overview

| Resource | Purpose |
|---|---|
| `/auth` | Authentication |
| `/api/users` | User management |
| `/api/property` | Property management |
| `/api/unit` | Unit management |
| `/api/maintenanceRequest` | Maintenance request management |
| `/api/assignment` | Vendor assignments |
| `/api/workUpdate` | Work progress updates |
| `/api/comment` | Maintenance request comments |
| `/api/attachment` | Attachment metadata |
| `/api/notification` | Notifications |
| `/api/rating` | Vendor ratings |

Access is protected using Spring Security, HTTP method authorization, role checks, and service-level ownership/assignment validation.

---

# Project Structure

```text
fixFlow/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/maintenance/fixFlow/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

`application.properties` is intentionally excluded from Git because it contains environment-specific configuration and secrets.

---

# Tech Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| BCrypt | Password hashing |
| Spring Data JPA | Data access |
| Hibernate | ORM / persistence |
| MySQL | Relational database |
| Maven | Build and dependency management |
| Jakarta Validation | Request validation |
| Postman | REST API testing |
| Git / GitHub | Version control |

---

# API Testing

The backend has been manually tested using Postman.

Major testing areas:

- Authentication and JWT protection
- Role-based authorization
- Ownership and assignment authorization
- Maintenance request lifecycle
- Assignment state transitions
- Work updates and comments
- Notifications
- Attachments
- Ratings and duplicate-rating protection
- Validation failures
- Business-rule violations
- Access-denied responses
- Resource-not-found handling

Examples:

```text
Unauthorized resource access → 403 Forbidden
Unauthorized comment modification → 403 Forbidden
Invalid business operation → 400 Bad Request
Missing resource → 404 Not Found
```

---

# Setup & Installation

## 1. Clone

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd fixFlow
```

## 2. Create MySQL Database

```sql
CREATE DATABASE fixflow;
```

## 3. Configure `application.properties`

Create:

```text
src/main/resources/application.properties
```

Configure:

```text
MySQL URL
Database username
Database password
JWT secret
JWT configuration
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fixflow
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update

jwt.secret=YOUR_SECRET
```

> **Important:** `application.properties` contains environment-specific configuration and secrets and is intentionally excluded from Git. Do not commit database passwords, JWT secrets, or other sensitive credentials.

## 4. Run

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

Test the APIs using Postman.

---

# Default Development Account

The project includes a development `DataInitializer` that creates a Manager account when one does not already exist.

```text
Email:    manager@fixflow.com
Password: Manager@123
Role:     MANAGER
```

This account is intended for **local development and testing only**.

Production deployments should use a proper account provisioning strategy and should not rely on hardcoded development credentials.

---

# Current Project Scope

FixFlow currently focuses on the **backend and REST API layer**.

```text
Authentication
      +
Authorization
      +
Maintenance Workflow
      +
Vendor Assignment
      +
Work Tracking
      +
Comments
      +
Attachment Metadata
      +
Notifications
      +
Ratings
      +
Database Persistence
```

The APIs can be consumed by a dedicated frontend application for role-specific user interfaces.

---

# Future Improvements

- Frontend application for Tenant, Vendor, and Manager dashboards
- Actual file upload and storage
- Swagger / OpenAPI documentation
- Automated unit tests
- Integration testing
- Production database migration strategy
- Production deployment configuration
- Improved Vendor onboarding and management
- Email or real-time notification delivery
- Monitoring and observability
- Production-ready secret management

---

## FixFlow Backend

A secure, role-based Spring Boot backend built around **real maintenance workflows, layered architecture, and defense-in-depth authorization**.
