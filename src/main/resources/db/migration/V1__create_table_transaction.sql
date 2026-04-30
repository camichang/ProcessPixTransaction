CREATE TABLE IF NOT EXISTS Transaction (
    id VARCHAR(255) PRIMARY KEY,
    pix_key VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--COMMENT ON COLUMN transaction.id IS 'Unique identifier for the transaction';
--COMMENT ON COLUMN transaction.pix_key IS 'The Pix key associated with the transaction';
--COMMENT ON COLUMN transaction.amount IS 'The amount of the transaction';
--COMMENT ON COLUMN transaction.status IS 'The status of the transaction (e.g., PENDING, APPROVED, REPROVED)';
--COMMENT ON COLUMN transaction.created_at IS 'The timestamp when the transaction was created ';