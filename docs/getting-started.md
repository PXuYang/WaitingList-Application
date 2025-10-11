# Getting Started Guide

This guide outlines a practical path for turning the application design into a working waitlist and reservation platform.

## 1. Clarify Scope and Priorities
1. Re-read the [application design](application-design.md) and highlight the core MVP capabilities: floor plan designer, table status sync, waitlist, and reservations.
2. Decide which features are essential for your first release. Start with a host-facing web app before tackling native mobile clients.
3. Capture open questions (e.g., authentication provider, notification vendor) and resolve them before coding.

## 2. Set Up the Project Workspace
1. Install prerequisite tooling:
   - **Node.js 20+** and **npm** or **yarn**.
   - **PostgreSQL 15+** and **Redis** (Docker Desktop or local services both work).
   - A package manager for infrastructure (e.g., **Docker**, **Docker Compose**).
2. Create a mono-repo or poly-repo structure. A simple starting point:
   ```text
   /app
     /client (React web host console)
     /server (Node.js + TypeScript API)
     /infra (IaC scripts, Docker Compose)
   ```
3. Initialize Git, configure formatting (Prettier, ESLint), and set up TypeScript configuration for both client and server.

## 3. Bootstrap the Backend
1. Scaffold a NestJS or Express + TypeScript project in `/app/server`.
2. Define the database schema using a migration tool (Prisma, TypeORM, or Knex). Implement tables for accounts, locations, floor plans, tables, reservations, and waitlist entries.
3. Implement authentication/authorization early. Use Auth0 or another managed provider to avoid building auth from scratch.
4. Expose initial REST or GraphQL endpoints:
   - `POST /floor-plans`
   - `GET /locations/:id/tables`
   - `POST /waitlist`
   - `POST /reservations`
5. Configure WebSocket or GraphQL subscription support to broadcast table status and waitlist updates.
6. Write integration tests (Jest + Supertest) and seed scripts to populate development data.

## 4. Build the Host Web Client
1. Scaffold a React app (Vite or Create React App) in `/app/client` with TypeScript and Tailwind or Material UI.
2. Implement foundational pages:
   - **Login** and **location selector**.
   - **Floor plan designer** using a canvas/drag-and-drop library (React Flow, Konva, or Fabric.js).
   - **Table status board** with real-time updates via WebSocket client.
   - **Waitlist & reservations view** with forms for adding parties and seating them.
3. Connect to the backend API, handling optimistic updates and error states.
4. Add component/unit tests with React Testing Library and Cypress for end-to-end flows.

## 5. Wire Up Notifications
1. Create a `NotificationService` on the backend using Twilio (SMS/voice) and optionally SendGrid (email).
2. Store notification preferences per location and per waitlist entry/reservation.
3. Provide UI controls for hosts to trigger notifications and display delivery status.

## 6. Prepare for Multi-Device Sync
1. Implement presence tracking so each connected device registers itself with the Sync service.
2. Ensure all mutations publish events to Redis pub/sub (or another event bus) consumed by WebSocket gateways.
3. Add conflict handling (e.g., last-write-wins with audit logging) and toast alerts when another device updates the same table.

## 7. DevOps and Deployment Foundations
1. Create Dockerfiles for client and server, plus a `docker-compose.yml` for local development (web, API, Postgres, Redis).
2. Configure CI (GitHub Actions) to lint, test, and build both projects on every push.
3. Set up Infrastructure as Code (Terraform or CloudFormation) describing cloud resources.
4. Automate database migrations during deployment.

## 8. Iterate with Feedback
1. Run usability tests with real hosts to validate the table designer and queue workflows.
2. Monitor metrics (wait times, seating duration) and bug reports to prioritize next sprints.
3. Add mobile clients, analytics dashboards, and offline support once the core workflow is stable.

## 9. Learning Resources
- **NestJS Docs**: https://docs.nestjs.com/
- **React + TypeScript Cheatsheets**: https://react-typescript-cheatsheet.netlify.app/
- **Prisma ORM**: https://www.prisma.io/docs
- **Twilio Programmable Messaging**: https://www.twilio.com/docs/sms

Following this path keeps the initial scope manageable while laying a foundation for multi-device, real-time collaboration.
