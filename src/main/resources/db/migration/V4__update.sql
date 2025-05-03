CREATE TABLE audit_trail
(
    id        UUID    NOT NULL PRIMARY KEY,
    timestamp VARCHAR NOT NULL,
    item_id   VARCHAR NOT NULL,
    item      VARCHAR,
    message   VARCHAR,
    user_id   VARCHAR
);
