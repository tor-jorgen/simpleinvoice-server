CREATE TABLE application_user
(
    id             UUID    NOT NULL PRIMARY KEY,
    principal_id   VARCHAR NOT NULL,
    login_provider VARCHAR NOT NULL,
    first_name     VARCHAR NOT NULL,
    last_name      VARCHAR NOT NULL,
    email_address  VARCHAR NOT NULL,
    scopes         VARCHAR NOT NULL
);

CREATE TABLE settings
(
    id                  UUID    NOT NULL PRIMARY KEY,
    default_due_days    INT     NOT NULL,
    last_invoice_number INT     NOT NULL,
    default_currency    VARCHAR NOT NULL
);

CREATE TABLE household
(
    id       UUID    NOT NULL PRIMARY KEY,
    name     VARCHAR,
    address  VARCHAR NOT NULL,
    zip_code VARCHAR NOT NULL,
    city     VARCHAR NOT NULL,
    country  VARCHAR
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
    id           UUID    NOT NULL PRIMARY KEY,
    product_code VARCHAR NOT NULL,
    product_name VARCHAR NOT NULL,
    quantity     INT     NOT NULL,
    price        DECIMAL NOT NULL,
    currency     VARCHAR NOT NULL
);

CREATE TABLE invoice
(
    id             UUID    NOT NULL PRIMARY KEY,
    invoice_number INT     NOT NULL,
    status         VARCHAR NOT NULL,
    generated_date VARCHAR NOT NULL,
    due_date       VARCHAR NOT NULL,
    finalized_date VARCHAR,
    household_id   UUID    NOT NULL REFERENCES household (id),
    total_price    DECIMAL NOT NULL,
    currency       VARCHAR NOT NULL
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
