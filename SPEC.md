# BackMind

## Product Summary
Backmind is a personal learning-retention app that helps users remember things they forgot they learned.

The app should feel like a private feed of the user’s own past knowledge, not like studying.
---

## Core Product Idea

Tagline:
Remind me what I forgot I learned.

Product framing:
Every note has future value.

Users quickly capture short thoughts, and the app brings them back later through a lightweight feed or notification.

Example:

You learned this 12 days ago:
“Diversification reduces single-stock risk, but it does not remove market risk.”

The user can then respond:
- Useful
- Not useful
- Still believe this
- I forgot this
- Explain again

---

## MVP Goal

Build a simple app where users can:

- Capture a short note
- See old notes in a casual feed
- Get occasional reminders
- Mark whether a note is still useful
- See notes they have not revisited in a long time

---

## MVP Features
### User Authentication
Users should be able to:
- Sign up
- Log in
- Log out

Auth options:
- Email and password
- JWT-based authentication

### Create Note

Users can create a short note.

Fields:

- Note content
- Optional category
- Created date
- Next review date
- Last seen date

Rules:

- Note should be short
- Recommended length: 1–3 lines
- No required title

Data Validation Constraints:

- Note Content: Required. Minimum 1 character, Maximum 300 characters. No trailing or leading spaces allowed.
- Category: Optional. Must be alphanumeric (letters and numbers only), Maximum 30 characters.
- Preferred Reminder Time: Required for notifications. String format must strictly adhere to HH:mm (24-hour format, e.g., "08:30" or "21:45").

### Home Feed

The home feed shows resurfaced notes.

Feed card example:

You learned this 10 days ago
"Diversification reduces single-stock risk, but it does not remove market risk.”"

Actions:

- Useful
- Not useful
- Still believe this
- I forgot this

### Passive Resurfacing

The app selects notes to show based on:

Notes due for review
Random older notes
Notes from the lost knowledge bucket

Initial resurfacing intervals:

- 1 day after creation
- 3 days after creation
- 10 days after creation
- 30 days after creation
- 60 days after creation
- Random resurfacing after that

### Lost Knowledge Bucket

A note goes into the lost knowledge bucket when it has not been seen for 30+ days.

Example copy:

You haven’t seen this in 47 days. Still useful?

This feature is important because it directly solves the problem of forgotten thoughts and forgotten app ideas.

### Feedback on notes
Feedback options:

- Useful
- Not useful
- Still believe this
- No longer believe this
- I forgot this
- Skip

This feedback helps decide whether to resurface the note again.

### Notifications
The app sends occasional reminders.

Notification frequency:

- Once per day by default
- User can turn notifications off
- User can choose preferred reminder time

---

## Non-MVP Features

Do not build these first:

- AI explanations
- AI summaries
- Advanced tagging
- Public sharing
- Social feed
- Browser extension
- Notion import
- Apple Notes import
- Complex analytics
- Streaks
- Gamification
- Embeddings/search

---

## Future Features
### Explain Again
User taps:
    *Explain again*

The app uses AI to explain the note in simpler terms.

### Related Notes

The app can show similar old notes.

### Belief Tracking
The app can ask:
*Do you still believe this?*

### Knowledge Timeline

A timeline of how the user’s thinking changed over time.

---

## Tech Stack
Frontend:
- React Native + Expo 
- TypeScript

Backend: 
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT authentication
- Bean Validation
- PostgreSQL
- Spring Scheduler
- Firebase Cloud Messaging for push notifications

Deployment:

MVP

- Backend: Render
- Frontend: Vercel
- Database: Neon

Later

- AWS ECS Fargate
- AWS RDS PostgreSQL
- AWS S3
- AWS CloudWatch
- Redis
- Quartz Scheduler or message queue

---
## Backend Modules
backmind
├── auth 
├── user 
├── note 
├── review 
├── resurfacing 
├── notification 
├── common 
└── config

Main services:

- AuthService
- UserService
- NoteService
- ReviewService
- ResurfacingService
- NotificationService

---

## Database Tables

### Database Enum Types
- `note_status`: ('ACTIVE', 'ARCHIVED', 'DELETED')
- `belief_status`: ('UNKNOWN', 'STILL_BELIEVE', 'NO_LONGER_BELIEVE', 'UNSURE')
- `feedback_type`: ('USEFUL', 'NOT_USEFUL', 'STILL_BELIEVE', 'NO_LONGER_BELIEVE', 'FORGOT_THIS', 'SKIPPED')
- `resurfacing_reason`: ('SPACED_REVIEW', 'RANDOM', 'LOST_KNOWLEDGE')

### Users table

Store user account information

Fields:
- id (UUID, Primary Key)
- email (VARCHAR(255), Unique, Not Null)
- password_hash (VARCHAR(255), Not Null)
- created_at (TIMESTAMP, Not Null)
- updated_at (TIMESTAMP, Not Null)

### Notes table

Stores user notes

Fields:
- id (UUID, Primary Key)
- user_id (UUID, Foreign Key referencing `users(id)`)
- content (VARCHAR(300), Not Null)
- category (VARCHAR(50), Nullable)
- created_at (TIMESTAMP, Not Null)
- updated_at (TIMESTAMP, Not Null)
- last_seen_at (TIMESTAMP, Not Null)
- next_review_at (TIMESTAMP, Not Null)
- current_interval_days (INT, Not Null, Default 1)
- status (`note_status` Enum)
- usefulness_score (INT, Default 0, Not Null)
- belief_status (`belief_status` Enum, Default 'UNKNOWN', Not Null)

### note_reviews

Stores every time a user interacts with a resurfaced note.
Fields:

- id (UUID, Primary Key)
- note_id (UUID, Foreign Key referencing `notes(id)`)
- user_id (UUID, Foreign Key referencing `users(id)`)
- reviewed_at (TIMESTAMP, Not Null)
- feedback_type (`feedback_type` Enum, Not Null)
- user_response (VARCHAR(500), Nullable)

### resurfacing_events
Stores when the app resurfaced a note.

Fields:

- id (UUID, Primary Key)
- note_id (UUID, Foreign Key referencing `notes(id)`, Not Null)
- user_id (UUID, Foreign Key referencing `users(id)`, Not Null)
- shown_at (TIMESTAMP, Not Null)
- reason (`resurfacing_reason` Enum, Not Null)

### notification_preferences

Stores user reminder settings.

Fields:
- id (UUID, Primary Key)
- user_id (UUID, Foreign Key referencing `users(id)`, Not Null)
- enabled (BOOLEAN, Default True, Not Null)
- preferred_time (VARCHAR(5), Not Null)
- frequency (VARCHAR(20), Default 'DAILY', Not Null)
- created_at (TIMESTAMP, Not Null)
- updated_at (TIMESTAMP, Not Null)

---

## Initial API Endpoints

### Auth APIs
POST /api/auth/signup
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me

### Note APIs
POST /api/notes
GET /api/notes
GET /api/notes/{id}
PUT /api/notes/{id}
DELETE /api/notes/{id}

### Feed APIs
GET /api/feed/today
GET /api/feed/lost

### Review APIs
POST /api/notes/{id}/review

Example review request:
{
  "feedbackType": "USEFUL",
  "userResponse": "This is still useful for investing basics."
}

### Notification Preference APIs
GET /api/notification-preferences
PUT /api/notification-preferences

---

## Resurfacing Logic

The system utilizes a fixed progression array for successful reviews: `[1, 3, 10, 30, 60]`

### State Transitions on User Action

1. **When a note is created:**
   * `current_interval_days = 1`
   * `next_review_at = created_at + 1 day`
   * `status = 'ACTIVE'`

2. **When user reacts with "Useful" or "Still believe this":**
   * Find the current value of `current_interval_days` in the interval array.
   * Increase it to the next highest interval in the array (e.g., 3 bumps to 10; Max out at 60).
   * Update `current_interval_days` to this new value.
   * Set `next_review_at = NOW() + current_interval_days`.

3. **When user reacts with "I forgot this":**
   * Reset `current_interval_days = 1`
   * Set `next_review_at = NOW() + 1 day`

4. **When user reacts with "Not useful" or "No longer believe this":**
   * Change `status = 'ARCHIVED'`
   * Remove from active resurfacing queues completely.

5. **Lost knowledge rule:**
    * If last_seen_at is older than 30 days, include in lost knowledge bucket.

### Feed selection rule:

Daily feed should include:
- Notes due today
- Some random older notes
- Some lost knowledge notes

### Edge-Case & Architecture Guardrails

- **Auth Identity Context:** Never accept a `userId` in a request body (like `POST /api/notes`). It's a security risk. Let Spring Security handle the JWT parsing behind the scenes and inject the user via `@AuthenticationPrincipal`.

- **Soft-Delete vs. Archive Filtering:** The Hibernate global filter (`@SQLRestriction`) on the Note entity must target `status <> 'DELETED'`. This ensures that both `ACTIVE` and `ARCHIVED` notes remain completely queryable for metrics, history logs, or the lost knowledge bucket, while ensuring that `DELETED` notes are globally hidden from standard repository lookups. Exclude `ARCHIVED` notes from the daily feed using regular repository query logic rather than Hibernate-level blocks.

- **Feed Saturation Guardrail:** GET /api/feed/today must cap response size at 5 notes max to prevent user overwhelm. It should pull a distributed sample: 60% due spaced-repetition notes, 20% from the Lost Knowledge bucket, and 20% random older active notes.

---

## Example User Flow

### First-time user
1. User signs up
2. User adds first note
3. App confirms: *Saved. I’ll bring this back later.*
4. Next day, app shows: *You learned this yesterday.*
5. User taps Useful
6. App schedules the note again after 3 days

### Returning user
1. User opens app
2. Home feed shows 3 resurfaced notes
3. One note says: *You haven’t seen this in 42 days.*
4. User taps: *Still believe this*
5. Note gets updated and scheduled for future resurfacing

---

## Backend Implementation Phases

### Phase 1 - Foundation
- Build the project skeleton, domain models, repositories, DTOs, validation, and error handling.

- Configure Hibernate/JPA global filters (`@SQLRestriction("status <> 'DELETED'")`) on the Note Entity to handle soft deletes out of the box while keeping active and archived data accessible.

### Phase 2 - Authentication
- Implement signup, login, logout, /me, JWT security, and password hashing.

### Phase 3 - Notes
- Implement note CRUD with ownership checks and content validation.

### Phase 4 - Reviews
- Implement note review tracking and resurfacing state updates.

### Phase 5 - Feed
- Implement today and lost feed endpoints with deterministic selection logic.

### Phase 6 - Notifications
- Implement notification preferences and scheduler stubs.

### Phase 7 - Hardening
- Add tests, seed data, logging, and deployment config cleanup.

---

## Frontend Scope

Build the frontend separately from the backend using React Native + Expo.

### Frontend MVP
- Auth screens: signup, login, logout.
- Home feed screen.
- Create note screen.
- Review actions: Useful, Not useful, Still believe this, I forgot this.
- Notification preferences screen.

### Frontend Architecture
- TypeScript.
- Navigation stack with auth and app flows.
- Reusable UI components.
- API client layer for backend calls.
- Secure token storage.
- Feature-based folder structure.

---

## Frontend Implementation Phases

### Phase 1 - App Shell
- Set up React Native + Expo + TypeScript, environment config, theming, shared UI components, and base navigation.

### Phase 2 - Auth
- Implement signup, login, logout, token storage, session bootstrap, and route guards.

### Phase 3 - Core Screens
- Implement home feed, create note, note review actions, and notification preferences screens.

### Phase 4 - API Integration
- Create a typed API client, connect auth and notes to the backend, and handle loading/error states.

### Phase 5 - Push Notifications
- Request permissions, register device tokens, and wire basic notification handling and deep links.

### Phase 6 - Polish
- Add empty states, reusable cards, form validation, and basic tests.

**Input validations in UI:**
- Note Content: Min 1 character, Max 300 characters. No trailing spaces.
- Category: Optional, alpha-numeric, Max 30 characters.