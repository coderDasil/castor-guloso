# 🦫 Castor Guloso — Project Progress

> **Last build:** ✅ SUCCESS — `main` (a662320)
> **Built:** 2026-07-27

---

## 📦 Modules Overview

| Module | Artifact | Status | What it does |
|--------|----------|--------|-------------|
| 🧠 Core | `castor-guloso-core` | ✅ Done | JPA entities, enums, shared DTOs |
| 👤 Customer | `castor-guloso-customer` | ✅ Done | Repositories only (no UI yet) |
| 📋 Menu | `castor-guloso-menu` | ✅ Done | Menu item CRUD + daily menu services |
| 🧾 Order | `castor-guloso-order` | ✅ Done | Order repositories + favorite queries |
| 💬 WhatsApp | `castor-guloso-whatsapp` | ⏳ Stub | Module scaffolded, no logic yet |
| 🖥️ Web (Vaadin) | `castor-guloso-web` | ✅ Done | REST controllers, Vaadin views, i18n |
| 🚀 App | `castor-guloso-app` | ✅ Done | Spring Boot entry + Flyway migrations |
| 🔧 Build | `builds/build.py` | ✅ Done | Silent Maven runner with timestamped logs |

---

## 🌐 REST API — Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/menu-items` | List all menu items |
| `GET` | `/api/menu-items/available` | List only available items |
| `GET` | `/api/menu-items/{id}` | Get item by ID |
| `POST` | `/api/menu-items` | Create new item |
| `PUT` | `/api/menu-items/{id}` | Update existing item |
| `DELETE` | `/api/menu-items/{id}` | Delete item |
| `GET` | `/api/daily-menus/today` | Get today's menu |
| `GET` | `/api/daily-menus/{date}` | Get menu for a date |
| `POST` | `/api/daily-menus` | Set today's menu |

---

## 🖥️ Vaadin UI — Available Views

| Route | View | Description |
|-------|------|-------------|
| `/` | MainView | Landing page with navigation buttons |
| `/menu-items` | MenuItemsView | Grid of all items with **add/edit** dialog |
| `/today-menu` | DailyMenuView | Checkbox picker to set today's menu |

---

## 🗄️ Database Schema

### Table: `menu_items`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `name` | `VARCHAR(120)` | Unique, not null |
| `description` | `TEXT` | Optional |
| `available` | `BOOLEAN` | Default `true` |
| `created_at` | `TIMESTAMP` | Auto-set |

### Table: `daily_menus`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `menu_date` | `DATE` | Unique, not null |
| `created_at` | `TIMESTAMP` | Auto-set |

### Table: `daily_menu_items`
| Column | Type | Notes |
|--------|------|-------|
| `daily_menu_id` | `BIGINT` | FK → `daily_menus.id` |
| `menu_item_id` | `BIGINT` | FK → `menu_items.id` |

### Table: `customers`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `name` | `VARCHAR(100)` | Not null |
| `phone` | `VARCHAR(20)` | Unique, not null |
| `active` | `BOOLEAN` | Default `true` |
| `notes` | `TEXT` | Optional |
| `created_at` | `TIMESTAMP` | Auto-set |

### Table: `daily_sessions`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `session_date` | `DATE` | Not null |
| `status` | `VARCHAR(20)` | `OPEN` / `CLOSED` |
| `cutoff_time` | `TIMESTAMP` | Optional |
| `created_at` | `TIMESTAMP` | Auto-set |

### Table: `daily_orders`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `session_id` | `BIGINT` | FK → `daily_sessions.id` |
| `customer_id` | `BIGINT` | FK → `customers.id` |
| `menu_item_id` | `BIGINT` | FK → `menu_items.id` (nullable) |
| `free_text_item` | `VARCHAR(120)` | For custom choices |
| `status` | `VARCHAR(20)` | `PENDING` / `CONFIRMED` / `NO_REPLY` |
| `whatsapp_message_id` | `VARCHAR(255)` | For tracking |
| `replied_at` | `TIMESTAMP` | When client responded |
| `created_at` | `TIMESTAMP` | Auto-set |

### Table: `tags`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `name` | `VARCHAR(80)` | Unique, not null |
| `type` | `VARCHAR(20)` | `GROUP` / `DIETARY` / `FINANCIAL` / `STATUS` |
| `color` | `VARCHAR(7)` | Hex color for UI badges |
| `active` | `BOOLEAN` | Default `true` |
| `created_at` | `TIMESTAMP` | Auto-set |

### Table: `customer_tags`
| Column | Type | Notes |
|--------|------|-------|
| `id` | `BIGSERIAL` | PK |
| `customer_id` | `BIGINT` | FK → `customers.id` |
| `tag_id` | `BIGINT` | FK → `tags.id` |
| `notes` | `TEXT` | Per-association context |
| `created_at` | `TIMESTAMP` | Auto-set |

---

## 🏷️ Seed Tags (17 pre-loaded)

| Group | Dietary | Financial | Status |
|-------|---------|-----------|--------|
| 👷 Construction Worker | 🌾 Gluten Allergy | 💰 Late Payer | 🚫 Blacklisted |
| 🏥 Hospital Staff | 🥛 Lactose Intolerant | 📄 Pending Invoice | ⭐ VIP |
| 🏢 Office Worker | 💉 Diabetic | | 🆕 Trial |
| 🏭 Factory Worker | 🥦 Vegetarian | | |
| 🏫 School Staff | 🌱 Vegan | | |
| | 🥜 Nut Allergy | | |
| | 🦐 Seafood Allergy | | |

---

## 🌐 Internationalization

| Locale | File | Status |
|--------|------|--------|
| 🇺🇸 English (default) | `messages.properties` | ✅ ~50 keys |
| 🇧🇷 Brazilian Portuguese | `messages_pt_BR.properties` | ✅ ~50 keys |

All Vaadin views use `getTranslation("key")`. A **LocaleToggle** component (🇧🇷 PT / 🇺🇸 EN) is on the main page.

---

## 🧪 How to Run

```bash
cd restaurant-app/castor-guloso

# Development mode (H2 database, no PostgreSQL needed)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production mode (PostgreSQL)
mvn spring-boot:run

# Build only (silent, with log)
python3 builds/build.py
```

App opens at **http://localhost:7080**

---

## 📈 Progress vs Milestones

| # | Milestone | Status | Est. Days | Notes |
|---|-----------|--------|-----------|-------|
| M1 | Scaffolding | ✅ Done | — | Modules, entities, repos, Flyway, Vaadin |
| M2 | Menu CRUD | ✅ Done | 2 | Services + REST + Vaadin views |
| M3 | Customer CRUD | ⏳ Pending | 2 | Next up |
| M4 | WhatsApp Integration | ⏳ Pending | 3 | Meta setup + webhook |
| M5 | Order Lifecycle | ⏳ Pending | 3 | Scheduler + state machine |
| M6 | Live Dashboard | ⏳ Pending | 2 | Vaadin real-time board |
| M7 | Personalization | ⏳ Pending | 2 | Favorite detection |
| M8 | Finalization & Summary | ⏳ Pending | 1 | Cutoff + kitchen summary |
| M9 | i18n & Localization | ✅ Done | 1 | EN + PT_BR |
| M10 | Customer Categorization | ✅ Done | 2 | Tags seeded, repos ready |
| M11 | Testing & Polish | ⏳ Pending | 2 | Edge cases, coverage |

---

## 🏗️ Project Structure

```
restaurant-app/castor-guloso/
├── builds/
│   └── build.py              # Silent build runner
├── docs/
│   └── CASTOR_GULOSO_YOUTRACK_SCOPE.md   # Project charter
├── pom.xml                   # Parent POM
├── castor-guloso-core/       # Domain entities + enums
├── castor-guloso-customer/   # Customer + tag repositories
├── castor-guloso-menu/       # Menu services + DTOs
├── castor-guloso-order/      # Order repositories
├── castor-guloso-whatsapp/   # WhatsApp module (stub)
├── castor-guloso-web/        # REST controllers + Vaadin views
└── castor-guloso-app/        # Spring Boot main + Flyway
```
