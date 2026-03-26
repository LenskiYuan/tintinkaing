# Spring Boot CRUD API

Base URL: `http://localhost:8080`

All endpoints are currently public. The app is intended to run with env-specific files, for example:

```bash
cd /home/yuan316/playground/tintinkaing/spring-boot-demo
docker compose --env-file .env.dev up -d mysql
set -a
source .env.dev
set +a
mvn -Dmaven.repo.local=/home/yuan316/playground/tintinkaing/.m2/repository \
  spring-boot:run -Dspring-boot.run.profiles=dev
```

## Users

Base path: `/api/users`

### Endpoints

- `GET /api/users`
- `GET /api/users/{id}`
- `GET /api/users/username/{username}`
- `GET /api/users/email/{email}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### Create / Update Payload

```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

### Validation

- `username`: required, 3-50 characters
- `email`: required, valid email
- `password`: required, minimum 6 characters

### Success Response Example

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2026-03-26T00:13:42.954341188"
}
```

## Authors

Base path: `/api/authors`

### Endpoints

- `GET /api/authors`
- `GET /api/authors/{id}`
- `POST /api/authors`
- `PUT /api/authors/{id}`
- `DELETE /api/authors/{id}`

### Create / Update Payload

```json
{
  "firstName": "Harper",
  "lastName": "Lee",
  "biography": "American novelist",
  "birthDate": "1926-04-28"
}
```

## Books

Base path: `/api/books`

### Endpoints

- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

### Create / Update Payload

```json
{
  "title": "Example Book",
  "isbn": "1234567890123",
  "publishedYear": 2024,
  "publisher": "Example Press",
  "authorIds": [1, 2]
}
```

### Book Response Example

```json
{
  "id": 1,
  "title": "Example Book",
  "isbn": "1234567890123",
  "publishedYear": 2024,
  "publisher": "Example Press",
  "authorNames": ["Harper Lee"],
  "createdAt": "2026-03-26T00:20:00",
  "updatedAt": "2026-03-26T00:20:00"
}
```

## Error Responses

### Validation Error: `400 Bad Request`

```json
{
  "username": "Username must be between 3 and 50 characters",
  "email": "Email should be valid"
}
```

### Resource Not Found: `404 Not Found`

```json
{
  "error": "Resource Not Found",
  "message": "User not found with id: 999"
}
```

### Bad Request: `400 Bad Request`

Used for controller-level validation such as duplicate ISBN checks.

```json
{
  "error": "Bad Request",
  "message": "Book already exists with ISBN: 1234567890123"
}
```

### Data Conflict: `409 Conflict`

Used for database-level uniqueness conflicts, for example duplicate usernames or emails.

```json
{
  "error": "Conflict",
  "message": "The request conflicts with existing data."
}
```

## Notes

- Passwords are never returned in API responses.
- `createdAt` is set automatically when a user is created.
- Flyway manages the schema; the app should be started after the MySQL container is up.
- Current schema version is expected to be `v4`.
