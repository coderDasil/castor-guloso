# 🦫 Castor Guloso — Project Progress

> **Last update:** 2026-11-12 — WhatsApp stub + Scheduler + Live Dashboard (pushed to GitHub)
> **Previous:** Customer CRUD (2026-11-09)

---

## 📦 Modules Overview

| Module | Artifact | Status | What it does |
|--------|----------|--------|-------------|
| 🧠 Core | `castor-guloso-core` | ✅ Done | JPA entities, enums |
| 👤 Customer | `castor-guloso-customer` | ✅ Done | Service + DTOs + repos + tags |
| 📋 Menu | `castor-guloso-menu` | ✅ Done | Menu item + daily menu + prices |
| 🧾 Order | `castor-guloso-order` | ✅ Done | Session lifecycle, confirm, kitchen summary |
| 💬 WhatsApp | `castor-guloso-whatsapp` | 🟡 Stub+ | Logging client, webhook, message service |
| 🖥️ Web (Vaadin) | `castor-guloso-web` | ✅ Done | Menu, Customers, **Dashboard**, i18n |
| 🚀 App | `castor-guloso-app` | ✅ Done | Boot + Flyway + **CastorScheduler** |

---

## 🌐 REST API (high level)

### Orders (`/api/orders`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/session/open` | Open or get today's session |
| POST | `/api/orders/session/create-pending` | Create PENDING for all active customers |
| GET | `/api/orders/today` | All orders today |
| GET | `/api/orders/today/{status}` | Filter by PENDING/CONFIRMED/NO_REPLY |
| POST | `/api/orders/{id}/confirm` | Confirm with menuItemId or freeText |
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
| `/today-menu` | DailyMenuView | Pick today's offered items |
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
whatsapp.mode=logging
castor.scheduler.send-cron=0 0 10 * * *
castor.scheduler.cutoff-cron=0 30 11 * * *
```

---

## 🧪 How to run

```bash
cd castor-guloso
mv .mvn/maven.config .mvn/maven.config.bak   # optional if -pl interferes
mvn -DskipTests install
mv .mvn/maven.config.bak .mvn/maven.config

mvn spring-boot:run
# → http://localhost:7080
# → http://localhost:7080/dashboard
```

---

## Next recommended work

1. **Real MetaWhatsAppClient** (`whatsapp.mode=meta`) using WebClient
2. **Personalization** — favorite item from last N confirmed orders
3. **Vaadin push / auto-refresh** on Dashboard
4. **Unit/integration tests**
5. **Deployment notes** (Render / Railway / Oracle free tier)
