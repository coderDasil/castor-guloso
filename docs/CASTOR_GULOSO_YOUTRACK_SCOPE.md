# 🦫 Castor Guloso — Lunch Order Automation

**Project Owner:** Renato Bacelar da Silveira (coderDasil)
**Repository:** `~/APPS/openclaw/food-beaver/restaurant-app/castor-guloso/`
**Codename:** Castor Alimentar
**Status:** 🟢 Scaffolded — build passing

---

## 1. Problem Statement

The restaurant owner currently manages lunch orders through a **manual notebook** and daily **WhatsApp broadcast** to clients. This is:

- Prone to human error (missed orders, transposition mistakes)
- Time-consuming (manual count, manual messaging)
- Not scalable (adding clients increases overhead linearly)
- No audit trail or historical data

**Goal:** Replace the notebook + broadcast workflow with an automated digital system where the owner manages clients, menus, and orders through a mobile-friendly admin panel, and the system handles WhatsApp communication automatically.

---

## 2. Daily Workflow (High-Level)

| Phase | Time | What Happens |
|-------|------|-------------|
| Setup | One-time | Owner registers clients + default menu items |
| Morning | 10:00 | System sends personalized lunch request to each active client via WhatsApp |
| Window | 10:00–11:30 | Clients reply via WhatsApp; system processes replies in real-time |
| Cutoff | 11:30 | System finalizes orders, generates kitchen summary |
| Summary | 11:30 | Owner views aggregated order totals to hand to kitchen |

The physical notebook is retired.

---

## 3. Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.2 |
| Frontend | Vaadin 24 (pure Java, server-side rendered, WebSocket push) |
| Database | PostgreSQL (Flyway migrations) |
| Messaging | WhatsApp Business Cloud API (Meta) |
| i18n | English + Brazilian Portuguese (Vaadin I18NProvider + Spring MessageSource) |
| Build | Maven multi-module |
| Dev DB | H2 (PostgreSQL compatibility mode) |

---

## 4. Module Architecture

```
castor-guloso/
├── pom.xml                             # Parent POM (Spring Boot 3.3.2)
├── castor-guloso-core/                 # Domain entities, enums, shared interfaces
├── castor-guloso-customer/             # Customer management (registration, profiles)
├── castor-guloso-menu/                 # Menu catalog + daily menu definition
├── castor-guloso-order/                # Order lifecycle, personalization engine, summaries
├── castor-guloso-whatsapp/             # WhatsApp Business API client, webhook handler
├── castor-guloso-web/                  # Vaadin 24 admin UI (replaces the notebook)
└── castor-guloso-app/                  # Spring Boot entry point, global config, Flyway
```

Modules are **feature-oriented**: each owns its slice of the domain and depends only on `core` (and `order` depends on `customer` + `menu` for business logic). No circular dependencies.

---

## 5. Database Schema (V1 — Initial)

Core tables (all in `V1__initial_schema.sql`):

- **`menu_items`** — Catalog of all dishes (normalized, not JSONB)
- **`daily_menus`** — Which items are offered on a given date
- **`daily_menu_items`** — Join table (many-to-many)
- **`customers`** — Client profiles (name, phone, active status)
- **`daily_sessions`** — Groups orders by day (OPEN / CLOSED)
- **`daily_orders`** — Per-client order within a session (PENDING / CONFIRMED / NO_REPLY)
- **`tags`** — Customer categorization catalog (type: GROUP / DIETARY / FINANCIAL / STATUS, with color-coded UI badges)
- **`customer_tags`** — Many-to-many join with per-association notes (e.g., "allergic to shrimp")

The normalized schema (vs. JSONB) enables:
- Statistical tracking of item popularity
- Client favorite detection (personalization engine)
- Menu trend analysis over time

---

## 6. Milestones

| # | Milestone | Description | Est. Days |
|---|-----------|-------------|-----------|
| M1 | Scaffolding | ✅ Done — Maven multi-module, entities, repos, Flyway, Vaadin shell | — |
| M2 | Customer CRUD | Admin UI to register/list/edit customers; REST API layer | 2 |
| M3 | Menu Management | Catalog items + daily menu setup screen; REST API | 2 |
| M4 | WhatsApp Integration | Meta Business setup, webhook endpoint, message sending client | 3 |
| M5 | Order Lifecycle | Scheduler trigger, message sending, reply processing, state machine | 3 |
| M6 | Live Dashboard | Vaadin live board with WebSocket push (pending/confirmed/no-reply) | 2 |
| M7 | Personalization | "Favorite" detection per client, tailored message templates | 2 |
| M8 | Finalization & Summary | Cutoff logic, kitchen summary view, optional owner notification | 1 |
| M9 | Testing & Polish | Edge cases, error handling, test coverage, UI polish | 2 |
| M10 | i18n & Localization | Translate UI strings to Brazilian Portuguese, locale toggle, ltr/rtl | 1 |
| M11 | Customer Categorization | Tag management UI, badge display on dashboard, filtering by tag | 2 |

**Total estimate: ~17 working days**

---

## 7. Key Technical Decisions

- **Vaadin over JS/TS** — Pure Java frontend, no separate build pipeline, first-class Spring Boot integration. Server-side rendering with automatic WebSocket push for real-time updates.
- **Normalized menu items** — Enables SQL-based analytics (favorites, popularity) over time.
- **Flyway** — Version-controlled schema migrations, validate mode in Hibernate for safety.
- **Feature modules** — Each module is independently testable. The `whatsapp` module can be swapped without touching order/customer logic.
- **`application-dev` profile** — H2 in PostgreSQL mode so developers don't need a local PG install.
- **i18n first-class** — Vaadin `I18NProvider` backed by `messages.properties` (EN) and `messages_pt_BR.properties`. Locale toggle component in the UI. New views use `getTranslation()` instead of hardcoded strings.
- **Flexible tag system** — Customers are categorized via a tag catalog with four types: GROUP (workplace/demographics), DIETARY (allergies/restrictions), FINANCIAL (payment flags), STATUS (blacklist, VIP). Tags can be created without code changes. Per-association notes for context (e.g., severity of an allergy).

---

## 8. WhatsApp API Costs (Estimate)

| Message Type | Cost | Frequency |
|-------------|------|-----------|
| Utility (daily request) | ~$0.008/message | 1 per client per day |
| Service (client reply) | Free (within 24h) | 1 per client per day |

**Estimate for 50 clients × 22 workdays:** ~$6.60/month

---

## 9. Deliverables

- [ ] Spring Boot backend with REST API
- [ ] Vaadin admin panel (mobile-friendly PWA)
- [ ] WhatsApp Business API integration
- [ ] Automated daily scheduling
- [ ] Kitchen summary generation
- [ ] Client history & favorite tracking
- [ ] Customer categorization (tags with GROUP / DIETARY / FINANCIAL / STATUS types)
- [ ] Deployment documentation (Render / Railway free tier)

---

## 10. Out of Scope (v1)

- Payment processing
- Delivery logistics / tracking
- Multi-restaurant support
- Mobile app (PWA covers the admin panel need)
- Analytics beyond basic order history
