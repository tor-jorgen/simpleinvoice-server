CREATE TABLE application_user
(
    id             UUID    NOT NULL PRIMARY KEY,
    principal_id   VARCHAR NOT NULL,
    login_provider VARCHAR NOT NULL,
    first_name     VARCHAR NOT NULL,
    last_name      VARCHAR NOT NULL,
    email_address  VARCHAR NOT NULL,
    scopes         VARCHAR NOT NULL,
    inactive       BOOLEAN DEFAULT FALSE
);

CREATE TABLE settings
(
    id                    UUID    NOT NULL PRIMARY KEY,
    default_due_days      INT     NOT NULL,
    last_invoice_number   INT     NOT NULL,
    default_currency      VARCHAR NOT NULL,
    default_email_subject VARCHAR,
    default_email_text    VARCHAR
);

CREATE TABLE household
(
    id       UUID    NOT NULL PRIMARY KEY,
    name     VARCHAR,
    address  VARCHAR NOT NULL,
    address2 VARCHAR,
    zip_code VARCHAR NOT NULL,
    city     VARCHAR NOT NULL,
    country  VARCHAR,
    inactive BOOLEAN DEFAULT FALSE
);

CREATE TABLE person
(
    id            UUID    NOT NULL PRIMARY KEY,
    household_id  UUID    NOT NULL REFERENCES household (id),
    first_name    VARCHAR NOT NULL,
    last_name     VARCHAR NOT NULL,
    email_address VARCHAR,
    phone_number  VARCHAR
);

CREATE TABLE product
(
    id             UUID    NOT NULL PRIMARY KEY,
    product_code   VARCHAR NOT NULL,
    product_name   VARCHAR NOT NULL,
    quantity       INT     NOT NULL,
    price          DECIMAL NOT NULL,
    currency       VARCHAR NOT NULL,
    tax_percentage INT NOT NULL,
    total_price    DECIMAL NOT NULL,
    inactive       BOOLEAN DEFAULT FALSE
);

CREATE TABLE invoice
(
    id                UUID       NOT NULL PRIMARY KEY,
    invoice_number    INT UNIQUE NOT NULL,
    status            VARCHAR    NOT NULL,
    generated_date    VARCHAR    NOT NULL,
    due_date          VARCHAR    NOT NULL,
    finalized_date    VARCHAR,
    household_id      UUID       NOT NULL REFERENCES household (id),
    price             DECIMAL NOT NULL,
    currency          VARCHAR NOT NULL,
    tax               DECIMAL NOT NULL,
    total_price       DECIMAL NOT NULL,
    invoice_file_path VARCHAR
);

CREATE TABLE invoice_line
(
    id          UUID    NOT NULL PRIMARY KEY,
    invoice_id  UUID    NOT NULL REFERENCES invoice (id),
    line_number INT     NOT NULL,
    product_id  UUID    NOT NULL REFERENCES product (id),
    quantity    INT     NOT NULL,
    total_price DECIMAL NOT NULL,
    currency    VARCHAR NOT NULL
);

CREATE TABLE tag
(
    id       UUID    NOT NULL PRIMARY KEY,
    name     VARCHAR NOT NULL UNIQUE,
    inactive BOOLEAN DEFAULT FALSE
);

CREATE TABLE household_tags
(
    household_id UUID NOT NULL REFERENCES household (id),
    tag_id       UUID NOT NULL REFERENCES tag (id),
    PRIMARY KEY (household_id, tag_id)
);

CREATE TABLE product_tags
(
    product_id UUID NOT NULL REFERENCES product (id),
    tag_id     UUID NOT NULL REFERENCES tag (id),
    PRIMARY KEY (product_id, tag_id)
);

CREATE TABLE invoice_tags
(
    invoice_id UUID NOT NULL REFERENCES invoice (id),
    tag_id     UUID NOT NULL REFERENCES tag (id),
    PRIMARY KEY (invoice_id, tag_id)
);

CREATE TABLE audit_trail
(
    id        UUID    NOT NULL PRIMARY KEY,
    timestamp VARCHAR NOT NULL,
    item_id   VARCHAR NOT NULL,
    item      VARCHAR,
    message   VARCHAR,
    user_id   VARCHAR
);

