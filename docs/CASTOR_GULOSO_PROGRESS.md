# 🦫 Castor Guloso — Project Progress

> **Last update:** 2026-11-10 — WhatsApp stub + Scheduler + Live Dashboard
> **Previous:** Customer CRUD (2026-11-09)

---

## 📦 Modules Overview

| Module | Artifact | Responsibility |
|--------|----------|----------------|
| core | castor-guloso-core | Domain entities, enums, shared exceptions |
| customer | castor-guloso-customer | Customer + Tag CRUD, repositories |
| menu | castor-guloso-menu | MenuItem, DailyMenu, price history |
| order | castor-guloso-order | OrderSession, DailyOrder lifecycle, kitchen summary |
| whatsapp | castor-guloso-whatsapp | WhatsAppClient, MessageService, Webhook |
| web | castor-guloso-web | Vaadin 24 UI (Dashboard, Customers, Menu, …) |
| app | castor-guloso-app | Spring Boot entrypoint + scheduler |

---

## ✅ What is working right now

### Order lifecycle (M5 + M8)
- `OrderSessionService` opens a daily session, creates PENDING orders for active customers
- Confirm by phone (webhook) or free-text reply
- `finalizeSession()` marks remaining as NO_REPLY and closes the session
- `kitchenSummary()` aggregates confirmed items for the kitchen

### WhatsApp (M4 partial)
- `WhatsAppClient` interface + `LoggingWhatsAppClient` (`whatsapp.mode=logging`)
- `WhatsAppMessageService.sendDailyLunchRequests()` — open session → create pending → send text → store msg id
- Webhook: GET verification + POST inbound text → `confirmByCustomerPhone`
- No Meta credentials required in logging mode

### Scheduler
- `@Scheduled` morning send (default 10:00) and cutoff finalize (default 11:30)
- Cron expressions configurable in `application.properties`

### Live Dashboard (M6)
- Route `/dashboard`
- Action buttons: Send lunch requests / Refresh / Finalize day
- Live order grid + kitchen summary grid
- Stats line (total / pending / confirmed / no-reply)

### i18n (M9)
- Full EN + pt_BR message bundles including dashboard keys

---

## 🔌 REST API (selected)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/session/open` | Open today’s session |
| POST | `/api/orders/pending` | Create pending for active customers |
| POST | `/api/orders/{id}/confirm` | Confirm an order |
| POST | `/api/orders/confirm-by-phone` | Confirm by customer phone (webhook use) |
| POST | `/api/orders/session/finalize` | Close session + mark remaining NO_REPLY |
| GET | `/api/orders/summary` | Kitchen summary aggregates |

### WhatsApp webhook
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/webhook/whatsapp` | Meta verification challenge |
| POST | `/webhook/whatsapp` | Inbound messages → confirm order by phone |

---

## 🖥️ Vaadin UI

| Route | View | Description |
|-------|------|-------------|
| `/` | MainView | Landing + nav (Dashboard primary button) |
| `/dashboard` | **DashboardView** | Live board: send / refresh / finalize + kitchen summary |
| `/customers` | CustomersView | Customer CRUD + tag badges |
| `/menu-items` | MenuItemsView | Menu catalog CRUD |
| `/today-menu` | DailyMenuView | Pick today’s offered items |
| `/price-history` | PriceHistoryView | Price history |

---

## 📈 Milestones

| # | Milestone | Status | Notes |
|---|-----------|--------|-------|
| M1 | Scaffolding | ✅ | |
| M2 | Menu CRUD | ✅ | |
| M3 | Customer CRUD | ✅ | |
| M4 | WhatsApp Integration | 🟡 Partial | Logging client + webhook + send pipeline. Real Meta client still TODO |
| M5 | Order Lifecycle | ✅ | open → pending → confirm → finalize + summary |
| M6 | Live Dashboard | ✅ | DashboardView with actions |
| M7 | Personalization | ⏳ | Favorite detection not yet |
| M8 | Finalization & Summary | ✅ | finalizeSession + kitchenSummary |
| M9 | i18n | ✅ | EN + pt_BR including dashboard keys |
| M10 | Tags | ✅ | |
| M11 | Testing & Polish | ⏳ | |

---

## ⚙️ New configuration

```properties
whatsapp.mode=logging          # default: LoggingWhatsAppClient (no Meta calls)
castor.scheduler.send-cron=0 0 10 * * *
castor.scheduler.cutoff-cron=0 30 11 * * *
```

---

## 🧪 How to run

```bash
cd castor-guloso
# full reactor (ignore .mvn/maven.config -pl restriction when installing modules)
mv .mvn/maven.config .mvn/maven.config.bak   # optional if -pl interferes
mvn -DskipTests install
mv .mvn/maven.config.bak .mvn/maven.config

mvn spring-boot:run
# → http://localhost:7080
# → http://localhost:7080/dashboard
```

---

## Next recommended work

1. **Real MetaWhatsAppClient** (behind `whatsapp.mode=meta`) using WebClient
2. **Personalization** — detect favorite item from last N confirmed orders and tailor message
3. **Vaadin push / auto-refresh** on Dashboard
4. **Unit/integration tests** for OrderSessionService + webhook parsing
5. **Deployment notes** (Render / Railway / Oracle free tier)
