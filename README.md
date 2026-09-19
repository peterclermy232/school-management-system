# School Management System

A Spring Boot REST API for managing students, teachers, classes, subjects, grades and
attendance, with JWT-based authentication and role-based access control (Admin / Teacher /
Student / Parent).

## Tech stack

- Java 17, Spring Boot 3.2 (Web, Data JPA, Security, Validation)
- PostgreSQL (runtime), H2 in-memory (tests only)
- JWT auth (`jjwt`), BCrypt password hashing

## Running locally

Requires a local PostgreSQL instance. By default the `dev` profile (active unless overridden)
connects to `jdbc:postgresql://localhost:5432/school_management_db` with `postgres`/`123` —
override any of these with the `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` env vars if your local
setup differs.

```bash
./mvnw spring-boot:run
```

The app starts on port `8084` (override with `SERVER_PORT`). On first run, `DataLoader` seeds
the four roles (`ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`, `ROLE_PARENT`).

## Running the tests

```bash
./mvnw test
```

The test suite runs against an in-memory H2 database (`application-test.properties`), so it
never needs a live PostgreSQL instance and is safe to run in CI. It covers:

- **Service layer** (`src/test/java/com/school/service`) — unit tests with Mockito for
  `StudentService`, `TeacherService`, `GradeService`, `AttendanceService`, `UserService`.
- **Controller layer** (`src/test/java/com/school/controller`) — `MockMvc` tests that boot the
  full app context (mocking only the service beans) so the real `@PreAuthorize` rules and JWT
  security filter chain are exercised, not just the HTTP mapping.
- **JWT utility** (`src/test/java/com/school/util/JwtUtilsTest`) — token generation, parsing
  and expiry.

## Continuous integration

`.github/workflows/ci.yml` runs `./mvnw test` on every push and pull request against `main`
(Temurin JDK 17, with Maven dependency caching). Since tests use H2, no database service
container is needed. To activate it, push this repository to GitHub:

```bash
git remote add origin <your-repo-url>
git push -u origin main
```

## Deploying to production

Set `SPRING_PROFILES_ACTIVE=prod` and provide these environment variables — the app fails
fast at startup if any are missing, rather than silently falling back to a dev credential:

| Variable | Purpose |
|---|---|
| `DB_URL` | JDBC URL, e.g. `jdbc:postgresql://<host>:5432/<db>` |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials |
| `JWT_SECRET` | Base64-encoded HMAC-SHA256 signing key |
| `JWT_EXPIRATION` | Token lifetime in ms (optional, defaults to 24h) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins |

`application-prod.properties` currently keeps `spring.jpa.hibernate.ddl-auto=update` since
there's no schema migration tool wired up yet. Introducing Flyway or Liquibase and switching
this to `validate` is a recommended next step before relying on this in a long-lived
production database.

## Security notes

- Public self-signup (`POST /api/auth/signup`) can only create `STUDENT` or `PARENT`
  accounts. Admin and teacher accounts are provisioned separately, by an authenticated admin
  (see `StudentService`/`TeacherService`), so signup can't be used to self-escalate.
- CORS is configured centrally in `WebSecurityConfig` via `app.cors.allowed-origins` — set it
  to your actual frontend origin(s) in production rather than leaving the dev/test default of
  `*`.
