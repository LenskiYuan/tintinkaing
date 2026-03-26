# Spring Boot MySQL CRUD - Current State

> Resume note for future Codex sessions: read `/home/yuan316/playground/tintinkaing/CODEX_RESUME_LOG.md` first for the latest setup status, machine-specific Docker notes, and next steps.

**Date:** 2026-03-26
**Status:** Working in `dev` against MySQL Docker

## Current Project Setup

- Spring Boot 3.2.0
- Spring Web, Validation, Data JPA, Flyway
- MySQL 8.0 in Docker
- Public CRUD APIs
- No Spring Security
- No Keycloak
- No automated tests

## Environment Rule

- Do not commit secret-bearing env files or env-specific Spring config files.
- Use env-specific files consistently:
  - Docker: `docker compose --env-file .env.<env> ...`
  - Spring Boot: source `.env.<env>` and run with the matching Spring profile
- Existing env files:
  - `spring-boot-demo/.env.dev`
  - `spring-boot-demo/.env.qa`
  - `spring-boot-demo/.env.uat`
  - `spring-boot-demo/.env.prod`
- Existing Spring env resources:
  - `spring-boot-demo/src/main/resources/application-dev.yml`
  - `spring-boot-demo/src/main/resources/data-dev.sql`
- Missing for future env rollout:
  - `application-qa.yml`
  - `application-uat.yml`
  - `application-prod.yml`

## Verified Working Path

### Docker

```bash
cd /home/yuan316/playground/tintinkaing/spring-boot-demo
docker compose --env-file .env.dev up -d mysql
docker compose --env-file .env.dev ps
```

### Spring Boot

```bash
cd /home/yuan316/playground/tintinkaing/spring-boot-demo
set -a
source .env.dev
set +a
mvn -Dmaven.repo.local=/home/yuan316/playground/tintinkaing/.m2/repository \
  spring-boot:run -Dspring-boot.run.profiles=dev
```

### API Smoke Check

```bash
curl http://localhost:8080/api/users
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"secret123"}'
```

## Current Schema

- `user`: id, username, email, password, created_at
- `author`: id, first_name, last_name, biography, birth_date, created_at, updated_at
- `book`: id, title, isbn, published_year, publisher, created_at, updated_at
- `book_author`: join table

## Recently Completed

- MySQL Docker image pulls and runs on this machine
- Spring Boot starts successfully with `.env.dev`
- Flyway migrations apply successfully
- CRUD API verified against live MySQL
- Controller error handling improved for books/authors
- `application.yml` added for a sane default local run path

## Next Tasks

1. Add minimal test coverage for core CRUD and Flyway startup.
2. Decide whether to keep tracked `application.yml` as the base config or reduce it further in favor of env-specific config only.
3. Add `application-qa.yml`, `application-uat.yml`, and `application-prod.yml` when those environments are actually implemented.
4. Improve API consistency:
   - clearer duplicate-resource responses
   - pagination for list endpoints
   - better validation for related entities
5. Update `API.md` so it matches current behavior and error responses.
