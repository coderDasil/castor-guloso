-- ============================================================================
-- Castor Guloso — V3: Menu Pricing
-- ============================================================================
-- Tracks price history for menu items via effective date ranges.
-- A row with effective_to IS NULL is the current active price.
-- When a price changes, close the old row and insert a new one.

CREATE TABLE menu_prices (
    id              BIGSERIAL       PRIMARY KEY,
    menu_item_id    BIGINT          NOT NULL REFERENCES menu_items(id),
    price           DECIMAL(10,2)   NOT NULL,
    effective_from  DATE            NOT NULL DEFAULT CURRENT_DATE,
    effective_to    DATE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_menu_prices_item ON menu_prices(menu_item_id);
CREATE INDEX idx_menu_prices_active ON menu_prices(menu_item_id, effective_from, effective_to);
