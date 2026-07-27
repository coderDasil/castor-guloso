-- ============================================================================
-- Castor Guloso — Initial Schema
-- Migration V1: core tables for customer, menu, session, and order management
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. Menu item catalog (normalised, not JSONB)
-- ---------------------------------------------------------------------------
CREATE TABLE menu_items (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    available   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------------------
-- 2. Daily menu — which items are offered on a given day
-- ---------------------------------------------------------------------------
CREATE TABLE daily_menus (
    id         BIGSERIAL  PRIMARY KEY,
    menu_date  DATE       NOT NULL UNIQUE,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW()
);

-- Join table for the many-to-many relationship
CREATE TABLE daily_menu_items (
    daily_menu_id BIGINT NOT NULL REFERENCES daily_menus(id),
    menu_item_id  BIGINT NOT NULL REFERENCES menu_items(id),
    PRIMARY KEY (daily_menu_id, menu_item_id)
);

-- ---------------------------------------------------------------------------
-- 3. Customers
-- ---------------------------------------------------------------------------
CREATE TABLE customers (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    phone      VARCHAR(20)  NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    notes      TEXT,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------------------
-- 4. Daily sessions (groups orders by day)
-- ---------------------------------------------------------------------------
CREATE TABLE daily_sessions (
    id           BIGSERIAL       PRIMARY KEY,
    session_date DATE            NOT NULL,
    status       VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    cutoff_time  TIMESTAMP,
    created_at   TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_session_status CHECK (status IN ('OPEN', 'CLOSED'))
);

CREATE INDEX idx_sessions_date_status ON daily_sessions(session_date, status);

-- ---------------------------------------------------------------------------
-- 5. Orders (per client, per daily session)
-- ---------------------------------------------------------------------------
CREATE TABLE daily_orders (
    id                  BIGSERIAL    PRIMARY KEY,
    session_id          BIGINT       NOT NULL REFERENCES daily_sessions(id),
    customer_id         BIGINT       NOT NULL REFERENCES customers(id),
    menu_item_id        BIGINT       REFERENCES menu_items(id),
    free_text_item      VARCHAR(120),
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    whatsapp_message_id VARCHAR(255),
    replied_at          TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'CONFIRMED', 'NO_REPLY'))
);

CREATE INDEX idx_orders_session ON daily_orders(session_id);
CREATE INDEX idx_orders_session_customer ON daily_orders(session_id, customer_id);
