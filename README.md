# School Management System

A Spring Boot REST API for managing students, teachers, classes, subjects, grades,
attendance and fee payments, with JWT-based authentication and role-based access control
(Admin / Teacher / Student / Parent / Accountant / Principal / Deputy Principal).

Ownership is enforced, not just role: a teacher can only mark attendance or enter grades for
their own class/subject (`TeacherAccessService`), and a parent can only read their own linked
children's records (`ParentAccessService`). Accountant, Principal and Deputy Principal are
plain roles on a `User` (no dedicated entity) — provision them via `POST /api/users` and
`PUT /api/users/{id}/roles` (admin-only). Parent accounts are the one role backed by a real
entity (`Parent`, linked to `Student`s via `POST /api/parents/{parentId}/children/{studentId}`),
since it's the only one that needs an actual relationship rather than just a permission.

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

The app starts on port `8084` (override with `PORT` — the same env var most PaaS hosts inject
automatically). On first run, `DataLoader` seeds the four roles (`ROLE_ADMIN`, `ROLE_TEACHER`,
`ROLE_STUDENT`, `ROLE_PARENT`).

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

A copy-pasteable template for all of the above lives in [`.env.example`](.env.example) — copy
it to `.env` (already gitignored) or paste the values into your host's environment variable
settings.

### Using Neon Postgres

Neon is just Postgres, so the existing `org.postgresql.Driver` config works unchanged —
you're only setting `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`, not adding a new integration.

1. In the [Neon console](https://console.neon.tech), open your project, pick the `production`
   branch, and copy the **pooled** connection string (`Connect` → `Pooled connection`).
2. Convert it to a JDBC URL and drop `channel_binding` (that's a `psql`/libpq-only parameter
   pgjdbc doesn't recognize — `sslmode=require` alone already forces TLS):

   ```
   psql string:  postgresql://neondb_owner:PASSWORD@ep-xxx-pooler.region.aws.neon.tech/neondb?sslmode=require&channel_binding=require
   DB_URL:       jdbc:postgresql://ep-xxx-pooler.region.aws.neon.tech/neondb?sslmode=require&prepareThreshold=0
   DB_USERNAME:  neondb_owner
   DB_PASSWORD:  PASSWORD
   ```

   `prepareThreshold=0` disables pgjdbc's server-side prepared statement caching, which is
   required when connecting through Neon's pooled endpoint (PgBouncer in transaction mode) —
   without it you'll eventually hit `prepared statement "..." already exists` errors under
   load. If you ever need the non-pooled endpoint (e.g. for a one-off migration tool), use
   the direct connection string instead and you can drop that parameter.
3. Treat the password as a secret: never commit it, and if one is ever pasted somewhere it
   shouldn't be (a chat, a PR, a log), reset it from the Neon console immediately.

### Deploying to Render

Render has no native Java runtime, so it builds and runs this app from the [`Dockerfile`](Dockerfile)
in this repo — you don't need Docker installed locally or interact with it directly; Render
builds and runs the image for you. [`render.yaml`](render.yaml) defines the service as a
Blueprint pointing at that Dockerfile.

Render injects `PORT` itself, which `application.properties` already binds to, so no extra
config is needed for that.

1. Push this repo to GitHub (see above), then in the Render dashboard choose **New → Blueprint**
   and point it at the repo — it picks up `render.yaml` automatically.
2. The blueprint declares `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and
   `CORS_ALLOWED_ORIGINS` as unset (`sync: false`) on purpose — Render will prompt you to fill
   these in through its dashboard during setup rather than storing them in the repo. Use the
   Neon values from the section above.
3. Every push to `main` redeploys automatically once the service is created.

If you'd rather not use a Blueprint, create a Web Service manually, choose the **Docker**
language/environment, and Render will pick up the same `Dockerfile` automatically.

## Security notes

- Public self-signup (`POST /api/auth/signup`) can only create `STUDENT` or `PARENT`
  accounts. Admin and teacher accounts are provisioned separately, by an authenticated admin
  (see `StudentService`/`TeacherService`), so signup can't be used to self-escalate.
- CORS is configured centrally in `WebSecurityConfig` via `app.cors.allowed-origins` — set it
  to your actual frontend origin(s) in production rather than leaving the dev/test default of
  `*`.
