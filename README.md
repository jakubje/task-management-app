# Task Management App

A reactive REST API for managing tasks and sub-tasks, built with Java, Spring Boot, and MongoDB.

---

## Tech Stack

- **Java 17**
- **Spring Boot** with **Reactive Spring (WebFlux)**
- **ReactiveMongoTemplate** for database interaction
- **MongoDB 7.0**
- **Docker & Docker Compose**

---

## Getting Started

### Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop) installed and running
- [Java 17+](https://adoptium.net/) (for running the app locally without Docker)

### Run with Docker Compose

Clone the repository and start the services:

```bash
git clone <your-repo-url>
cd <your-repo-name>
docker compose up -d
```

This will spin up:
- **MongoDB** on port `27017`
- **The Spring Boot app** on port `8080`

To stop the services:

```bash
docker compose down
```

To stop and remove the database volume (full reset):

```bash
docker compose down -v
```

### Run MongoDB Only (for local development)

If you want to run the app locally via your IDE and only need the database containerised:

```bash
docker compose up mongodb -d
```

Then run the Spring Boot app from your IDE or via:

```bash
./mvnw spring-boot:run
```

---

## Configuration

The app connects to MongoDB using the following settings in `application.properties`:

```properties
spring.application.name=task-management-api
spring.mongodb.uri=${MONGODB_URI:mongodb://taskuser:taskpassword@localhost:27017/taskdb?authSource=taskdb}
spring.jackson.default-property-inclusion=NON_NULL
```

When running via Docker Compose, the app container uses the internal service name:

```
mongodb://taskuser:taskpassword@mongodb:27017/taskdb?authSource=taskdb
```

---

## Database

MongoDB is initialised automatically on first startup via `mongo-init.js`, which:

- Creates the `taskdb` database
- Creates a `taskuser` with `readWrite` access
- Creates the `tasks` collection with schema validation enforcing `TODO`, `IN_PROGRESS`, and `DONE` statuses

### Viewing the Database

Use [MongoDB Compass](https://www.mongodb.com/try/download/compass) to inspect the database visually (this is the tool used during evaluation).

**Connection string:**
```
mongodb://taskuser:taskpassword@localhost:27017/taskdb?authSource=taskdb
```

> **Note:** `mongo-init.js` only runs on the first startup when the volume is empty. To re-run it, tear down the volume with `docker compose down -v` then start again.

---

## Document Structure

Tasks are stored as MongoDB documents with support for nested sub-tasks:

```json
{
  "_id": "ObjectId",
  "title": "Task Title",
  "description": "Task Description",
  "status": "TODO",
  "subTasks": [
    {
      "title": "Sub-Task Title",
      "description": "Sub-Task Description",
      "status": "IN_PROGRESS",
      "subTasks": [
        {
          "title": "Nested Sub-Task Title",
          "description": "Nested Sub-Task Description",
          "status": "DONE"
        }
      ]
    }
  ]
}
```

**Valid status values:** `TODO`, `IN_PROGRESS`, `DONE`

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/tasks` | Create a new task |
| `GET` | `/tasks` | Get all tasks |
| `GET` | `/tasks/{id}` | Get a task by ID |
| `GET` | `/tasks?status={status}` | Filter tasks by status |
| `PUT` | `/tasks/{id}` | Update a task |
| `DELETE` | `/tasks/{id}` | Delete a task |
| `DELETE` | `/tasks/{id}/subtasks/{subtaskIndex}` | Delete a specific sub-task |

### Example: Create a Task

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Task",
    "description": "This is a task",
    "status": "TODO",
    "subTasks": []
  }'
```

### Example: Filter by Status

```bash
curl http://localhost:8080/tasks?status=IN_PROGRESS
```

> The API can also be tested using **Postman** or **Bruno**.

---

## Testing

The project includes both unit and integration tests, with a minimum of one integration test per endpoint covering error cases.

Run all tests:

```bash
./mvnw test
```