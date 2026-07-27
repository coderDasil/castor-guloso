# 🦫 Castor Guloso — Developer Guide

> **Project root:** `restaurant-app/castor-guloso/`
> **Built with:** Java 17 + Spring Boot 3.3.2 + Vaadin 24 + PostgreSQL / H2

---

## 📋 Prerequisites

| Tool | Version | Check with |
|------|---------|-----------|
| Java | 17+ | `java --version` |
| Maven | 3.8+ | `mvn --version` |
| PostgreSQL | 15+ (optional — dev mode uses H2) | `psql --version` |

---

## 🚀 Quick Start (Development)

```bash
# 1. Navigate to project
cd restaurant-app/castor-guloso

# 2. Run with H2 database (no PostgreSQL needed)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Open in browser
#    http://localhost:7080
#    H2 Console: http://localhost:7080/h2-console
```

> The `.mvn/maven.config` file makes `mvn spring-boot:run` automatically target the app module — no need to specify `-pl castor-guloso-app`.

---

## 🔨 Build Commands

### Quick build (with log)

```bash
python3 builds/build.py
```

Produces timestamped logs in `builds/build-YYYYMMDD-HHMMSS.log`.

### Standard Maven

```bash
# Full clean compile
mvn clean compile

# Package executable JAR
mvn clean package -DskipTests

# Package with Vaadin production optimizations
mvn clean package -DskipTests -Pproduction

# Run tests
mvn test
```

---

## 🗄️ Database

### Development (default)

Uses **H2 file-based** database — no install needed:
- JDBC: `jdbc:h2:file:./data/castor_guloso_dev`
- Console: `http://localhost:7080/h2-console`
- No password required

### Production (PostgreSQL)

1. Create a database and user:

```sql
CREATE DATABASE castor_guloso;
CREATE USER castor WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE castor_guloso TO castor;
```

2. Set the password via environment variable:

```bash
export CASTOR_DB_PASSWORD=your_password
```

3. Run with the `prod` profile (or no profile — it defaults to PostgreSQL):

```bash
mvn spring-boot:run
```

---

## 📁 Project Structure

```
castor-guloso/
├── .mvn/maven.config           # Default Maven project target
├── builds/build.py             # Silent build runner
├── docs/                       # Documentation
│   ├── CASTOR_GULOSO_YOUTRACK_SCOPE.md
│   ├── CASTOR_GULOSO_PROGRESS.md
│   └── DEV_GUIDE.md            ← you are here
├── pom.xml                     # Parent POM
├── castor-guloso-core/         # Domain entities + enums
├── castor-guloso-customer/     # Customer + tag repositories
├── castor-guloso-menu/         # Menu services + DTOs
├── castor-guloso-order/        # Order repositories + queries
├── castor-guloso-whatsapp/     # WhatsApp module (stub)
├── castor-guloso-web/          # REST controllers + Vaadin UI views
└── castor-guloso-app/          # Spring Boot entry + Flyway migrations
```

---

## 🌐 REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/menu-items` | List all menu items |
| `GET` | `/api/menu-items/available` | List only available items |
| `GET` | `/api/menu-items/{id}` | Get item by ID |
| `POST` | `/api/menu-items` | Create new item |
| `PUT` | `/api/menu-items/{id}` | Update item |
| `DELETE` | `/api/menu-items/{id}` | Delete item |
| `GET` | `/api/daily-menus/today` | Get today's menu |
| `GET` | `/api/daily-menus/{date}` | Get menu by date (YYYY-MM-DD) |
| `POST` | `/api/daily-menus` | Set menu for a date |

---

## 🖥️ Vaadin UI Routes

| Route | View | Description |
|-------|------|-------------|
| `/` | MainView | Landing page with navigation |
| `/menu-items` | MenuItemsView | CRUD grid for menu catalog |
| `/today-menu` | DailyMenuView | Pick today's offered items |

---

## 🌐 Internationalization

App supports **English** (default) and **Brazilian Portuguese**.

Message files live in `castor-guloso-web/src/main/resources/`:

| File | Locale |
|------|--------|
| `messages.properties` | 🇺🇸 English |
| `messages_pt_BR.properties` | 🇧🇷 Portuguese |

All Vaadin views use `getTranslation("key")`. A language toggle is on the main page.

---

## 📱 Running on a Phone (Termux Demo)

Great for showing the restaurant owner a live demo without a server.

### Option A: Executable JAR (recommended)

**On your laptop — build the JAR:**

```bash
cd restaurant-app/castor-guloso

# Build with dev profile (H2 database bundled)
mvn clean package -DskipTests
```

**Transfer to the phone:**

```bash
# Via ADB (USB debugging enabled)
adb push castor-guloso-app/target/castor-guloso-app-0.0.1-SNAPSHOT.jar \
  /sdcard/Download/

# Or just use USB file transfer — copy the JAR to Downloads/
```

**On Termux (one-time setup):**

```bash
# Install Java 17
pkg update && pkg install openjdk-17

# Navigate to the JAR
cd ~/storage/downloads

# Run it — accessible from any device connected to the phone's hotspot
java -jar castor-guloso-app-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --server.address=0.0.0.0
```

**On the owner's phone:**

1. S23 creates a **WiFi hotspot**
2. Owner's phone connects to the hotspot
3. Browser → `http://192.168.X.X:7080` (use the S23's hotspot IP)
4. Full Vaadin admin panel works in the browser

### Option B: Full development on the phone

```bash
# Install tools
pkg install openjdk-17 git maven

# Clone or copy the repo
cp -r /sdcard/Download/castor-guloso ~/

# Run (slower but allows code editing)
mvn spring-boot:run -Dspring-boot.run.profiles=dev -f castor-guloso/pom.xml
```

---

## 🐛 Troubleshooting

### "Cannot load driver class: org.h2.Driver"

H2 is in `runtime` scope in `castor-guloso-app/pom.xml`. If still failing:

```bash
# Force clean download
mvn dependency:purge-local-repository
```

### "Port 7080 already in use"

```bash
# Find and kill the process
lsof -i :7080
kill -9 <PID>
```

### Flyway migration errors

```bash
# H2 file-based DB
rm -rf data/

# PostgreSQL
DROP DATABASE castor_guloso;
CREATE DATABASE castor_guloso;
```

---

## 🔧 Profile-Specific Configuration

### `application-dev.properties` (H2)

```properties
spring.datasource.url=jdbc:h2:file:./data/castor_guloso_dev;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.show-sql=true
vaadin.productionMode=false
```

### `application.properties` (default = production / PostgreSQL)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/castor_guloso
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
vaadin.productionMode=false   # set true for production build
```

---

## ✅ Git Workflow

```bash
# Check status
git status

# Stage and commit
git add -A
git commit -m "description of changes"

# View history
git log --oneline
```

---

## 📦 Dependency quick reference

| Dependency | Module | Purpose |
|-----------|--------|---------|
| Spring Boot 3.3.2 | All | Framework |
| Vaadin 24.4.4 | web | Java frontend UI |
| Flyway | app | Database migrations |
| H2 | app (runtime) | Dev database |
| PostgreSQL | app (runtime) | Production database |
| Lombok | All (optional) | Boilerplate reduction |
| Spring Boot Actuator | app | Health + metrics |
| Spring Boot DevTools | app (runtime) | LiveReload + restart |

---

> **Questions?** Ask coderDasil (Renato) or open a ticket on YouTrack.
