# User Management REST API

Base URL: `http://localhost:8080/api/users`

## Endpoints

### 1. Get All Users
**GET** `/api/users`

Returns a list of all users (without passwords).

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "createdAt": "2026-03-22T02:31:46.408569"
  }
]
```

### 2. Get User by ID
**GET** `/api/users/{id}`

Returns a specific user by ID.

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2026-03-22T02:31:46.408569"
}
```

**Error (404 Not Found):**
```json
{
  "error": "Resource Not Found",
  "message": "User not found with id: 999"
}
```

### 3. Get User by Username
**GET** `/api/users/username/{username}`

Finds a user by username.

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2026-03-22T02:31:46.408569"
}
```

### 4. Get User by Email
**GET** `/api/users/email/{email}`

Finds a user by email address.

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2026-03-22T02:31:46.408569"
}
```

### 5. Create User
**POST** `/api/users`

Creates a new user.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Validation Rules:**
- `username`: required, 3-50 characters
- `email`: required, must be valid email format
- `password`: required, minimum 6 characters

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2026-03-22T02:31:46.408569"
}
```

**Validation Error (400 Bad Request):**
```json
{
  "username": "Username must be between 3 and 50 characters",
  "email": "Email should be valid",
  "password": "Password must be at least 6 characters"
}
```

### 6. Update User
**PUT** `/api/users/{id}`

Updates an existing user.

**Request Body:**
```json
{
  "username": "updated_username",
  "email": "updated@example.com",
  "password": "newpassword123"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "updated_username",
  "email": "updated@example.com",
  "createdAt": "2026-03-22T02:31:46.408569"
}
```

**Error (404 Not Found):**
```json
{
  "error": "Resource Not Found",
  "message": "User not found with id: 999"
}
```

### 7. Delete User
**DELETE** `/api/users/{id}`

Deletes a user.

**Response (204 No Content)**

**Error (404 Not Found):**
```json
{
  "error": "Resource Not Found",
  "message": "User not found with id: 999"
}
```

## Notes

- Password is never returned in API responses (only stored, not exposed)
- `createdAt` is automatically set when the user is created
- All timestamps are in ISO-8601 format
- Duplicate usernames or emails will cause a 500 error (database constraint violation)
- Endpoint paths:
  - `/api/users` - collection operations (GET all, POST)
  - `/api/users/{id}` - individual operations by ID (GET, PUT, DELETE)
  - `/api/users/username/{username}` - lookup by username
  - `/api/users/email/{email}` - lookup by email
