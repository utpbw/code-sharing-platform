# Code Sharing Platform

A Spring Boot REST + web application for sharing code snippets with optional time and view-count restrictions.

## Features

- Store and retrieve code snippets via both a JSON API and browser-friendly HTML pages
- Snippets are identified by **UUID**, persisted in an H2 file database, and survive server restarts
- Syntax highlighting via [highlight.js](https://highlightjs.org/)
- Two access restrictions (either or both can be applied per snippet):
  - **Time limit** — snippet expires N seconds after creation
  - **View limit** — snippet expires after N views
- Restricted snippets are excluded from the "latest" feed; they are accessible only via a direct link

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 2.x |
| Persistence | Spring Data JPA + H2 (file mode) |
| Build | Gradle (Java 11 target) |

## API Reference

### Create a snippet

```
POST /api/code/new
Content-Type: application/json

{
  "code":  "...",
  "time":  0,      // seconds until expiry; 0 = no limit
  "views": 0       // max views; 0 = no limit
}
```

Response: `{ "id": "<uuid>" }`

### Get a snippet (JSON)

```
GET /api/code/{uuid}
```

Response:
```json
{
  "code":  "...",
  "date":  "2024/01/01 12:00:00",
  "time":  95,   // remaining seconds (0 if no limit)
  "views": 4     // remaining views after this access (0 if no limit)
}
```

Returns `404` if the snippet does not exist or a restriction has been triggered.

### Latest snippets (JSON)

```
GET /api/code/latest
```

Returns a JSON array of up to 10 most recent **unrestricted** snippets, newest first.  
Each object has the same shape as the single-snippet response (`time` and `views` are both `0`).

### Web UI

| URL | Description |
|---|---|
| `GET /code/new` | Form to create a new snippet |
| `GET /code/{uuid}` | View a snippet (HTML, with highlight.js) |
| `GET /code/latest` | List of 10 most recent unrestricted snippets |

## Running Locally

```bash
./gradlew :Code-sharing_Platform-task:bootRun
```

The server starts on port **8889** (configured in `application.properties`).  
The H2 database file is created at `../snippets.mv.db` relative to the working directory.

## Running Tests

```bash
./gradlew :Code-sharing_Platform-task:test
```

## Project Structure

```
Code-sharing Platform/task/src/
├── platform/
│   ├── CodeSharingPlatform.java     # Spring Boot entry point
│   ├── controller/Controller.java   # All HTTP endpoints
│   ├── model/Code.java              # JPA entity (UUID id, code, date, timeLimit, viewsLimit, createdAt)
│   └── repository/CodeRepository.java
└── resources/
    └── application.properties       # Port, H2 datasource, JPA config
```
