INSERT INTO users (email, password, role, manager_id, active, created_at) VALUES
('admin@example.com', '$2a$10$8K1p/a0dL3.MX.6/NhYwReU.bDDCzWz6LvT8eGHXgLZNFwvpJYnhm', 'ADMIN', NULL, TRUE, NOW()),
('manager@example.com', '$2a$10$8K1p/a0dL3.MX.6/NhYwReU.bDDCzWz6LvT8eGHXgLZNFwvpJYnhm', 'MANAGER', NULL, TRUE, NOW()),
('employee1@example.com', '$2a$10$8K1p/a0dL3.MX.6/NhYwReU.bDDCzWz6LvT8eGHXgLZNFwvpJYnhm', 'EMPLOYEE', 2, TRUE, NOW()),
('employee2@example.com', '$2a$10$8K1p/a0dL3.MX.6/NhYwReU.bDDCzWz6LvT8eGHXgLZNFwvpJYnhm', 'EMPLOYEE', 2, TRUE, NOW());

INSERT INTO request_types (code, name, active) VALUES
('PURCHASE', 'Purchase Request', TRUE),
('IT_ACCESS', 'IT Access Request', TRUE),
('HR', 'HR Request', TRUE),
('TRAVEL', 'Travel Request', TRUE);

INSERT INTO requests (requester_id, manager_id, type_id, title, description, amount, priority, status, created_at, updated_at) VALUES
(3, 2, 1, 'New laptop request', 'Need a new laptop for development work', 1500.00, 'HIGH', 'SUBMITTED', NOW(), NOW()),
(3, 2, 2, 'Access to production database', 'Need read access to production database for debugging', NULL, 'MEDIUM', 'APPROVED', NOW(), NOW()),
(4, 2, 3, 'Vacation request', 'Request for 5 days vacation next month', NULL, 'LOW', 'SUBMITTED', NOW(), NOW()),
(4, 2, 4, 'Conference travel', 'Travel to Spring Boot conference in New York', 2500.00, 'MEDIUM', 'IN_PROGRESS', NOW(), NOW()),
(3, 2, 1, 'Office supplies', 'Need new keyboard and mouse', 150.00, 'LOW', 'DONE', NOW(), NOW());

INSERT INTO request_comments (request_id, author_id, comment, created_at) VALUES
(2, 2, 'Approved. Please coordinate with IT team for access setup.', NOW()),
(4, 2, 'Approved. Please book the flights and hotel.', NOW()),
(5, 1, 'Request completed. Items delivered.', NOW());

INSERT INTO request_audit_events (request_id, actor_id, event_type, from_status, to_status, note, created_at) VALUES
(1, 3, 'CREATED', NULL, 'SUBMITTED', NULL, NOW()),
(2, 3, 'CREATED', NULL, 'SUBMITTED', NULL, NOW()),
(2, 2, 'APPROVED', 'SUBMITTED', 'APPROVED', 'Approved. Please coordinate with IT team for access setup.', NOW()),
(3, 4, 'CREATED', NULL, 'SUBMITTED', NULL, NOW()),
(4, 4, 'CREATED', NULL, 'SUBMITTED', NULL, NOW()),
(4, 2, 'APPROVED', 'SUBMITTED', 'APPROVED', 'Approved. Please book the flights and hotel.', NOW()),
(4, 1, 'STATUS_CHANGED', 'APPROVED', 'IN_PROGRESS', 'Travel arrangements in progress', NOW()),
(5, 3, 'CREATED', NULL, 'SUBMITTED', NULL, NOW()),
(5, 2, 'APPROVED', 'SUBMITTED', 'APPROVED', 'Approved', NOW()),
(5, 1, 'STATUS_CHANGED', 'APPROVED', 'IN_PROGRESS', 'Processing order', NOW()),
(5, 1, 'STATUS_CHANGED', 'IN_PROGRESS', 'DONE', 'Request completed. Items delivered.', NOW());
