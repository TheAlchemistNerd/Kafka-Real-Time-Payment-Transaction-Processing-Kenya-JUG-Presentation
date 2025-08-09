CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(255) PRIMARY,
    amount NUMERIC (19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);