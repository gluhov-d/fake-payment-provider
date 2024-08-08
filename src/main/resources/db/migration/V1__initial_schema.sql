CREATE TABLE account (
                         id UUID PRIMARY KEY,
                         balance BIGINT NOT NULL,
                         owner_id UUID NOT NULL,
                         owner_type VARCHAR(255) NOT NULL,
                         currency VARCHAR(255) NOT NULL,
                         status VARCHAR(255),
                         modified_by VARCHAR(255),
                         created_by VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE card_data (
                           id UUID PRIMARY KEY,
                           card_number VARCHAR(255) NOT NULL,
                           exp_date TIMESTAMP NOT NULL,
                           cvv INT NOT NULL,
                           account_id UUID,
                           status VARCHAR(255),
                           modified_by VARCHAR(255),
                           created_by VARCHAR(255),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE customer (
                          id UUID PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          country VARCHAR(255) NOT NULL,
                          account_id UUID,
                          status VARCHAR(255),
                          modified_by VARCHAR(255),
                          created_by VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE merchant (
                          id UUID PRIMARY KEY,
                          merchant_id VARCHAR(255) NOT NULL UNIQUE,
                          account_id UUID,
                          secret_key VARCHAR(255) NOT NULL,
                          status VARCHAR(255),
                          modified_by VARCHAR(255),
                          created_by VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (account_id) REFERENCES account(id)
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
                             id UUID PRIMARY KEY,
                             notification_url VARCHAR(255),
                             payment_method_id UUID,
                             type VARCHAR(255) NOT NULL,
                             currency VARCHAR(255) NOT NULL,
                             amount BIGINT NOT NULL,
                             language VARCHAR(255),
                             message VARCHAR(255),
                             transaction_status VARCHAR(255),
                             status VARCHAR(255),
                             card_id UUID,
                             customer_id UUID,
                             merchant_id UUID,
                             modified_by VARCHAR(255),
                             created_by VARCHAR(255),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (payment_method_id) REFERENCES payment_method(id),
                             FOREIGN KEY (customer_id) REFERENCES customer(id),
                             FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE TABLE webhook (
                         id UUID PRIMARY KEY,
                         transaction_id UUID,
                         transaction_status VARCHAR(255),
                         status VARCHAR(255),
                         message VARCHAR(255),
                         modified_by VARCHAR(255),
                         created_by VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (transaction_id) REFERENCES transaction(id)
);