CREATE TABLE IF NOT EXISTS urls (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS url_checks (
    id SERIAL PRIMARY KEY,
    url_id BIGINT NOT NULL REFERENCES urls(id),
    status_code INT,
    title TEXT,
    h1 TEXT,
    description TEXT,
    created_at TIMESTAMP
);
