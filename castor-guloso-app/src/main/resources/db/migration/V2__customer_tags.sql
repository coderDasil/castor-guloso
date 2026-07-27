-- ============================================================================
-- Castor Guloso — V2: Customer Tags / Categorization
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. Tag catalog
-- ---------------------------------------------------------------------------
CREATE TABLE tags (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL UNIQUE,
    type       VARCHAR(20)  NOT NULL,
    color      VARCHAR(7),
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_tag_type CHECK (type IN ('GROUP', 'DIETARY', 'FINANCIAL', 'STATUS'))
);

-- ---------------------------------------------------------------------------
-- 2. Customer-Tag join table
-- ---------------------------------------------------------------------------
CREATE TABLE customer_tags (
    id          BIGSERIAL  PRIMARY KEY,
    customer_id BIGINT     NOT NULL REFERENCES customers(id),
    tag_id      BIGINT     NOT NULL REFERENCES tags(id),
    notes       TEXT,
    created_at  TIMESTAMP  NOT NULL DEFAULT NOW(),

    UNIQUE (customer_id, tag_id)
);

CREATE INDEX idx_customer_tags_customer ON customer_tags(customer_id);
CREATE INDEX idx_customer_tags_tag ON customer_tags(tag_id);

-- ---------------------------------------------------------------------------
-- 3. Seed default tags
-- ---------------------------------------------------------------------------
INSERT INTO tags (name, type, color, active) VALUES
    -- Groups
    ('Construction Worker',  'GROUP',   '#f39c12', TRUE),
    ('Hospital Staff',       'GROUP',   '#3498db', TRUE),
    ('Office Worker',        'GROUP',   '#2ecc71', TRUE),
    ('Factory Worker',       'GROUP',   '#9b59b6', TRUE),
    ('School Staff',         'GROUP',   '#1abc9c', TRUE),
    -- Dietary
    ('Gluten Allergy',       'DIETARY', '#e74c3c', TRUE),
    ('Lactose Intolerant',   'DIETARY', '#e67e22', TRUE),
    ('Diabetic',             'DIETARY', '#e74c3c', TRUE),
    ('Vegetarian',           'DIETARY', '#27ae60', TRUE),
    ('Vegan',                'DIETARY', '#27ae60', TRUE),
    ('Nut Allergy',          'DIETARY', '#c0392b', TRUE),
    ('Seafood Allergy',      'DIETARY', '#c0392b', TRUE),
    -- Financial
    ('Late Payer',           'FINANCIAL', '#f39c12', TRUE),
    ('Pending Invoice',      'FINANCIAL', '#e67e22', TRUE),
    -- Status
    ('Blacklisted',          'STATUS',  '#c0392b', TRUE),
    ('VIP',                  'STATUS',  '#f1c40f', TRUE),
    ('Trial',                'STATUS',  '#3498db', TRUE);
