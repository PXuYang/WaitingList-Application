# Waiting List & Reservation Application Design

## 1. Product Vision
Create a cloud-connected, real-time restaurant seating and waitlist management platform that allows hosts to design custom table maps, monitor table status, manage reservations and waitlists, and notify guests when seats are ready. The application must support multiple devices per account, multiple restaurant locations, and deliver a responsive experience across web, tablet, and mobile clients.

## 2. Key Use Cases
- **Table map designer**: Hosts and managers create floor plans with multiple rooms, place tables of varying shapes/sizes, and define capacities.
- **Table status tracking**: Hosts update table states (Open, Seated, Bussing, Paid, Reserved, Unavailable) in real time and view color-coded status across devices.
- **Reservation management**: Schedule reservations, record guest details, track arrival/no-show, and seat or cancel reservations.
- **Waitlist management**: Add walk-in parties, estimate wait times, prioritize parties, and transfer them to tables when ready.
- **Guest notifications**: Notify guests via SMS, phone call, or in-app messaging when their table is ready, including automated reminders.
- **Multi-location operations**: Manage several restaurant locations within a single account, with role-based permissions for managers, hosts, and bussers.

## 3. User Personas
- **Restaurant Manager**: Configures locations, floor plans, hours, and manages staff accounts.
- **Host/Hostess**: Uses the table map, controls table status, and manages the queue.
- **Server/Bussers**: Update table readiness states and mark tables as bussing/clean.
- **Guests**: Receive notifications, confirm reservations, and join waitlists remotely.

## 4. Functional Requirements
1. Design, save, and reuse multiple floor plans per location.
2. Visual real-time table map with drag-and-drop table placement.
3. Table status categories: Open, Seated, Bussing, Cleaning, Reserved, Paid, Unavailable, Custom (configurable by managers).
4. Waitlist tracking with estimated wait times, party size, notes, and special requests.
5. Reservation lifecycle: create, modify, cancel, check-in, no-show logging.
6. Multi-device synchronization (< 2s latency) for table status and queue changes.
7. Notifications via SMS (Twilio or similar), optional voice calls, and email push.
8. Analytics dashboard (future phase) for turnover, wait times, and seating efficiency.
9. Role-based access control and audit log of actions.
10. Offline-friendly mode with queued sync when connectivity resumes (future enhancement).

## 5. Non-Functional Requirements
- **Availability**: 99.5% uptime target, multi-region deployment optional.
- **Scalability**: Support hundreds of concurrent devices per restaurant and thousands of reservations per day.
- **Security**: SOC2-ready practices, encrypted data in transit and at rest, least-privilege access.
- **Extensibility**: Modular services and open APIs for POS integration.
- **Usability**: Touch-friendly UI, accessible color contrast for status indicators.

## 6. Proposed Technology Stack
| Layer | Recommendation | Rationale |
| --- | --- | --- |
| Client Apps | React (web), React Native (iOS/Android) | Reuse components, deliver responsive UI, strong ecosystem |
| State Sync | GraphQL subscriptions (Apollo) or WebSocket channels | Real-time updates across devices |
| Backend | Node.js (TypeScript) with NestJS or Express | Familiar, scalable, good websocket support |
| Database | PostgreSQL (primary), Redis (real-time cache/queues) | Relational data model for reservations, fast pub/sub |
| Hosting | Cloud provider (AWS/GCP/Azure) with managed Postgres (RDS/Cloud SQL), containerized services (Docker + Kubernetes or ECS) | Simplifies scaling and deployment |
| Notifications | Twilio (SMS/Voice), SendGrid (Email) | Reliable communication APIs |
| Authentication | Auth0/Cognito or custom JWT service | Multi-tenant, secure user management |

## 7. System Architecture
1. **Client Layer**: Web/tablet host console, mobile host app, optional guest mobile app.
2. **API Gateway**: GraphQL endpoint for queries/mutations/subscriptions. REST endpoints exposed where integration partners prefer.
3. **Service Layer**:
   - **Table Management Service**: Handles floor plans, table states, occupancy tracking.
   - **Reservation Service**: Manages reservation lifecycle, conflict detection, reminders.
   - **Waitlist Service**: Stores queue entries, wait time estimation algorithms.
   - **Notification Service**: Interfaces with Twilio/Email, orchestrates messaging.
   - **User & Location Service**: Multi-tenant account management, roles, permissions.
   - **Sync & Presence Service**: Manages WebSocket connections, device presence, conflict resolution.
4. **Data Layer**: PostgreSQL schema with separate schemas per tenant or tenant_id column. Redis used for caching, pub/sub, and distributed locks.
5. **Integrations**: Optional connectors for POS, calendar, analytics exports.

### Real-Time Synchronization
- Use Apollo GraphQL subscriptions or Socket.IO channels backed by Redis pub/sub.
- Emit events on table status change, waitlist updates, reservation updates.
- Optimistic UI updates for quick feedback; server reconciliation ensures consistency.
- Maintain presence data to show active devices and notify others of conflicting edits.

### Multi-Device & Multi-Tenant Support
- Tenant (restaurant group) identifier ties together locations and devices.
- JWT tokens include tenant and role claims for authorization checks.
- Use row-level security policies or service-level filters for tenant isolation.

## 8. Data Model Overview
| Entity | Key Fields | Notes |
| --- | --- | --- |
| Account | id, name, billing_plan | Parent of tenants; handles subscription/billing |
| User | id, account_id, role, contact_info, status | Role-based access |
| Location | id, account_id, name, address, timezone | Each restaurant location |
| FloorPlan | id, location_id, name, layout_json | Stores table map configuration |
| Table | id, floor_plan_id, label, capacity, status, position | Status values enumerated |
| Reservation | id, location_id, party_name, size, datetime, status, table_id? | Includes notes, contact info |
| WaitlistEntry | id, location_id, party_name, size, phone, quoted_wait, status | Tracks walk-in parties |
| NotificationLog | id, target_type, target_id, channel, status, sent_at | Auditing notifications |
| AuditLog | id, user_id, action_type, payload_json, created_at | Traceability |

## 9. API Surface (Sample)
- `mutation createFloorPlan(input)` → returns floorPlan
- `mutation updateTableStatus(tableId, status)` → triggers subscription `tableStatusChanged`
- `mutation addWaitlistEntry(input)` → returns entry and publishes `waitlistUpdated`
- `mutation seatParty(waitlistEntryId, tableId)` → updates statuses, logs action
- `query reservationsByDate(locationId, date)`
- `subscription tableStatusChanged(locationId)` → streaming updates

## 10. Notification Flow
1. Host selects "Notify Party" on waitlist entry.
2. Client calls Notification Service API.
3. Notification Service enqueues message, uses Twilio/SendGrid to send SMS/Email.
4. Delivery status updates propagate via WebSocket subscription.
5. Guests can confirm via SMS keyword or app; response updates the queue.

## 11. Implementation Roadmap
1. **Phase 0 – Foundations**: Set up repo, CI/CD, authentication scaffold, database migrations.
2. **Phase 1 – Core Floor Plan & Status**: CRUD floor plans, real-time table status updates, multi-device sync prototype.
3. **Phase 2 – Waitlist & Reservations**: UI for queue management, reservation scheduling, conflict resolution.
4. **Phase 3 – Notifications & Analytics**: Integrate SMS/email, gather usage metrics, dashboards.
5. **Phase 4 – Multi-location Enhancements**: Cross-location reporting, centralized admin console.

## 12. Future Enhancements
- Integrate with POS systems for automatic check/bill status updates.
- AI-based demand forecasting and wait time predictions using historical data.
- Guest mobile app for self check-in, remote waitlist join, and loyalty program integration.
- Offline-first capabilities with local storage and conflict resolution once reconnected.
- Table turnover recommendations based on server workload and table history.

## 13. Success Metrics
- Reduction in average wait time and host manual workload.
- Increased table turnover rate and reservation fulfillment.
- High user satisfaction (NPS > 40) among hosts and managers.
- Notification delivery success rate > 98% with response tracking.

