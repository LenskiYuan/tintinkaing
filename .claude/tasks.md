# Spring Boot MySQL CRUD - Simplified Project

**Date:** 2026-03-23
**Status:** ✅ Complete - App Running

---

## Current Project Setup

### Stack
- Spring Boot 3.2.0 (Spring Web, Validation, Data JPA, Flyway)
- MySQL 8.0
- No security (all endpoints public)
- No Keycloak
- No tests

### Key Components
1. **UserRepository** - JPA repository for User entity
2. **UserService** - Business logic for user operations
3. **UserController** - CRUD endpoints (all public)
4. **Author & Book** - Additional entities with controllers
5. **Flyway Migrations** - V1, V2, V3 applied (schema version 3)
6. **Docker Compose** - MySQL only (Keycloak service removed from compose)

---

## Database Schema

- `user`: id, username, email, password, created_at, keycloak_id (unused)
- `author`: id, first_name, last_name, biography, birth_date, created_at, updated_at
- `book`: id, title, isbn, published_year, publisher, created_at, updated_at
- `book_author`: join table for many-to-many

---

## Working Endpoints

All endpoints are **publicly accessible** (no authentication).

### Users
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get by ID
- `GET /api/users/username/{username}` - Get by username
- `GET /api/users/email/{email}` - Get by email
- `POST /api/users` - Create user (JSON: username, email, password)
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Authors
- `GET /api/authors` - List all
- `GET /api/authors/{id}` - Get by ID
- `POST /api/authors` - Create author
- `PUT /api/authors/{id}` - Update author
- `DELETE /api/authors/{id}` - Delete author

### Books
- `GET /api/books` - List all
- `GET /api/books/{id}` - Get by ID
- `POST /api/books` - Create book (JSON: title, isbn, publishedYear, publisher, authorIds[])
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

---

## Notes

- Application started successfully on port 8080
- MySQL container running via docker-compose
- All CRUD operations tested and working
- `createdAt` set via `@PrePersist` in User entity (like Book and Author)
- `user.keycloak_id` column remains but unused

---

## See Also

- **SIMPLIFICATION_LOG.md** - Detailed log of all changes made, problems resolved, and restart instructions

### Configuration
- Keycloak: `http://localhost:8081/realms/demo-realm`
- App: `http://localhost:8080`
- Database: `demo_db` on MySQL

---

## Tasks to Complete

### Priority 1: Basic Setup Verification
- [ ] Check if Docker containers are running (MySQL and Keycloak)
- [ ] Verify Keycloak is accessible at http://localhost:8081
- [ ] Check if realm import file exists (currently missing)
- [ ] Decide: Use `--import-realm` OR `KeycloakRealmConfiguration` (not both)
- [ ] Verify database connectivity
- [ ] Check Flyway migrations ran successfully

### Priority 2: Testing Keycloak Integration
- [ ] Get admin access token from Keycloak
- [ ] Verify realm exists and has correct configuration
- [ ] Verify client `demo-client` exists with secret
- [ ] Verify client roles (USER, ADMIN) exist
- [ ] Verify test users (admin, user) exist with passwords
- [ ] Test obtaining access tokens with password grant
- [ ] Validate JWT tokens using JWKS endpoint

### Priority 3: Testing REST APIs
- [ ] Start Spring Boot application with dev profile
- [ ] Test unauthenticated requests (should fail)
- [ ] Test with user token (USER role)
  - GET /api/users (should work)
  - POST /api/users (should fail - need ADMIN)
- [ ] Test with admin token (ADMIN role)
  - GET /api/users (should work)
  - POST /api/users (should work)
  - PUT /api/users/{id} (should work)
  - DELETE /api/users/{id} (should work)
- [ ] Verify user sync occurs on first authenticated request
- [ ] Check local DB has synced Keycloak users

### Priority 4: Troubleshooting (Known Issues)
- [ ] **Issue:** Docker compose uses `--import-realm` but no realm file exists
  - **Fix:** Either create realm-export.json OR remove flag OR add realm file
- [ ] **Issue:** SecurityConfig may have role mapping issues
  - **Check:** JWT roles map correctly to `ROLE_USER` and `ROLE_ADMIN`
- [ ] **Issue:** User entity createdAt field initialization
  - **Check:** Ensure `createdAt` is set properly (constructor only, no @PrePersist)
- [ ] **Issue:** Application.yml has commented password line
  - **Check:** Ensure .env.dev is loaded properly

### Priority 5: Improvements
- [ ] Add @PrePersist to User entity for automatic createdAt
- [ ] Add comprehensive logging
- [ ] Add health indicators
- [ ] Create Postman/curl test collection

---

## Test Commands

### Docker
```bash
cd spring-boot-demo
docker-compose --env-file .env.dev up -d
docker-compose ps
docker-compose logs -f
```

### Get Keycloak Admin Token
```bash
curl -X POST 'http://localhost:8081/realms/master/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=admin-cli' \
  -d 'grant_type=password' \
  -d 'username=admin' \
  -d 'password=admin'
```

### Test User Token
```bash
curl -X POST 'http://localhost:8081/realms/demo-realm/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=demo-client' \
  -d 'client_secret=demo-client-secret' \
  -d 'grant_type=password' \
  -d 'username=user' \
  -d 'password=user123'
```

### Test API
```bash
curl -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  http://localhost:8080/api/users
```

---

## Notes

- `mvn spring-boot:run -Dspring-boot.run.profiles=dev` remember to run spring boot app in `dev`
- The `KeycloakRealmConfiguration` runs only in `dev` profile (line 20)
- Docker compose starts both MySQL and Keycloak with health checks
- The `--import-realm` flag in docker-compose line 31 expects realm files in `/opt/keycloak/data/import`
- Bearer-only mode enabled for Keycloak client (line 41 in application.yml)

---

## Questions/Decisions Needed

1. Should we use automatic realm creation (`KeycloakRealmConfiguration`) OR realm import file?
   - Option A: Keep auto-creation (simpler for dev, no import files needed)
   - Option B: Create realm-export.json and use `--import-realm`
   - Option C: Support both (auto-creation fallback)

2. Should we add a `@PrePersist` to set `createdAt` automatically?

---

## Next Steps

Start with: Check Docker container status and Keycloak accessibility.
