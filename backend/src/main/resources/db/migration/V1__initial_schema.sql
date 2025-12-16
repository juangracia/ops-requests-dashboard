CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    manager_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_users_manager FOREIGN KEY (manager_id) REFERENCES users(id)
);

CREATE TABLE request_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE requests (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    manager_id BIGINT,
    type_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    amount DECIMAL(10, 2),
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_requests_requester FOREIGN KEY (requester_id) REFERENCES users(id),
    CONSTRAINT fk_requests_manager FOREIGN KEY (manager_id) REFERENCES users(id),
    CONSTRAINT fk_requests_type FOREIGN KEY (type_id) REFERENCES request_types(id)
);

CREATE TABLE request_comments (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_request FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE request_audit_events (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50),
    note TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_audit_request FOREIGN KEY (request_id) REFERENCES requests(id),
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES users(id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_manager_id ON users(manager_id);
CREATE INDEX idx_requests_requester_id ON requests(requester_id);
CREATE INDEX idx_requests_manager_id ON requests(manager_id);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_request_comments_request_id ON request_comments(request_id);
CREATE INDEX idx_request_audit_events_request_id ON request_audit_events(request_id);
