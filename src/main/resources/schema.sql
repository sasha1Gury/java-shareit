CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    available BOOLEAN,
    owner BIGINT,
    request BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner_to_user FOREIGN KEY (owner) REFERENCES users(id),
    CONSTRAINT fk_request FOREIGN KEY (owner) REFERENCES item_request(id)
);

CREATE TABLE IF NOT EXISTS item_request (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description TEXT,
    owner BIGINT,
    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT fk_request_owner FOREIGN KEY (owner) REFERENCES users(id)
);
