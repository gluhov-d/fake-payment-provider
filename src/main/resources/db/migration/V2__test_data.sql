INSERT INTO account (id, balance, owner_id, owner_type, currency, modified_by, created_by, status)
VALUES
    ('2a09f680-1c67-11ec-9621-0242ac130002', 100000, '1b09f680-1c67-11ec-9621-0242ac130002', 'customer', 'USD', 'system', 'system', 'ACTIVE'),
    ('2a09f680-1c67-11ec-9621-0242ac130003', 200000, '1b09f680-1c67-11ec-9621-0242ac130003', 'customer', 'EUR', 'system', 'system', 'ACTIVE'),
    ('2a09f680-1c67-11ec-9621-0242ac130004', 200000, '4c09f680-1c67-11ec-9621-0242ac130002', 'merchant', 'EUR', 'system', 'system', 'ACTIVE'),
    ('2a09f680-1c67-11ec-9621-0242ac130005', 200000, '4c09f680-1c67-11ec-9621-0242ac130003', 'merchant', 'EUR', 'system', 'system', 'ACTIVE');

INSERT INTO card_data (id, card_number, exp_date, cvv, account_id, modified_by, created_by, status)
VALUES
    ('3e09f680-1c67-11ec-9621-0242ac130002', '1234567812345678', '2025-12-31', 123, '2a09f680-1c67-11ec-9621-0242ac130002', 'system', 'system', 'ACTIVE'),
    ('3e09f680-1c67-11ec-9621-0242ac130003', '2345678923456789', '2026-11-30', 456, '2a09f680-1c67-11ec-9621-0242ac130002', 'system', 'system', 'ACTIVE'),
    ('3e09f680-1c67-11ec-9621-0242ac130004', '2345678923456788', '2026-11-30', 455, '2a09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'ACTIVE'),
    ('3e09f680-1c67-11ec-9621-0242ac130005', '2345678923456787', '2026-11-30', 453, '2a09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'ACTIVE');

INSERT INTO customer (id, first_name, last_name, country, account_id, modified_by, created_by, status)
VALUES
    ('1b09f680-1c67-11ec-9621-0242ac130002', 'John', 'Doel', 'BRL', '2a09f680-1c67-11ec-9621-0242ac130002', 'system', 'system', 'ACTIVE'),
    ('1b09f680-1c67-11ec-9621-0242ac130003', 'Jane', 'Smith', 'Germany', '2a09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'ACTIVE');

INSERT INTO merchant (id, merchant_id, account_id, secret_key, modified_by, created_by, status)
VALUES
    ('4c09f680-1c67-11ec-9621-0242ac130002', 'PROSELYTE', '2a09f680-1c67-11ec-9621-0242ac130004', 'secret123', 'system', 'system', 'ACTIVE'),
    ('4c09f680-1c67-11ec-9621-0242ac130003', 'GLUHOV', '2a09f680-1c67-11ec-9621-0242ac130005', 'secret456', 'system', 'system', 'ACTIVE');

INSERT INTO payment_method (id, type, modified_by, created_by, status)
VALUES
    ('5d09f680-1c67-11ec-9621-0242ac130002', 'CARD', 'system', 'system', 'ACTIVE');

INSERT INTO transaction (id, notification_url, payment_method_id, type, currency, amount, language, message, status, customer_id, merchant_id, card_id, modified_by, created_by, transaction_status, created_at)
VALUES
    ('6e09f680-1c67-11ec-9621-0242ac130002', 'https://proselyte.net/webhook/transaction', '5d09f680-1c67-11ec-9621-0242ac130002', 'transaction', 'USD', 500, 'en', 'OK', 'ACTIVE', '1b09f680-1c67-11ec-9621-0242ac130002', '4c09f680-1c67-11ec-9621-0242ac130002','3e09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'IN_PROGRESS', CURRENT_DATE - 1),
    ('6e09f680-1c67-11ec-9621-0242ac130003', 'https://proselyte.net/webhook/payout', '5d09f680-1c67-11ec-9621-0242ac130002', 'payout', 'EUR', 200, 'de', 'OK', 'ACTIVE', '1b09f680-1c67-11ec-9621-0242ac130003', '4c09f680-1c67-11ec-9621-0242ac130002', '3e09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'IN_PROGRESS', CURRENT_TIMESTAMP);

INSERT INTO webhook (id, transaction_id, status, message, modified_by, created_by, transaction_status)
VALUES
    ('7f09f680-1c67-11ec-9621-0242ac130002', '6e09f680-1c67-11ec-9621-0242ac130002', 'ACTIVE', 'Notification sent', 'system', 'system', 'IN_PROGRESS'),
    ('7f09f680-1c67-11ec-9621-0242ac130003', '6e09f680-1c67-11ec-9621-0242ac130003', 'ACTIVE', 'Notification failed', 'system', 'system', 'IN_PROGRESS');