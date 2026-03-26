# Codex Resume Log

**Date:** 2026-03-25
**Workspace:** `/home/yuan316/playground/tintinkaing`

## What I Found

- The repo on disk is the **simplified Spring Boot + MySQL CRUD app**, not the earlier Keycloak-secured version.
- The handoff docs conflict:
  - `.claude/tasks.md` mixes simplified-project notes with stale Keycloak tasks.
  - `log.txt` and `log-old.txt` describe a previous Keycloak validation pass.
  - `SIMPLIFICATION_LOG.md` matches the codebase more closely.
- Current code has:
  - Public CRUD endpoints for users, authors, books
  - No Spring Security / OAuth dependencies
  - Leftover Keycloak artifacts still present in Docker/config/schema

## What I Did

### Toolchain

- Verified Java was initially missing.
- Installed and verified:
  - Java 21
  - Maven 3.8.7
  - `curl`
  - `jq`
  - `netcat-openbsd`

### Maven / Build

- Maven initially failed because it tried to use `/home/yuan316/.m2/repository`, which was not available in this environment.
- Created a workspace-local Maven repo:
  - `.m2/repository`
- Built successfully with:

```bash
mvn -q -Dmaven.repo.local=/home/yuan316/playground/tintinkaing/.m2/repository -DskipTests package
```

- Successful artifact:
  - `spring-boot-demo/target/demo-0.0.1-SNAPSHOT.jar`

### Docker

- Installed and verified:
  - Docker Engine `28.2.2`
  - Docker Compose `2.37.1`
- Started the Docker service.
- Added user `yuan316` to the `docker` group.

### Important Docker Note

- Group membership change is applied on the system, but the current shell may not see it until a new login shell is opened.
- To resume Docker work later, run one of:

```bash
newgrp docker
```

or open a new terminal/session.

Then verify:

```bash
docker info
docker compose version
```

## Current Technical State

### Build Status

- The project **packages successfully**.
- I have **not** completed a full application run against MySQL yet.

### Config Gaps

- `spring-boot-demo/src/main/resources/` only contains:
  - `application-example.yml`
- There is **no active** `application.yml` or `application-dev.yml` in the repo right now.
- This means startup from a clean checkout is still not properly configured.

### Known Code / Design Issues

1. **Stale documentation**
   - `.claude/tasks.md` is not reliable as-is.
   - It says “No Keycloak” near the top but still contains Keycloak testing tasks below.

2. **Leftover Keycloak artifacts**
   - `spring-boot-demo/docker-compose.yml` still defines a `keycloak` service.
   - `spring-boot-demo/keycloak/realm-dev.json` still exists.
   - `User` still has `keycloakId`.
   - Flyway migration `V2__add_keycloak_id_to_user.sql` still exists.

3. **API correctness bugs**
   - `BookController` duplicate-ISBN update flow can return the wrong status because it returns `null` inside `Optional.map`.
   - `AuthorController` currently treats duplicate last name as a bad request, which is not a sound uniqueness rule.
   - Global exception handling is too generic for DB constraint violations.

4. **No tests**
   - The repo currently has no test suite.

## What To Do Next

### Priority 1: Make Local Run Path Clean

1. Open a fresh shell or run `newgrp docker`.
2. Verify Docker daemon access with `docker info`.
3. Start MySQL container for the project.
4. Add real Spring config:
   - likely `application.yml` or `application-dev.yml`
   - wire datasource and environment handling cleanly
5. Start the Spring app and verify DB connectivity.

### Priority 2: Stabilize App Behavior

1. Fix `BookController` duplicate ISBN handling.
2. Fix `AuthorController` duplicate-last-name behavior.
3. Improve exception handling for validation / DB constraint errors.
4. Verify CRUD endpoints against the running MySQL-backed app.

### Priority 3: Clean Up Project Drift

1. Update `.claude/tasks.md` so it matches the actual simplified app.
2. Decide whether Keycloak is truly gone or intentionally coming back.
3. If Keycloak is gone:
   - remove `keycloak` service from Docker compose
   - remove `keycloak/realm-dev.json`
   - remove `keycloakId` from entity / schema via new Flyway migration

## Useful Commands For Resuming

### Verify toolchain

```bash
java -version
javac -version
mvn -version
docker --version
docker compose version
```

### Use Docker in a fresh shell

```bash
newgrp docker
docker info
```

### Build with workspace-local Maven cache

```bash
cd /home/yuan316/playground/tintinkaing/spring-boot-demo
mvn -q -Dmaven.repo.local=/home/yuan316/playground/tintinkaing/.m2/repository -DskipTests package
```

## Files Worth Reading First Next Time

- `/home/yuan316/playground/tintinkaing/CODEX_RESUME_LOG.md`
- `/home/yuan316/playground/tintinkaing/.claude/tasks.md`
- `/home/yuan316/playground/tintinkaing/SIMPLIFICATION_LOG.md`
- `/home/yuan316/playground/tintinkaing/spring-boot-demo/docker-compose.yml`
- `/home/yuan316/playground/tintinkaing/spring-boot-demo/pom.xml`

---

## Update: 2026-03-25 20:50 EDT

### Additional Work Completed

- Added a real tracked Spring config file:
  - `spring-boot-demo/src/main/resources/application.yml`
- Updated Docker Compose for a cleaner local run path:
  - added a default MySQL root password fallback
  - removed the mount that executed Flyway SQL as MySQL init scripts
- Fixed API behavior:
  - `AuthorController` now uses `ResourceNotFoundException` consistently for missing authors
  - removed the duplicate-last-name rejection on author creation
  - `BookController` now returns a proper bad-request path for duplicate ISBN instead of falling through to an incorrect 404
  - missing book deletes / lookups now use `ResourceNotFoundException`
  - `GlobalExceptionHandler` now handles `IllegalArgumentException` as `400`
  - `GlobalExceptionHandler` now handles `DataIntegrityViolationException` as `409`
- Rebuilt successfully again with:

```bash
mvn -q -Dmaven.repo.local=/home/yuan316/playground/tintinkaing/.m2/repository -DskipTests package
```

### What Is Still Blocked

- I still have **not** completed a full MySQL-backed app run.
- Docker is installed and the service was started earlier, but this Codex sandbox still cannot directly validate daemon access after the group change.
- Attempts to use `sg docker` in this environment fail with:
  - `Cannot open audit interface - aborting.`

### Immediate Next Step

In a fresh user shell outside this constrained session, run:

```bash
newgrp docker
cd /home/yuan316/playground/tintinkaing/spring-boot-demo
docker info
docker compose up -d mysql
```

Then the next Codex task should be:

1. verify MySQL is healthy
2. start the Spring Boot app with the new `application.yml`
3. exercise CRUD endpoints against the running DB
4. update `.claude/tasks.md` to remove stale Keycloak sections

### Environment File Rule

- The repo does contain environment-specific secret files:
  - `spring-boot-demo/.env.dev`
  - `spring-boot-demo/.env.qa`
  - `spring-boot-demo/.env.uat`
  - `spring-boot-demo/.env.prod`
- The repo also contains current dev-specific Spring resources:
  - `spring-boot-demo/src/main/resources/application-dev.yml`
  - `spring-boot-demo/src/main/resources/data-dev.sql`
- When running this project in the future, use env-specific files consistently:
  - Docker: `docker compose --env-file .env.<env> ...`
  - Spring Boot: export/source `.env.<env>` and run with the matching Spring profile
- QA / UAT / Prod env files exist already, but matching `application-qa.yml`, `application-uat.yml`, and `application-prod.yml` do not exist yet and should be added when those environments are implemented.

### Runtime Verification Completed

- MySQL now runs successfully from Docker with the dev env file:

```bash
docker compose --env-file /home/yuan316/playground/tintinkaing/spring-boot-demo/.env.dev \
  -f /home/yuan316/playground/tintinkaing/spring-boot-demo/docker-compose.yml \
  up -d mysql
```

- Verified MySQL container became healthy.
- Spring Boot now starts successfully against that MySQL container with the dev profile:

```bash
cd /home/yuan316/playground/tintinkaing/spring-boot-demo
set -a
source .env.dev
set +a
mvn -Dmaven.repo.local=/home/yuan316/playground/tintinkaing/.m2/repository \
  spring-boot:run -Dspring-boot.run.profiles=dev
```

- Verified live API against the running app:
  - `POST /api/users` created a user in MySQL
  - `GET /api/users` returned the persisted user

### Machine-Specific Docker Fix

- Docker initially could not pull from Docker Hub because `dockerd` failed DNS lookups against the WSL resolver `10.255.255.254`.
- Two host-level fixes were applied:
  - `/etc/docker/daemon.json`
    - `{"dns": ["8.8.8.8", "1.1.1.1"]}`
  - systemd override for Docker:
    - `/etc/systemd/system/docker.service.d/override.conf`
    - `Environment=GODEBUG=netdns=cgo`
- These changes were necessary to make Docker image pulls work on this machine.
