# Project Simplification Log - Spring Boot + MySQL Only

**Date:** 2026-03-23
**Goal:** Remove Keycloak OAuth2 integration and all test code, focusing on a simple Spring Boot + MySQL CRUD application.

---

## Original State

The project was a Spring Boot 3.2.0 application with:
- Keycloak 26.0.5 OAuth2 resource server integration
- Spring Security with role-based access control (@PreAuthorize)
- User, Author, Book entities with JPA
- Flyway migrations (V1, V2, V3)
- Keycloak admin client for auto-creating realm/client/users in dev profile
- Full test suite (5 test files)
- Docker Compose with MySQL + Keycloak

**Issues Found:**
1. Docker containers running without .env.dev - Keycloak unhealthy due to missing MYSQL_ROOT_PASSWORD
2. User.createdAt not set properly for synced Keycloak users (empty constructor issue)
3. Test code considered "bad" by user
4. Overly complex for basic CRUD needs

---

## Changes Made

### 1. POM Dependencies Removed
**File:** `spring-boot-demo/pom.xml`

Removed:
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `keycloak-admin-client` (version 26.0.5)
- `spring-security-test` (test scope)
- `spring-boot-starter-test` (entire dependency)
- `maven-surefire-plugin` configuration

Kept:
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `flyway-core` + `flyway-mysql`
- `mysql-connector-j`

### 2. Keycloak Files Deleted
- `src/main/java/com/example/demo/config/SecurityConfig.java`
- `src/main/java/com/example/demo/service/KeycloakRealmConfiguration.java`
- `src/main/java/com/example/demo/config/UserSyncListener.java`
- `src/main/java/com/example/demo/service/UserSyncService.java`

### 3. Code Cleanup

**UserRepository.java** (`src/main/java/com/example/demo/repository/UserRepository.java`)
- Removed: `Optional<User> findByKeycloakId(String keycloakId);`

**UserController.java**
- Removed all `@PreAuthorize` annotations
- Removed import: `import org.springframework.security.access.prepost.PreAuthorize;`
- All endpoints now publicly accessible

**BookController.java**
- Removed all `@PreAuthorize` annotations
- Removed import: `import org.springframework.security.access.prepost.PreAuthorize;`

**AuthorController.java**
- Removed all `@PreAuthorize` annotations on POST, PUT, DELETE
- Removed import: `import org.springframework.security.access.prepost.PreAuthorize;`

**User.java** (`src/main/java/com/example/demo/entity/User.java`)
- Removed `createdAt` setting from constructor
- Added `@PrePersist` method:
  ```java
  @PrePersist
  protected void onCreate() {
      this.createdAt = LocalDateTime.now();
  }
  ```
- This matches the pattern already used in Book and Author entities

### 4. Configuration Files Simplified

**application-dev.yml** (`src/main/resources/application-dev.yml`)
- Removed entire `spring.security.oauth2.resourceserver.jwt` section
- Removed entire `keycloak:` section
- Password now comes from env: `password: ${MYSQL_ROOT_PASSWORD}`

**application-example.yml** (`src/main/resources/application-example.yml`)
- Removed `spring.security.oauth2.resourceserver.jwt` section
- Removed `keycloak:` section

### 5. Test Files Deleted
- Entire `src/test/` directory removed (5 test files)

### 6. Optional Files Cleaned Up
- Deleted `keycloak/` directory (contained realm-dev.json)

---

## Problems Resolved

1. **Compilation Errors After Dependency Removal**
   - **Issue:** Even after deleting files, Maven was still trying to compile test sources
   - **Fix:** Completely removed `src/test/` directory (first attempt didn't delete it properly)

2. **Manual DB Password Setup**
   - **Issue:** Docker-compose was using `--env-file .env.dev` but wasn't set initially
   - **Fix:** Started MySQL container explicitly with `docker-compose --env-file .env.dev up -d mysql`

3. **User.createdAt Null for Synced Users**
   - **Issue:** User entity's `createdAt` was only set in parameterized constructor, but synced users used empty constructor
   - **Fix:** Added `@PrePersist` annotation to automatically set timestamp on persist (matches Book/Author pattern)

4. **Persistent @PreAuthorize Errors**
   - **Issue:** UserController still had `@PreAuthorize` annotations after first edit pass
   - **Fix:** Used `replace_all` to remove all instances of `@PreAuthorize("...")` from UserController

---

## Current State

✅ **Application successfully running** on `http://localhost:8080`
✅ **MySQL 8.0** connected on port 3306
✅ **Flyway migrations** applied (V1, V2, V3 - schema version 3)
✅ **All endpoints public** - no authentication required
✅ **All CRUD operations tested** and working:

```
GET    /api/users           - List all users
GET    /api/users/{id}      - Get user by ID
GET    /api/users/username/{username} - Lookup by username
POST   /api/users           - Create user (JSON: username, email, password)
PUT    /api/users/{id}      - Update user
DELETE /api/users/{id}      - Delete user

GET    /api/authors         - List all authors
POST   /api/authors         - Create author
PUT    /api/authors/{id}    - Update author
DELETE /api/authors/{id}    - Delete author

GET    /api/books           - List all books
POST   /api/books           - Create book (with authorIds)
PUT    /api/books/{id}      - Update book
DELETE /api/books/{id}      - Delete book
```

**Database Schema:**
- `user` table: id, username, email, password, created_at, keycloak_id (unused)
- `author` table: id, first_name, last_name, biography, birth_date, created_at, updated_at
- `book` table: id, title, isbn, published_year, publisher, created_at, updated_at
- `book_author` join table for many-to-many relationship

---

## What Should Be Done Next (If Any)

1. **Optional: Clean up unused keycloak_id column**
   - The `user.keycloak_id` column remains from V2 migration but is unused
   - Create new Flyway migration to drop it if desired
   - Command: `V4__drop_keycloak_id_from_user.sql` with:
     ```sql
     ALTER TABLE user DROP COLUMN keycloak_id;
     ```

2. **Optional: Remove data-dev.sql**
   - The file `src/main/resources/data-dev.sql` contains sample admin user
   - Can be kept or removed (not used by Flyway - it's for manual testing)

3. **Optional: Add proper health checks**
   - Basic `/actuator/health` endpoint if needed for monitoring
   - Add `spring-boot-starter-actuator` dependency

4. **If Keycloak needed in future**:
   - Re-add dependencies: `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`, `keycloak-admin-client`
   - Restore deleted config files from git history
   - Re-add test dependencies and test files
   - Ensure `@PreAuthorize` annotations are properly imported
   - Fix UserSyncService to properly set `createdAt` or use `@PrePersist`

5. **Future Development**:
   - Add pagination to list endpoints
   - Add validation for ISBN uniqueness (Book already has check)
   - Add proper error handling (some 404s work, but could be more consistent)
   - Consider adding service layer business logic if needed

---

## Important Notes

- The app is running in **dev profile** with MySQL on localhost
- **No authentication** - all endpoints are publicly accessible
- `keycloak_id` column remains but unused (decision: leave as-is)
- `createdAt` for User entity now set automatically via `@PrePersist`
- **All test code removed** as requested
- Docker Compose still has Keycloak service defined but it's not running (MySQL only)

---

## Verification Commands

To resume work later:

```bash
cd /Users/lunchungyuan/playground/tintinkaing/spring-boot-demo

# Check MySQL is running
docker-compose --env-file .env.dev ps mysql

# Start the app
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Test APIs
curl http://localhost:8080/api/users
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"username":"test","email":"test@test.com","password":"pass123"}'
```

---

**Status:** ✅ Complete - Spring Boot + MySQL CRUD app working without Keycloak or tests
