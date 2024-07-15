INSERT INTO card_data (id, card_number, exp_date, cvv, updated_by, created_by)
VALUES
    ('3e09f680-1c67-11ec-9621-0242ac130002', '1234567812345678', '2025-12-31', 123, 'system', 'system'),
    ('3e09f680-1c67-11ec-9621-0242ac130003', '2345678923456789', '2026-11-30', 456, 'system', 'system');

INSERT INTO account (id, balance, card_id, currency, updated_by, created_by)
VALUES
    ('2a09f680-1c67-11ec-9621-0242ac130002', 100000, '3e09f680-1c67-11ec-9621-0242ac130002', 'USD', 'system', 'system'),
    ('2a09f680-1c67-11ec-9621-0242ac130003', 200000, '3e09f680-1c67-11ec-9621-0242ac130003', 'EUR', 'system', 'system');

INSERT INTO customer (id, first_name, last_name, country, account_id, updated_by, created_by)
VALUES
    ('1b09f680-1c67-11ec-9621-0242ac130002', 'John', 'Doe', 'USA', '2a09f680-1c67-11ec-9621-0242ac130002', 'system', 'system'),
    ('1b09f680-1c67-11ec-9621-0242ac130003', 'Jane', 'Smith', 'Germany', '2a09f680-1c67-11ec-9621-0242ac130003', 'system', 'system');

INSERT INTO merchant (id, account_id, secret_key, updated_by, created_by)
VALUES
    ('4c09f680-1c67-11ec-9621-0242ac130002', '2a09f680-1c67-11ec-9621-0242ac130002', 'secret123', 'system', 'system'),
    ('4c09f680-1c67-11ec-9621-0242ac130003', '2a09f680-1c67-11ec-9621-0242ac130003', 'secret456', 'system', 'system');

INSERT INTO payment_method (id, type, updated_by, created_by)
VALUES
    ('5d09f680-1c67-11ec-9621-0242ac130002', 'Credit Card', 'system', 'system'),
    ('5d09f680-1c67-11ec-9621-0242ac130003', 'PayPal', 'system', 'system');

INSERT INTO transaction (id, notification_url, payment_method_id, type, currency, amount, language, message, status, customer_id, merchant_id, updated_by, created_by, transaction_status)
VALUES
    ('6e09f680-1c67-11ec-9621-0242ac130002', 'https://example.com/notify', '5d09f680-1c67-11ec-9621-0242ac130002', 'purchase', 'USD', 50000, 'en', 'Payment for Order #123', 'completed', '1b09f680-1c67-11ec-9621-0242ac130002', '4c09f680-1c67-11ec-9621-0242ac130002', 'system', 'system', 'IN_PROGRESS'),
    ('6e09f680-1c67-11ec-9621-0242ac130003', 'https://example.com/notify', '5d09f680-1c67-11ec-9621-0242ac130003', 'refund', 'EUR', 20000, 'de', 'Refund for Order #456', 'pending', '1b09f680-1c67-11ec-9621-0242ac130003', '4c09f680-1c67-11ec-9621-0242ac130003', 'system', 'system', 'IN_PROGRESS');

INSERT INTO webhook (id, transaction_id, status, message, updated_by, created_by, transaction_status)
VALUES
    ('7f09f680-1c67-11ec-9621-0242ac130002', '6e09f680-1c67-11ec-9621-0242ac130002', 'success', 'Notification sent', 'system', 'system', 'IN_PROGRESS'),
    ('7f09f680-1c67-11ec-9621-0242ac130003', '6e09f680-1c67-11ec-9621-0242ac130003', 'failed', 'Notification failed', 'system', 'system', 'IN_PROGRESS');