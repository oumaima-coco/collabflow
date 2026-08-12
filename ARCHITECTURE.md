# CollabFlow — Architecture

CollabFlow is a microservices-based project & team collaboration tool (Asana/Trello-style), built to demonstrate a real, defensible microservices architecture rather than a monolith split into folders.

## Tech stack

- **Backend**: Java 17, Spring Boot 3, Spring Cloud (Gateway, Eureka, Config Server)
- **Frontend**: Angular (standalone components, signals/NgRx for state)
- **Messaging**: Kafka (via Redpanda in local dev)
- **Databases**: PostgreSQL, one instance per service
- **Infra (local)**: Docker Compose
- **CI/CD**: GitHub Actions

## Services

| Service | Responsibility | Owns data | Talks to |
|---|---|---|---|
| `auth-service` | Registration, login, JWT issuing/refresh | Users' credentials, hashed passwords | Called by gateway/frontend |
| `user-service` | Profiles, teams, team membership, roles | Profiles, teams, memberships | Called by gateway; calls auth (verify token) |
| `project-service` | Projects, tasks, statuses, assignees | Projects, tasks | Publishes events to Kafka; called by gateway |
| `notification-service` | Consumes events, stores/delivers notifications | Notifications, delivery status | Consumes Kafka events from project-service |
| `activity-service` | Comments, activity feed (append-only log) | Comments, activity log entries | Consumes Kafka events; called by gateway |

## Why these boundaries (and not others)

A service boundary here follows a **change reason** or a **scaling reason** — not just "this looks like a different noun."

- **Auth is separate from User/Team** because identity/security concerns change on a different cadence than team/role management, and nearly every other service depends on Auth — keeping it small and stable matters more than keeping it "complete."
- **Project and Task are NOT split into two services.** They're read and written together almost every time; splitting them would add network hops with no real benefit. This is a deliberate trade-off, not an oversight.
- **Notification is separate** because it has a different failure mode: it should be able to go down without breaking the ability to create/edit tasks. Async (Kafka) instead of sync REST calls is what makes that possible.
- **Activity/Comments is separate** because its data shape (append-only log, high read volume) and access pattern are fundamentally different from the relational CRUD in Project/Task.

## Data ownership rule

Each service owns its own database. No service reads another service's database directly. Cross-service data needs are satisfied by:
1. A synchronous REST call through the gateway (used sparingly — e.g. project-service asking user-service "does this user exist"), or
2. An async event via Kafka (preferred — e.g. project-service publishes `TaskAssigned`, notification-service consumes it without project-service knowing or caring who's listening)



## Request flow (synchronous)

```
Angular client → API Gateway → [routes to] → Auth / User / Project services
```
The gateway is the single entry point. Services register themselves with Eureka; the gateway discovers them by name rather than hardcoded URLs, so services can scale or move without reconfiguring the gateway.

## Event flow (asynchronous)

```
project-service → publishes event → Kafka → notification-service consumes
                                          → activity-service consumes
```
Neither project-service nor the consumers know about each other directly. This is what lets notification-service or activity-service go down, or be slow, without blocking task creation.

## Cross-cutting concerns (added in later phases)

- **Config**: centralized via Spring Cloud Config Server (Phase 5)
- **Resilience**: circuit breakers / retries via Resilience4j (Phase 12)
- **Observability**: centralized logging via Grafana Loki (Phase 12)
- **CI/CD**: GitHub Actions running build + test + lint on every push (Phase 13)

## What I'd reconsider at larger scale

Documenting trade-offs honestly, not just decisions:
- Database-per-service means no cross-service JOINs — some read paths (e.g. "task list with assignee names") need either an API call per row or a denormalized read model. For this project's scale, a direct call is fine; at larger scale this would move to a CQRS-style read model.
- A monorepo is used here deliberately for solo-developer velocity. At a company with separate team ownership per service, multi-repo (or a monorepo with stricter ownership boundaries/CODEOWNERS) would be more appropriate.
