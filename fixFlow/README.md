# FixFlow

### Secure Role-Based Property Maintenance Management System

FixFlow is a **Spring Boot backend application** designed to manage property maintenance operations across **Tenants, Vendors, and Managers**.

The system manages the complete maintenance lifecycle — from reporting a maintenance issue and reviewing the request to assigning a vendor, tracking work progress, completing the request, generating notifications, and allowing the tenant to rate the vendor.

The project focuses on **backend architecture, REST API design, JWT-based authentication, Spring Security, role-based authorization, resource ownership, assignment-based authorization, business-rule enforcement, validation, exception handling, and relational data management**.

---

# Overview

FixFlow provides a centralized backend for managing property maintenance requests and their complete lifecycle.

### Core capabilities

- User registration and authentication
- JWT-based stateless authentication
- BCrypt password hashing
- Role-based authorization
- Ownership-based authorization
- Vendor-assignment authorization
- Property management
- Unit management
- Maintenance request management
- Vendor assignment
- Assignment status management
- Work progress updates
- Comments
- Attachment metadata management
- Notifications
- Vendor ratings
- Maintenance state transition management
- Request validation
- Business-rule validation
- Centralized exception handling

The backend follows a layered architecture:

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


Application Workflow

The core FixFlow workflow is:

                         ┌─────────────────────┐
                         │       TENANT        │
                         └──────────┬──────────┘
                                    │
                                    │ Report Maintenance Issue
                                    ▼
                    ┌──────────────────────────────┐
                    │   CREATE MAINTENANCE        │
                    │          REQUEST             │
                    │                              │
                    │ • Title                     │
                    │ • Description               │
                    │ • Priority                  │
                    │ • Category                  │
                    │ • Tenant derived from JWT   │
                    │ • Tenant's Unit associated  │
                    └──────────────┬───────────────┘
                                   │
                                   ▼
                         ┌─────────────────┐
                         │    SUBMITTED    │
                         └────────┬────────┘
                                  │
                                  │ Manager Review
                                  ▼
                         ┌─────────────────┐
                         │  UNDER_REVIEW   │
                         └────────┬────────┘
                                  │
                    ┌─────────────┼──────────────┐
                    │             │              │
                    │             │              │
                 Assign         Reject         Cancel
                 Vendor           │              │
                    │             ▼              ▼
                    │        REJECTED        CANCELLED
                    │        TERMINAL        TERMINAL
                    ▼
             ┌──────────────┐
             │   ASSIGNED   │
             └──────┬───────┘
                    │
                    │ Vendor receives
                    │ REQUEST_ASSIGNED
                    │ notification
                    ▼
             ┌──────────────┐
             │    VENDOR    │
             └──────┬───────┘
                    │
              ┌─────┴──────┐
              │            │
           Accept        Reject
              │            │
              ▼            ▼
       ┌────────────┐  ┌────────────┐
       │ ACCEPTED   │  │  REJECTED  │
       └─────┬──────┘  └──────┬─────┘
             │                │
             ▼                ▼
       IN_PROGRESS        REJECTED
             │              TERMINAL
             │
             ├──────────────► Work Updates
             │
             ├──────────────► Comments
             │
             ├──────────────► Attachments
             │
             └──────────────► Notifications
             │
             │ Vendor Completes
             ▼
       ┌──────────────┐
       │  COMPLETED   │
       │   TERMINAL   │
       └──────┬───────┘
              │
              │
              ▼
       ┌──────────────┐
       │    TENANT    │
       └──────┬───────┘
              │
              │ Rate Vendor
              ▼
       ┌──────────────┐
       │    RATING    │
       └──────────────┘

Maintenance Request Lifecycle

Maintenance requests use explicit state transitions.

                         ┌──────────────┐
                         │  SUBMITTED   │
                         └──────┬───────┘
                                │
                         Manager Review
                                │
                                ▼
                       ┌────────────────┐
                       │ UNDER_REVIEW   │
                       └───────┬────────┘
                               │
                 ┌─────────────┼──────────────┐
                 │             │              │
              Assign         Reject         Cancel
                 │             │              │
                 ▼             ▼              ▼
             ASSIGNED      REJECTED       CANCELLED
                 │          TERMINAL        TERMINAL
                 │
            Vendor Accepts
                 │
                 ▼
            IN_PROGRESS
                 │
          ┌──────┴───────┐
          │              │
       Complete        Cancel
          │              │
          ▼              ▼
      COMPLETED       CANCELLED
      TERMINAL        TERMINAL
      
      
Valid Maintenance Request Transitions
SUBMITTED      → UNDER_REVIEW
SUBMITTED      → ASSIGNED
SUBMITTED      → CANCELLED

UNDER_REVIEW   → ASSIGNED
UNDER_REVIEW   → REJECTED
UNDER_REVIEW   → CANCELLED

ASSIGNED       → IN_PROGRESS
ASSIGNED       → CANCELLED

IN_PROGRESS    → COMPLETED
IN_PROGRESS    → CANCELLED
Terminal States
COMPLETED
CANCELLED
REJECTED

Once a request reaches a terminal state, operations that are not allowed by the business rules are rejected.

Assignment Lifecycle

Vendor assignments maintain their own state.

                       ┌────────────┐
                       │   PENDING  │
                       └─────┬──────┘
                             │
                 ┌───────────┼────────────┐
                 │           │            │
              Accept       Reject       Cancel
                 │           │            │
                 ▼           ▼            ▼
             ACCEPTED    REJECTED      CANCELLED
                 │        TERMINAL      TERMINAL
                 │
                 ├──────────────► CANCELLED
                 │
                 ▼
             COMPLETED
              TERMINAL
Valid Assignment Transitions
PENDING  → ACCEPTED
PENDING  → REJECTED
PENDING  → CANCELLED

ACCEPTED → COMPLETED
ACCEPTED → CANCELLED

Invalid assignment transitions are rejected by the service layer.

Vendor Assignment Flow

When a Manager assigns a Vendor, multiple business checks are performed.

Manager
   │
   ▼
Select Maintenance Request
   │
   ▼
Select Vendor
   │
   ▼
Vendor Exists?
   │
   ├── NO ──► ResourceNotFoundException
   │              │
   │              └──► 404 Not Found
   │
   ▼ YES
Vendor Role = VENDOR?
   │
   ├── NO ──► BusinessException
   │              │
   │              └──► 400 Bad Request
   │
   ▼ YES
Maintenance Request Terminal?
   │
   ├── YES ──► BusinessException
   │              │
   │              └──► 400 Bad Request
   │
   ▼ NO
Existing Active Assignment?
   │
   ├── YES ──► Duplicate Assignment
   │              │
   │              └──► 400 Bad Request
   │
   ▼ NO
Create Assignment
   │
   ▼
Assignment = PENDING
   │
   ▼
MaintenanceRequest = ASSIGNED
   │
   ▼
Save to Database
   │
   ▼
REQUEST_ASSIGNED Notification
   │
   ▼
Vendor

An active duplicate assignment is prevented when an existing assignment is already PENDING or ACCEPTED.

Vendor Work Flow

After receiving an assignment:

Vendor
   │
   ▼
PENDING Assignment
   │
   ├───────────────┐
   │               │
 Accept          Reject
   │               │
   ▼               ▼
ACCEPTED        REJECTED
   │               │
   ▼               ▼
IN_PROGRESS    Request = REJECTED
   │              TERMINAL
   │
   ├──────────────► Work Updates
   │
   ├──────────────► Comments
   │
   ├──────────────► Attachments
   │
   └──────────────► Notifications
   │
   │ Complete Work
   ▼
Assignment = COMPLETED
   │
   ▼
MaintenanceRequest = COMPLETED
   │
   ▼
Tenant can rate Vendor
Work Update Flow

Work updates allow Vendors to record progress on assigned maintenance requests.

Vendor JWT
    │
    ▼
Identify Authenticated Vendor
    │
    ▼
Vendor Role Valid?
    │
    ├── NO ──► 403 Forbidden
    │
    ▼ YES
Vendor Assigned to Request?
    │
    ├── NO ──► Access Denied
    │
    ▼ YES
Request Terminal?
    │
    ├── YES ──► BusinessException
    │              │
    │              └──► 400 Bad Request
    │
    ▼ NO
Create WorkUpdate
    │
    ▼
Save to Database
    │
    ▼
WORK_UPDATE Notification
    │
    ▼
Tenant

A WorkUpdate is associated with:

MaintenanceRequest
        +
Vendor
Comment Flow

Comments allow users involved with a maintenance request to communicate.

Tenant Comment
Tenant JWT
   │
   ▼
Owns Maintenance Request?
   │
   ├── NO ──► 403 Forbidden
   │
   ▼ YES
Create Comment
   │
   ▼
Save Comment
   │
   ▼
NEW_COMMENT Notification
   │
   ▼
Vendor
Vendor Comment
Vendor JWT
   │
   ▼
Assigned to Maintenance Request?
   │
   ├── NO ──► 403 Forbidden
   │
   ▼ YES
Create Comment
   │
   ▼
Save Comment
   │
   ▼
NEW_COMMENT Notification
   │
   ▼
Tenant
Comment Update / Delete
User
  │
  ▼
Modify Comment
  │
  ▼
Manager OR Original Author?
  │
  ├── NO ──► 403 Forbidden
  │
  ▼ YES
Operation Allowed
Attachment Flow

FixFlow currently manages attachment metadata, not actual file storage.

Stored metadata includes:

name
contentType
size
description
maintenanceRequestId

Flow:

User
  │
  ▼
Attachment Operation
  │
  ▼
Authorization Check
  │
  ├── Tenant → Own Request?
  │
  ├── Vendor → Valid Assignment?
  │
  └── Manager → Management Access
  │
  ▼
Request Terminal?
  │
  ├── YES ──► BusinessException → 400
  │
  ▼ NO
Create / Update Attachment Metadata
  │
  ▼
Save to MySQL

Actual file upload and file storage are future improvements.

Notification Flow

Notifications are generated by important business events.

Manager Assigns Vendor
        │
        ▼
REQUEST_ASSIGNED
        │
        ▼
      Vendor
Vendor Accepts Assignment
        │
        ▼
ASSIGNMENT_ACCEPTED
Vendor Rejects Assignment
        │
        ▼
ASSIGNMENT_REJECTED
Tenant / Vendor Creates Comment
        │
        ▼
NEW_COMMENT
        │
        ▼
Relevant User
Vendor Creates Work Update
        │
        ▼
WORK_UPDATE
        │
        ▼
      Tenant
Request / Assignment Completion
        │
        ▼
REQUEST_COMPLETED

Notifications are persisted and associated with users.

Notification Types
ASSIGNMENT_ACCEPTED
ASSIGNMENT_REJECTED
NEW_COMMENT
REQUEST_ASSIGNED
REQUEST_COMPLETED
WORK_UPDATE
Rating Flow

A Tenant can rate a Vendor only after the maintenance request has been completed.

Tenant
  │
  ▼
Rate Vendor
  │
  ▼
Tenant is Reporter of Request?
  │
  ├── NO ──► Authorization / Business Failure
  │
  ▼ YES
Request = COMPLETED?
  │
  ├── NO ──► BusinessException → 400
  │
  ▼ YES
Vendor has COMPLETED Assignment?
  │
  ├── NO ──► BusinessException
  │
  ▼ YES
Already Rated?
  │
  ├── YES ──► BusinessException → 400
  │           "Maintenance request has already been rated"
  │
  ▼ NO
Score Valid? 1–5
  │
  ├── NO ──► Validation Error → 400
  │
  ▼ YES
Create Rating
  │
  ▼
Save to MySQL

Only one rating is allowed per maintenance request.

Security Architecture

Security is a core part of FixFlow.

The application uses Spring Security with JWT-based stateless authentication.

Authentication Flow
                         ┌──────────────┐
                         │    Client    │
                         └──────┬───────┘
                                │
                              Login
                                │
                                ▼
                    ┌────────────────────────┐
                    │ AuthenticationManager  │
                    └───────────┬────────────┘
                                │
                                ▼
                    ┌────────────────────────┐
                    │ UserDetailsService     │
                    └───────────┬────────────┘
                                │
                                ▼
                    ┌────────────────────────┐
                    │ BCrypt Password Check  │
                    └───────────┬────────────┘
                                │
                         Credentials Valid?
                           /            \
                         NO              YES
                         │                │
                         ▼                ▼
                 Authentication       Generate JWT
                    Failure               │
                                         ▼
                              JWT returned to Client
                                         │
                                         │ Protected Request
                                         ▼
                              ┌──────────────────────┐
                              │ JwtAuthentication    │
                              │       Filter         │
                              └──────────┬───────────┘
                                         │
                                         ▼
                              ┌──────────────────────┐
                              │  SecurityContext     │
                              │      Holder          │
                              └──────────┬───────────┘
                                         │
                                         ▼
                              ┌──────────────────────┐
                              │ Role Authorization   │
                              └──────────┬───────────┘
                                         │
                                         ▼
                                    Controller
JWT Authentication

For protected requests:

Client
  │
  │ Bearer JWT
  ▼
JwtAuthenticationFilter
  │
  ├── Extract Token
  ├── Validate Token
  ├── Extract User Information
  └── Create Authentication
          │
          ▼
    SecurityContextHolder
          │
          ▼
       Controller

FixFlow uses stateless JWT authentication rather than relying on an HTTP session.

Password Security

Passwords are never stored as plain text.

FixFlow uses:

BCryptPasswordEncoder
Registration
Plain Password
      │
      ▼
BCryptPasswordEncoder
      │
      ▼
Hashed Password
      │
      ▼
MySQL
Login
Login Password
      │
      ▼
BCryptPasswordEncoder.matches()
      │
      ▼
Stored BCrypt Hash
      │
      ▼
Authentication Result

Passwords are not exposed through response DTOs.

Authorization

FixFlow uses multiple layers of authorization:

JWT Authentication
        ↓
Role-Based Authorization
        ↓
Ownership / Assignment Authorization
        ↓
Business Rule Validation
        ↓
Operation

This provides defense in depth instead of relying only on endpoint-level role checks.

Role-Based Authorization
TENANT

Tenant-level operations include:

Register account
Login
Create maintenance requests
Update own maintenance requests
View resources related to own requests
Create comments on own requests
View relevant work updates
Rate completed requests
VENDOR

Vendor-level operations include:

Login
View assigned work
Accept assignments
Reject assignments
Add work updates
Add comments on assigned requests
Complete accepted assignments
MANAGER

Manager-level operations include:

Manage properties
Manage units
Review maintenance requests
Manage maintenance requests
Assign vendors
Manage users
Access management-level resources
Perform supported management operations
Ownership & Assignment Authorization

Role authorization alone is not enough.

FixFlow also verifies whether the authenticated user has a valid relationship with the requested resource.

Tenant Ownership
Tenant
   │
   ▼
Maintenance Request
   │
   ▼
Is Tenant the Reporter / Owner?
   │
   ├── NO ──► 403 Forbidden
   │
   ▼ YES
Operation Allowed
Vendor Assignment
Vendor
   │
   ▼
Maintenance Request
   │
   ▼
Does Vendor have Required Assignment?
   │
   ├── NO ──► 403 Forbidden
   │
   ▼ YES
Operation Allowed

This prevents a Vendor from accessing another Vendor's assigned work.

Global Request Processing Pipeline

Every protected request follows the general backend pipeline:

Client
  │
  ▼
HTTP Request
  │
  ▼
JWT Authentication Filter
  │
  ▼
SecurityContext
  │
  ▼
Role Authorization
  │
  ▼
Controller
  │
  ▼
DTO Validation
  │
  ▼
Mapper
  │
  ▼
Service Layer
  │
  ├──────────────► Ownership / Assignment Check
  │
  ├──────────────► Business Rule Check
  │
  ▼
Repository
  │
  ▼
JPA / Hibernate
  │
  ▼
MySQL
  │
  ▼
Response
Validation Flow

Request DTOs are validated before business processing.

HTTP Request
     │
     ▼
@Valid
     │
     ▼
Validation Passed?
   /         \
 NO           YES
 │             │
 ▼             ▼
400          Service
 │
MethodArgumentNotValidException
 │
 ▼
GlobalExceptionHandler

Validation covers constraints such as:

Required fields
Non-blank strings
Required IDs
Positive numeric values
Rating score between 1 and 5
String length constraints
Exception Handling

FixFlow uses centralized exception handling for application-level errors.

400 — Bad Request
Validation Error
Invalid DTO
   ↓
MethodArgumentNotValidException
   ↓
GlobalExceptionHandler
   ↓
400 Bad Request
Business Error
Invalid Business Operation
   ↓
BusinessException
   ↓
GlobalExceptionHandler
   ↓
400 Bad Request

Examples:

Duplicate active assignment
Invalid status transition
Operation on terminal request
Rating before completion
Duplicate rating
Invalid reporter/unit relationship
Invalid vendor assignment
403 — Forbidden

Authorization failures are handled through Spring Security's custom access-denied handler.

Unauthorized Operation
        ↓
CustomAccessDeniedHandler
        ↓
403 Forbidden

Examples:

Insufficient role
Tenant accessing another user's resource
Vendor accessing another Vendor's assignment
Unauthorized comment modification
404 — Not Found
Resource Lookup
      ↓
Resource Does Not Exist
      ↓
ResourceNotFoundException
      ↓
GlobalExceptionHandler
      ↓
404 Not Found
Protection & Failure Paths
┌──────────────────────────────────────────────┐
│              SECURITY / ERRORS               │
└──────────────────────────────────────────────┘

Invalid / Missing JWT
        │
        └──────────► Authentication Failure


Insufficient Role
        │
        └──────────► 403 Forbidden


Wrong Tenant Ownership
        │
        └──────────► 403 Forbidden


Wrong Vendor Assignment
        │
        └──────────► 403 Forbidden


Invalid DTO
        │
        └──────────► 400 Bad Request
                     MethodArgumentNotValidException


Invalid Business Rule
        │
        ├── Duplicate Assignment
        ├── Invalid Status Transition
        ├── Terminal Request Operation
        ├── Rating Before Completion
        └── Duplicate Rating
                │
                └──► 400 Bad Request
                     BusinessException


Resource Does Not Exist
        │
        └──────────► 404 Not Found
                     ResourceNotFoundException
User Registration Flow

Public registration is intentionally restricted to Tenant accounts.

Client
  │
  ▼
POST /api/users
  │
  ▼
Validate Registration DTO
  │
  ▼
Force Role = TENANT
  │
  ▼
BCrypt Password Hashing
  │
  ▼
Save User
  │
  ▼
Registration Successful

The client cannot register themselves as:

MANAGER
VENDOR

This prevents privilege escalation through public registration.

Property & Unit Management

Managers manage properties and units.

Manager
   │
   ├── Create Property
   ├── Update Property
   └── Delete Property

Manager
   │
   ├── Create Unit
   ├── Update Unit
   └── Delete Unit

Relationships:

Property
   │
   └── Unit
        │
        └── User

Maintenance requests are associated with units.

Backend Architecture

FixFlow follows a layered architecture with clear separation of responsibilities.

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
Controller Layer

The Controller layer exposes REST APIs.

Responsibilities:

Handle HTTP requests
Map requests to service methods
Receive request DTOs
Trigger request validation
Return response DTOs
Handle HTTP-level concerns

Controllers do not contain the core business logic.

DTO Layer

DTOs are used to control data entering and leaving the API.

Examples include:

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

DTOs also contain validation constraints such as:

@NotNull
@NotBlank
@Size
@Positive
@Min
@Max
Mapper Layer

The Mapper layer converts between DTOs and entities.

Request DTO
     ↓
   Mapper
     ↓
  Entity

Response flow:

Entity
   ↓
 Mapper
   ↓
Response DTO

This keeps conversion logic separate from Controllers and Services and prevents JPA entities from being directly exposed through the API.

Service Layer

The Service layer contains the core business logic of FixFlow.

Responsibilities include:

Business rules
Authorization checks
Ownership checks
Vendor assignment checks
State transition validation
Duplicate prevention
Relationship validation
Notification creation
Transaction management
Coordination between repositories

For example, assigning a vendor is not simply a database insert:

Vendor Exists?
      ↓
Role = VENDOR?
      ↓
Request Exists?
      ↓
Request Terminal?
      ↓
Duplicate Active Assignment?
      ↓
Create Assignment
      ↓
Update MaintenanceRequest
      ↓
Create Notification
Repository Layer

Repositories handle database access using Spring Data JPA.

Responsibilities include:

Entity persistence
Entity retrieval
Entity updates
Entity deletion
Derived queries
Custom queries where required

Flow:

Service
   ↓
Repository
   ↓
JPA / Hibernate
   ↓
MySQL
Entity Layer

Entities represent the persistent domain model.

Main entities:

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

JPA/Hibernate is used to map these entities to relational database tables.

Security Layer

The security layer handles:

JWT authentication
Password encoding
Authentication context
Role-based authorization
Access-denied handling
Security configuration

The service layer performs additional resource-level authorization such as:

Ownership checks
Vendor assignment checks
Business authorization
Exception Layer

The exception layer provides centralized handling for application-level failures.

Important exceptions include:

ResourceNotFoundException
BusinessException
MethodArgumentNotValidException

Spring Security access-denied situations are handled separately through the custom access-denied handler.

Complete Backend Request / Response Flow
Request
HTTP Request
     ↓
Spring Security
     ↓
Controller
     ↓
Request DTO
     ↓
Validation
     ↓
Mapper
     ↓
Entity
     ↓
Service
     ↓
Repository
     ↓
JPA / Hibernate
     ↓
MySQL
Response
MySQL
   ↓
Repository
   ↓
Entity
   ↓
Mapper
   ↓
Response DTO
   ↓
Controller
   ↓
HTTP Response
   ↓
Client
Data Model

The main domain entities are:

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

Main relationships:

Property
   │
   └── Unit
         │
         └── User


User
 ├── reports ────────► MaintenanceRequest
 ├── vendor ─────────► Assignment
 ├── author ─────────► Comment
 ├── vendor ─────────► WorkUpdate
 ├── receives ───────► Notification
 └── tenant/vendor ──► Rating


MaintenanceRequest
 ├── has ────────────► Assignment
 ├── has ────────────► WorkUpdate
 ├── has ────────────► Comment
 ├── has ────────────► Attachment
 └── has ────────────► Rating
API Overview

The backend exposes REST APIs organized around domain resources.

Resource	Purpose
/auth	Authentication
/api/users	User management
/api/property	Property management
/api/unit	Unit management
/api/maintenanceRequest	Maintenance request management
/api/assignment	Vendor assignments
/api/workUpdate	Work progress updates
/api/comment	Maintenance request comments
/api/attachment	Attachment metadata
/api/notification	Notifications
/api/rating	Vendor ratings

Access to these APIs is protected using Spring Security, HTTP method authorization, role checks, and service-level ownership/assignment validation.

Project Structure
fixFlow/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── maintenance/
│   │   │           └── fixFlow/
│   │   │               │
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── mapper/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               └── service/
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
Package Responsibilities
Package	Responsibility
config	Application and security configuration
controller	REST API endpoints
dto	Request and response DTOs
entity	JPA domain entities
exception	Custom exceptions and global exception handling
mapper	DTO ↔ Entity conversion
repository	Database access through Spring Data JPA
security	JWT authentication and security components
service	Business logic, authorization and workflow rules
Why This Architecture?

The layered structure provides:

Separation of concerns
Cleaner Controllers
Centralized business logic
Reusable Services
Controlled entity exposure
DTO-based API design
Clear database access boundaries
Centralized exception handling
Easier testing and maintenance
Better scalability for future features

A typical feature follows:

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
Tech Stack
Technology	Purpose
Java	Backend programming language
Spring Boot	Backend application framework
Spring Security	Authentication and authorization
JWT	Stateless authentication
BCrypt	Secure password hashing
Spring Data JPA	Data access
Hibernate	ORM / persistence
MySQL	Relational database
Maven	Build and dependency management
Jakarta Validation	Request validation
Postman	REST API testing
Git / GitHub	Version control
API Testing

The backend has been manually tested using Postman.

Major testing areas include:

Authentication
JWT-protected endpoints
Role-based authorization
Ownership authorization
Vendor assignment authorization
Maintenance request lifecycle
Assignment state transitions
Work updates
Comments
Notifications
Attachments
Ratings
Duplicate rating protection
Validation failures
Business-rule violations
Access-denied responses
Resource-not-found handling

Examples:

Tenant accessing another user's resource
        ↓
403 Forbidden
Vendor accessing another Vendor's assignment
        ↓
403 Forbidden
Unauthorized comment modification
        ↓
403 Forbidden
Invalid business operation
        ↓
400 Bad Request
Missing resource
        ↓
404 Not Found
Design Principles
Separation of Concerns

Each layer has a clearly defined responsibility.

DTO-Based API Design

Request and response DTOs are used instead of directly exposing JPA entities.

Layered Architecture
Controller
    ↓
DTO / Mapper
    ↓
Service
    ↓
Repository
    ↓
Database
Defense in Depth
JWT Authentication
        ↓
Role Authorization
        ↓
Ownership / Assignment Authorization
        ↓
Business Rule Validation
Secure Password Storage

Passwords are stored as BCrypt hashes rather than plain text.

Controlled State Transitions

Maintenance requests and assignments use explicit state transitions.

Centralized Error Handling

Application-level errors are handled consistently through centralized exception handling.

Setup & Installation
1. Clone the Repository
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd fixFlow
2. Create the MySQL Database

Create the database configured for your local environment.

Example:

CREATE DATABASE fixflow;
3. Configure Application Properties

Create:

src/main/resources/application.properties

Configure your local:

MySQL URL
Database username
Database password
JWT secret
JWT configuration

Example structure:

spring.datasource.url=jdbc:mysql://localhost:3306/fixflow
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update

jwt.secret=YOUR_SECRET
Important

application.properties contains environment-specific configuration and secrets.

It is intentionally excluded from Git version control.

Do not commit database passwords, JWT secrets, or other sensitive credentials.

4. Run the Application
Windows
.\mvnw.cmd spring-boot:run
Linux / macOS
./mvnw spring-boot:run

The APIs can then be tested using Postman.

Default Development Account

The project currently includes a development DataInitializer that creates a Manager account when one does not already exist.

Email:
manager@fixflow.com

Password:
Manager@123

Role:
MANAGER

This account is intended for local development and testing only.

Production deployments should use a proper account provisioning strategy and should not rely on hardcoded development credentials.

Current Project Scope

FixFlow currently focuses on the backend and REST API layer.

The backend provides the foundation for a property maintenance platform:

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

The APIs can be consumed by a dedicated frontend application for role-specific user interfaces.

Future Improvements

Potential future enhancements include:

Frontend application for Tenant, Vendor and Manager dashboards
Actual file upload and storage for attachments
Swagger / OpenAPI API documentation
Automated unit tests
Integration testing
Production database migration strategy
Production deployment configuration
Improved vendor onboarding and management
Email or real-time notification delivery
Monitoring and observability improvements
Production-ready secret management

