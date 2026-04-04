# Finance Dashboard Backend

A Spring Boot REST API for managing financial records with role-based access control.

## Tech Stack
- Java 17, Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA + MySQL 8
- Lombok, Bean Validation

## Setup

### Prerequisites
- Java 17+
- MySQL 8 running locally

### Database
```sql
CREATE DATABASE finance_db;
```

### Environment
Edit `src/main/resources/application.properties`:

### Run
```bash
./mvnw spring-boot:run
```
API starts at `http://localhost:8080`

## Roles
| Role    | Can do |
|---------|--------|
| VIEWER  | Read records and dashboard |
| ANALYST | Read + create + update records |
| ADMIN   | Full access including user management |

## Assumptions
- Register endpoint accepts a role field (for demo; in production only admins assign roles)
- Soft delete used for records — data is never physically removed
- Monthly trend query uses MySQL DATE_FORMAT — swap to FORMATDATETIME for H2
- JWT stored client-side; expiry is 24 hours

## Tradeoffs
- Chose flat role model (not hierarchical) for simplicity and clarity
- Used native query for monthly trend to leverage MySQL DATE_FORMAT
- No rate limiting implemented to keep scope focused on core requirements
