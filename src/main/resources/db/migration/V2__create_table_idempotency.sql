CREATE TABLE IF NOT EXISTS Idempotency (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--COMMENT ON COLUMN idempotency.idempotency_key IS 'Unique identifier for the idempotency';
--COMMENT ON COLUMN idempotency.transaction_id IS 'The id associated with the transaction';
--COMMENT ON COLUMN idempotency.transaction_amount IS 'The amount of the transaction';
--COMMENT ON COLUMN idempotency.transaction_created_at IS 'The timestamp when the idempotency key was created';