INSERT INTO card_data (id, card_number, exp_date, cvv, modified_by, created_by, status)
VALUES
    ('3e09f680-1c67-11ec-9621-0242ac130002', '1234567812345678', '2025-12-31', 123, 'system', 'system', 'ACTIVE'),
    ('3e09f680-1c67-11ec-9621-0242ac130003', '2345678923456789', '2026-11-30', 456, 'system', 'system', 'ACTIVE');

INSERT INTO account (id, balance, card_id, currency, modified_by, created_by, status)
VALUES
    ('2a09f680-1c67-11ec-9621-0242ac130002', 100000, '3e09f680-1c67-11ec-9621-0242ac130002', 'USD', 'system', 'system', 'ACTIVE'),
    ('2a09f680-1c67-11ec-9621-0242ac130003', 200000, '3e09f680-1c67-11ec-9621-0242ac130003', 'EUR', 'system', 'system', 'ACTIVE');

INSERT INTO customer (id, first_name, last_name, country, account_id, modified_by, created_by, status)
VALUES
    ('1b09f680-1c67-11ec-9621-0242ac130002', 'John', 'Doel', 'BRL', '2a09f680-1c67-11ec-9621-0242ac130002', 'system', 'system', 'ACTIVE'),
    ('1b09f680-1c67-11ec-9621-0242ac130003', 'Jane', 'Smith', 'Germany', '2a09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'ACTIVE');

INSERT INTO merchant (id, merchant_id, account_id, secret_key, modified_by, created_by, status)
VALUES
    ('4c09f680-1c67-11ec-9621-0242ac130002', 'PROSELYTE', '2a09f680-1c67-11ec-9621-0242ac130002', 'secret123', 'system', 'system', 'ACTIVE'),
    ('4c09f680-1c67-11ec-9621-0242ac130003', 'GLUHOV', '2a09f680-1c67-11ec-9621-0242ac130003', 'secret456', 'system', 'system', 'ACTIVE');

INSERT INTO payment_method (id, type, modified_by, created_by, status)
VALUES
    ('5d09f680-1c67-11ec-9621-0242ac130002', 'CARD', 'system', 'system', 'ACTIVE'),
    ('5d09f680-1c67-11ec-9621-0242ac130003', 'PayPal', 'system', 'system', 'ACTIVE');

INSERT INTO transaction (id, notification_url, payment_method_id, type, currency, amount, language, message, status, customer_id, merchant_id, modified_by, created_by, transaction_status)
VALUES
    ('6e09f680-1c67-11ec-9621-0242ac130002', 'https://proselyte.net/webhook/transaction', '5d09f680-1c67-11ec-9621-0242ac130002', 'transaction', 'USD', 50000, 'en', 'OK', 'ACTIVE', '1b09f680-1c67-11ec-9621-0242ac130002', '4c09f680-1c67-11ec-9621-0242ac130002', 'system', 'system', 'IN_PROGRESS'),
    ('6e09f680-1c67-11ec-9621-0242ac130003', 'https://proselyte.net/webhook/transaction', '5d09f680-1c67-11ec-9621-0242ac130003', 'transaction', 'EUR', 20000, 'de', 'OK', 'ACTIVE', '1b09f680-1c67-11ec-9621-0242ac130003', '4c09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'IN_PROGRESS');

INSERT INTO webhook (id, transaction_id, status, message, modified_by, created_by, transaction_status)
VALUES
    ('7f09f680-1c67-11ec-9621-0242ac130002', '6e09f680-1c67-11ec-9621-0242ac130002', 'ACTIVE', 'Notification sent', 'system', 'system', 'IN_PROGRESS'),
    ('7f09f680-1c67-11ec-9621-0242ac130003', '6e09f680-1c67-11ec-9621-0242ac130003', 'ACTIVE', 'Notification failed', 'system', 'system', 'IN_PROGRESS');