-- ============================================================================
-- Castor Guloso — V4: Seed Data for Demo
-- ============================================================================
-- Populates menu items, pricing history, customers, and tags
-- so the demo is convincing from the first run.

-- ---------------------------------------------------------------------------
-- 1. Menu items (5 items with real descriptions)
-- ---------------------------------------------------------------------------
INSERT INTO menu_items (name, description, available) VALUES
    ('Prato Feito',      'Arroz, feijão, bife acebolado, batata frita e salada',            TRUE),
    ('Bife com Fritas',  'Bife grelhado com batatas fritas crocantes, arroz e feijão',     TRUE),
    ('Filé de Frango',   'Filé de frango grelhado com legumes salteados, arroz e salada',  TRUE),
    ('Salada Completa',  'Salada mista com folhas, tomate, cenoura, beterraba e frango',   TRUE),
    ('Vegetariano do Dia','Prato vegetariano com proteína de soja, legumes e arroz integral', TRUE);

-- ---------------------------------------------------------------------------
-- 2. Pricing history (2-3 price changes per item for a rich history view)
-- ---------------------------------------------------------------------------
-- Prato Feito: R$ 15 → R$ 17 → R$ 18
INSERT INTO menu_prices (menu_item_id, price, effective_from, effective_to) VALUES
    (1, 15.00, '2025-01-01', '2025-06-30'),
    (1, 17.00, '2025-07-01', '2026-03-31'),
    (1, 18.00, '2026-04-01', NULL);

-- Bife com Fritas: R$ 18 → R$ 20 → R$ 22
INSERT INTO menu_prices (menu_item_id, price, effective_from, effective_to) VALUES
    (2, 18.00, '2025-01-01', '2025-08-31'),
    (2, 20.00, '2025-09-01', '2026-02-28'),
    (2, 22.00, '2026-03-01', NULL);

-- Filé de Frango: R$ 17 → R$ 20
INSERT INTO menu_prices (menu_item_id, price, effective_from, effective_to) VALUES
    (3, 17.00, '2025-01-01', '2025-11-30'),
    (3, 20.00, '2025-12-01', NULL);

-- Salada Completa: R$ 14 → R$ 16
INSERT INTO menu_prices (menu_item_id, price, effective_from, effective_to) VALUES
    (4, 14.00, '2025-01-01', '2026-01-31'),
    (4, 16.00, '2026-02-01', NULL);

-- Vegetariano do Dia: R$ 19
INSERT INTO menu_prices (menu_item_id, price, effective_from, effective_to) VALUES
    (5, 19.00, '2025-06-01', NULL);

-- ---------------------------------------------------------------------------
-- 3. Sample customers (5 with varied tags for a realistic roster)
-- ---------------------------------------------------------------------------
INSERT INTO customers (name, phone, active, notes) VALUES
    ('Construtora Alpha Ltda',     '5511955551001', TRUE,  'Obra na Rua Augusta — 25 marmitas/dia'),
    ('Hospital São Lucas',         '5511955552002', TRUE,  'Setor administrativo — 15 pedidos/dia'),
    ('Escola Primeiros Passos',    '5511955553003', TRUE,  'Professores e funcionários — 12 marmitas'),
    ('Escritório Bresser Tower',   '5511955554004', TRUE,  'Andar 12 — 8 pedidos fixos'),
    ('Padaria Pão Nosso',          '5511955555005', TRUE,  'Funcionários — 5 pedidos, delivery');

-- ---------------------------------------------------------------------------
-- 4. Tag assignments
-- ---------------------------------------------------------------------------
-- Tags were seeded in V2. Map by name.
-- Construtora Alpha → Construction Worker
INSERT INTO customer_tags (customer_id, tag_id) VALUES (1, (SELECT id FROM tags WHERE name = 'Construction Worker'));
-- Hospital São Lucas → Hospital Staff + Late Payer
INSERT INTO customer_tags (customer_id, tag_id) VALUES (2, (SELECT id FROM tags WHERE name = 'Hospital Staff'));
INSERT INTO customer_tags (customer_id, tag_id, notes) VALUES (2, (SELECT id FROM tags WHERE name = 'Late Payer'), 'Fatura 003 pendente — 15 dias');
-- Escola → School Staff
INSERT INTO customer_tags (customer_id, tag_id) VALUES (3, (SELECT id FROM tags WHERE name = 'School Staff'));
-- Escritório → Office Worker
INSERT INTO customer_tags (customer_id, tag_id) VALUES (4, (SELECT id FROM tags WHERE name = 'Office Worker'));
-- Padaria → Blacklisted (for demo purposes — shows the badge)
INSERT INTO customer_tags (customer_id, tag_id, notes) VALUES (5, (SELECT id FROM tags WHERE name = 'Blacklisted'), 'Cancelou pagamento do mes passado');
