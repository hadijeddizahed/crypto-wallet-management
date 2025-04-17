-- Insert sample data into tables
DO $$
DECLARE
    user_id BIGINT;
    wallet_id BIGINT;
BEGIN

    INSERT INTO users (email)
    VALUES ('test@example.com')
    RETURNING id INTO user_id;

    INSERT INTO wallets (user_id)
    VALUES (user_id)
    RETURNING id INTO wallet_id;

    INSERT INTO assets (symbol, price, quantity, wallet_id)
    VALUES ('BTC', 65000.50, 0.025, wallet_id);
    VALUES ('ETH', 65000.50, 0.25, wallet_id);
END $$;