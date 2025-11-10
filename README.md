# URL Shortener API

A production-ready URL shortener service similar to Bitly or TinyURL.

## Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/url-shortener-api.git
cd url-shortener-api
```

### 2. Setup PostgreSQL Database

**Option A: Local PostgreSQL**

Create a PostgreSQL database:
```bash
createdb url_shortener
```

Or using psql:
```sql
CREATE DATABASE url_shortener;
```

**Option B: Docker PostgreSQL (If you don't have PostgreSQL installed)**

```bash
docker-compose up -d
```

This will start PostgreSQL container on `localhost:5432` with:
- Database: `url_shortener`
- Username: `postgres`
- Password: `postgres`

**To stop Docker PostgreSQL:**
```bash
docker-compose down
```

### 3. Configure Application Properties (Optional)

Default values in `src/main/resources/application.properties`:
```properties
# Database (default: localhost)
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/url_shortener
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# JWT Secret
jwt.secret=KrungSriConsumer_PassedAllTests_JWT_Secret_Key_Production_Ready
jwt.expiration=3600000
```

**Override defaults with environment variables:**
```bash
export DB_HOST=your_db_host
export DB_USER=your_db_user
export DB_PASSWORD=your_db_password
mvn spring-boot:run
```

**Or with Docker:**
```bash
docker run -e DB_HOST=host.docker.internal url-shortener-api:latest
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### 1. User Registration

**Endpoint**: `POST /api/register`

**Request**:
```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securepassword123"
  }'
```

**Response** (201 Created):
```json
{
  "id": 1,
  "email": "user@example.com",
  "createdAt": "2025-11-10T10:30:00"
}
```

### 2. User Login

**Endpoint**: `POST /api/login`

**Request**:
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securepassword123"
  }'
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com"
}
```

**Save the token** for authenticated requests.

### 3. Shorten URL (Authenticated)

**Endpoint**: `POST /api/shorten`

**Request**:
```bash
TOKEN="your_jwt_token_here"

curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "original_url": "https://www.example.com/some/very/long/url/that/needs/shortening"
  }'
```

**Response** (201 Created):
```json
{
  "short_url": "http://localhost:8080/r/aBc123"
}
```

### 4. Redirect to Original URL

**Endpoint**: `GET /api/r/{shortCode}`

**Request**:
```bash
curl -L http://localhost:8080/api/r/aBc123
```

**Response**: Redirects (301) to the original URL

### 5. Get All User's URLs (Authenticated)

**Endpoint**: `GET /api/urls`

**Request**:
```bash
TOKEN="your_jwt_token_here"

curl -X GET http://localhost:8080/api/urls \
  -H "Authorization: Bearer $TOKEN"
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "shortCode": "aBc123",
    "originalUrl": "https://www.example.com/some/very/long/url",
    "createdAt": "2025-11-10T10:35:00",
    "active": true
  }
]
```

### 6. Delete a Short URL (Authenticated)

**Endpoint**: `DELETE /api/urls/{id}`

**Request**:
```bash
TOKEN="your_jwt_token_here"

curl -X DELETE http://localhost:8080/api/urls/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Response** (204 No Content)

## Docker Setup

### Build Docker Image
```bash
docker build -t url-shortener-api:latest .
```

### Run Docker Container (with local PostgreSQL)
```bash
docker run -d -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  --name url-shortener-api \
  url-shortener-api:latest
```

### Access API from Docker
```bash
curl http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

### Stop Docker Container
```bash
docker stop url-shortener-api
docker rm url-shortener-api
```


