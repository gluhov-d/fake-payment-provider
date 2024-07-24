CREATE TABLE card_data (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           card_number VARCHAR(255) NOT NULL,
                           exp_date TIMESTAMP NOT NULL,
                           cvv INT NOT NULL,
                           status VARCHAR(255),
                           modified_by VARCHAR(255),
                           created_by VARCHAR(255),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         balance BIGINT NOT NULL,
                         card_id UUID REFERENCES card_data(id),
                         currency VARCHAR(255) NOT NULL,
                         status VARCHAR(255),
                         modified_by VARCHAR(255),
                         created_by VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          country VARCHAR(255) NOT NULL,
                          account_id UUID REFERENCES account(id),
                          status VARCHAR(255),
                          modified_by VARCHAR(255),
                          created_by VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE merchant (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          merchant_id VARCHAR(255) NOT NULL UNIQUE,
                          account_id UUID REFERENCES account(id),
                          secret_key VARCHAR(255) NOT NULL,
                          status VARCHAR(255),
                          modified_by VARCHAR(255),
                          created_by VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_method (
                                id UUID PRIMARY KEY,
                                type VARCHAR(255) NOT NULL,
                                status VARCHAR(255),
                                modified_by VARCHAR(255),
                                created_by VARCHAR(255),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             notification_url VARCHAR(255),
                             payment_method_id UUID REFERENCES payment_method(id),
                             type VARCHAR(255) NOT NULL,
                             currency VARCHAR(255) NOT NULL,
                             amount BIGINT NOT NULL,
                             language VARCHAR(255),
                             message VARCHAR(255),
                             transaction_status VARCHAR(255),
                             status VARCHAR(255),
                             customer_id UUID REFERENCES customer(id),
                             merchant_id UUID REFERENCES merchant(id),
                             modified_by VARCHAR(255),
                             created_by VARCHAR(255),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE webhook (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         transaction_id UUID REFERENCES transaction(id),
                         transaction_status VARCHAR(255),
                         status VARCHAR(255),
                         message VARCHAR(255),
                         modified_by VARCHAR(255),
                         created_by VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE account ADD CONSTRAINT fk_card_id FOREIGN KEY (card_id) REFERENCES card_data(id);