# 🦫 Castor Guloso — Project Progress

> **Last update:** 2026-11-09 — Customer CRUD delivered (commit abf1744)
> **Previous build:** ✅ SUCCESS — 2026-07-27

---

## 📦 Modules Overview

| Module | Artifact | Status | What it does |
|--------|----------|--------|-------------|
| 🧠 Core | `castor-guloso-core` | ✅ Done | JPA entities, enums, shared DTOs |
| 👤 Customer | `castor-guloso-customer` | ✅ Done | Repos + **CustomerService** + DTOs |
| 📋 Menu | `castor-guloso-menu` | ✅ Done | Menu item CRUD + daily menu services |
| 🧾 Order | `castor-guloso-order` | 🟡 Partial | Order repositories + favorite queries |
| 💬 WhatsApp | `castor-guloso-whatsapp` | ⏳ Stub | Module scaffolded, no logic yet |
| 🖥️ Web (Vaadin) | `castor-guloso-web` | ✅ Done | REST + Vaadin views (Menu + **Customers**) |
| 🚀 App | `castor-guloso-app` | ✅ Done | Spring Boot entry + Flyway migrations |
| 🔧 Build | `builds/build.py` | ✅ Done | Silent Maven runner with timestamped logs |

---

## 🌐 REST API — Available Endpoints

### Menu
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

### Customers (NEW)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/customers` | List all customers |
| `GET` | `/api/customers/active` | List only active customers |
| `GET` | `/api/customers/{id}` | Get by ID |
| `POST` | `/api/customers` | Create customer |
| `PUT` | `/api/customers/{id}` | Update customer |
| `DELETE` | `/api/customers/{id}` | Deactivate customer |
| `POST` | `/api/customers/{id}/tags/{tagId}` | Attach tag (optional notes body) |
| `DELETE` | `/api/customers/{id}/tags/{tagId}` | Remove tag |
| `GET` | `/api/customers/tags/available` | List active tags |

---

## 🖥️ Vaadin UI — Available Views

| Route | View | Description |
|-------|------|-------------|
| `/` | MainView | Landing page with navigation buttons |
| `/customers` | **CustomersView** | **NEW** — Grid + add/edit dialog + tag badges |
| `/menu-items` | MenuItemsView | Grid of all items with add/edit dialog |
| `/today-menu` | DailyMenuView | Checkbox picker to set today's menu |
| `/price-history` | PriceHistoryView | Price history per item |

---

## 📈 Progress vs Milestones

| # | Milestone | Status | Est. Days | Notes |
|---|-----------|--------|-----------|-------|
| M1 | Scaffolding | ✅ Done | — | Modules, entities, repos, Flyway, Vaadin |
| M2 | Menu CRUD | ✅ Done | 2 | Services + REST + Vaadin views |
| M3 | Customer CRUD | ✅ **Done** | 2 | Service + REST + Vaadin + tags |
| M4 | WhatsApp Integration | ⏳ Pending | 3 | Meta setup + webhook |
| M5 | Order Lifecycle | ⏳ Pending | 3 | Scheduler + state machine |
| M6 | Live Dashboard | ⏳ Pending | 2 | Vaadin real-time board |
| M7 | Personalization | ⏳ Pending | 2 | Favorite detection |
| M8 | Finalization & Summary | ⏳ Pending | 1 | Cutoff + kitchen summary |
| M9 | i18n & Localization | ✅ Done | 1 | EN + PT_BR |
| M10 | Customer Categorization | ✅ Done | 2 | Tags seeded, attach/detach in UI |
| M11 | Testing & Polish | ⏳ Pending | 2 | Edge cases, coverage |

---

## 🧪 How to Run

```bash
cd restaurant-app/castor-guloso   # or your local clone path

mvn spring-boot:run
# App → http://localhost:7080
# Customers → http://localhost:7080/customers
```

---

## Next recommended work

1. **Order service** — create DailySession, generate PENDING DailyOrders for active customers
2. **WhatsApp stub client** — log-only sender so the rest of the pipeline can be wired
3. **Scheduler** — 10:00 send + 11:30 cutoff (config already in application.properties)
4. **Live Dashboard** Vaadin view
