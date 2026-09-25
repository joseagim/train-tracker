# TrainTracker API

**Live API: [train-tracker-api-w194.onrender.com](https://train-tracker-api-w194.onrender.com)** · **Swagger UI: [/swagger-ui.html](https://train-tracker-api-w194.onrender.com/swagger-ui.html)** · **Live frontend: [train-tracker-frontend.vercel.app](https://train-tracker-frontend.vercel.app)**

TrainTracker is a train ticket search & booking REST API built as a **portfolio project**, with a focus on the backend engineering itself rather than on the surrounding UI. All stations, schedules and tickets are **fictional demo data** — this is not a real train service.

> The fastest way to try the whole project is the **[live frontend](https://train-tracker-frontend.vercel.app)** — see [Demo accounts](#demo-accounts) for credentials and [A note on the API waking up](#a-note-on-the-api-waking-up) before you click through. If you'd rather explore the API directly, use [Swagger UI](https://train-tracker-api-w194.onrender.com/swagger-ui.html) instead.

The goal of the project was to build a production-shaped backend from scratch and go deep on the two pieces that usually separate a junior CRUD exercise from something closer to a real system: **authentication/authorization done by hand**, and **real concurrency control**, backed by a test that proves it.

- **Backend (this repo):** Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA, PostgreSQL, Flyway. Containerized with Docker and deployed on Render's free tier, connecting to a PostgreSQL database hosted on [Neon](https://neon.tech)'s free tier.
- **Frontend:** [train-tracker-frontend](https://github.com/joseagim/train-tracker-frontend) — React, Vite, Tailwind CSS. Deployed on Vercel: **[train-tracker-frontend.vercel.app](https://train-tracker-frontend.vercel.app)**.

## Table of contents

- [Features](#features)
- [Demo accounts](#demo-accounts)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Running locally](#running-locally)
- [Testing](#testing)
- [API documentation](#api-documentation)
- [Environment variables](#environment-variables)
- [A note on the API waking up](#a-note-on-the-api-waking-up)
- [Related repository](#related-repository)

## Features

- **JWT authentication from scratch**: registration with BCrypt password hashing, login issuing a signed JWT, a custom `OncePerRequestFilter` that validates the token on every request and populates Spring Security's context, and role-based authorization (`ROLE_USER` / `ROLE_ADMIN`) enforced per endpoint.
- **Trip search algorithm** (`GET /api/trips/search`): validates that the requested origin actually comes before the destination along the trip's route, computes the *real* departure/arrival time at the stations the passenger actually requested (not just the route's absolute start time), filters out trips with no seats left, and excludes trips that have already departed — using an injectable `Clock` (fixed to `Europe/Madrid`) so "now" is both timezone-safe and fully unit-testable.
- **Dynamic pricing**: ticket price is computed from the distance between the requested stations, adjusted by time-of-day and day-of-week factors (cheaper early morning, pricier daytime and weekends) — the same calculation is reused for both search results and the actual purchase, so the two can never drift apart.
- **Concurrency-safe ticket purchase**: seat assignment and stock update happen inside a single transaction protected by optimistic locking (`@Version`), so two simultaneous purchases for the last seat on a trip can never both succeed. This is verified with a real multi-threaded integration test — see [Testing](#testing).
- **QR ticket validation (admin only)**: each ticket has a UUID that a frontend can render as a QR code; a dedicated pair of endpoints lets an authenticated admin look up a ticket by that UUID and mark it as scanned, with a `scanned` flag preventing the same code from being validated twice.
- **Full CRUD for the admin-managed catalog** (stations, trains, routes with nested, ordered stops, trips), paginated and documented in Swagger — public read access on stations so a search UI doesn't need a login just to populate a dropdown.
- **Centralized, consistent error handling**: a single `@RestControllerAdvice` maps every exception (not found, validation failure, duplicate resource, no seats available, optimistic lock conflict, anything unexpected) to the same JSON error shape and the correct HTTP status.
- **Realistic seed data**: on first boot, the app seeds itself with real Spanish AVE routes (Madrid–Barcelona, Madrid–Valencia, Madrid–Sevilla, Madrid–Santiago, Madrid–Granada, Barcelona–Sevilla, and two Madrid–Murcia variants), each with round trips and randomized but realistic departure times across a rolling 30-day window.
- **Versioned schema**: the database schema is managed with Flyway migrations, not `ddl-auto`, so the exact same schema is reproducible in development, in CI and in production.

## Demo accounts

Two accounts already exist on the deployed API so you don't need to register before trying anything:

| Role  | Email                    | Password    |
| ----- | ------------------------ | ----------- |
| Admin | `admin@traintracker.com` | `admin1234` |
| User  | `user@traintracker.com`  | `user1234`  |

Only the admin account can access the station/train/route management endpoints and the QR validation endpoints.

## Tech stack

- **Java 21**, **Spring Boot** (Web, Data JPA, Security, Validation)
- **PostgreSQL**, with **Flyway** for schema migrations
- **JJWT** for JSON Web Tokens (`HS256`)
- **springdoc-openapi** for Swagger UI / OpenAPI docs
- **JUnit 5** + **Mockito** for testing
- **Docker** (multi-stage build) + **Docker Compose** for local development
- Deployed on **Render** (Docker web service), database on **Neon** (serverless Postgres)

## Architecture

A layered monolith, organized by responsibility rather than by feature:

```
entity/       JPA entities — no business logic
repository/   Spring Data JPA repositories
service/      Business logic (CRUD services + trip search + ticket purchase)
controller/   REST endpoints — thin, delegate to services
dto/          Request/response DTOs, decoupled from entities
security/     JWT generation/validation, the auth filter, Spring Security config
exception/    Custom exceptions + the global exception handler
config/       Cross-cutting configuration (Swagger, password encoder, seed data, clock)
```

Response DTOs build themselves from entities via static `from(...)` factory methods, composing smaller DTOs where relevant (e.g. a route's response embeds its stations, which embed their station data) instead of duplicating field mappings across services.

## Running locally

### Prerequisites

- [Docker](https://www.docker.com/) and Docker Compose
- Java 21 (only needed if you want to run the app outside Docker, e.g. from an IDE)

### 1. Clone the repository

```
git clone https://github.com/joseagim/train-tracker.git
cd train-tracker/traintracker
```

### 2. Create a `.env` file

It's gitignored, so it won't exist after cloning. Generate a random JWT secret (PowerShell example):

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

```
JWT_KEY=paste-the-generated-key-here
```

### 3. Start everything with Docker Compose

```
docker compose up --build
```

This builds the API image, starts a PostgreSQL container alongside it, applies the Flyway migrations, and seeds the database with demo data on first boot. The API will be available at `http://localhost:8080`, with Swagger UI at `http://localhost:8080/swagger-ui.html`.

Subsequent runs (with no code changes) don't need `--build`:

```
docker compose up -d
```

## Testing

The project follows a TDD approach for the two pieces of logic that most benefit from it: the trip search algorithm and the concurrent ticket purchase.

**Unit tests** (JUnit 5 + Mockito, no database involved) cover the trip search service in isolation: route order validation, seat availability, and the "has this trip already departed at the requested origin" check — the last one using an injected, fixed `Clock` so time-dependent behavior is fully deterministic in tests.

```
./mvnw test -Dtest=TripSearchServiceTest
```

**Integration test for concurrency**: `TicketConcurrencyTest` spins up the full Spring context against a real PostgreSQL database, creates a trip with a single available seat, and launches two real threads that attempt to purchase it at the same time. It asserts that exactly one purchase succeeds and the other fails with an optimistic locking conflict — proving the `@Version` + `@Transactional` combination actually prevents overselling, rather than just assuming it does.

This test needs a running database:

```
docker compose up postgres -d
./mvnw test -Dtest=TicketConcurrencyTest
```

To run the full test suite:

```
docker compose up postgres -d
./mvnw test
```

## API documentation

Full interactive documentation is available via Swagger UI once the app is running:

- Local: `http://localhost:8080/swagger-ui.html`
- Deployed: `https://train-tracker-api-w194.onrender.com/swagger-ui.html`

The raw OpenAPI spec is served at `/v3/api-docs`.

## Environment variables

All configuration falls back to sensible local defaults if a variable isn't set, so the app runs out of the box with `docker compose up` and only `JWT_KEY` needs to be provided manually.

| Variable                    | Default                                            | Purpose                                             |
| ---------------------------- | --------------------------------------------------- | ---------------------------------------------------- |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5433/traintracker`     | JDBC connection string                              |
| `SPRING_DATASOURCE_USERNAME` | `traintracker_user`                                 | Database user                                       |
| `SPRING_DATASOURCE_PASSWORD` | `traintracker_pass`                                 | Database password                                   |
| `JWT_KEY`                    | *(dev key, override in production)*                 | HMAC secret used to sign JWTs — must be 256+ bits    |
| `JWT_EXPIRATION_MINUTES`     | `60`                                                 | JWT time-to-live                                    |
| `CORS_ALLOWED_ORIGINS`       | `http://localhost:5173`                             | Comma-separated list of allowed frontend origins    |
| `PORT`                       | `8080`                                              | Port the app listens on                             |

## A note on the API waking up

The API is deployed on Render's free tier, which spins the service down after a period of inactivity. If nobody has used it in a while, the **first** request can take up to ~3 minutes to go through — this is expected behavior of the free hosting tier, not a bug. Everything is fast afterwards.

## Related repository

- **Frontend**: [github.com/joseagim/train-tracker-frontend](https://github.com/joseagim/train-tracker-frontend) — React, Vite, Tailwind, consuming this API. Live demo: **[train-tracker-frontend.vercel.app](https://train-tracker-frontend.vercel.app)**.
