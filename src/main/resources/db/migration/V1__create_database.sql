CREATE TABLE application_user
(
    id             VARCHAR NOT NULL PRIMARY KEY,
    principal_id   VARCHAR NOT NULL,
    login_provider VARCHAR NOT NULL,
    first_name     VARCHAR NOT NULL,
    last_name      VARCHAR NOT NULL,
    email_address  VARCHAR NOT NULL,
    scopes         VARCHAR NOT NULL
);

CREATE TABLE application_config
(
    id                  VARCHAR NOT NULL PRIMARY KEY,
    default_due_days    INT     NOT NULL,
    last_invoice_number INT     NOT NULL,
    default_currency    VARCHAR NOT NULL
);

CREATE TABLE customer
(
    id            VARCHAR NOT NULL PRIMARY KEY,
    first_name    VARCHAR NOT NULL,
    last_name     VARCHAR NOT NULL,
    email_address VARCHAR NOT NULL,
    address       VARCHAR NOT NULL,
    zip_code      VARCHAR NOT NULL,
    city          VARCHAR NOT NULL,
    phone_number  VARCHAR
);

CREATE TABLE product
(
    id           VARCHAR NOT NULL PRIMARY KEY,
    product_code VARCHAR NOT NULL,
    product_name VARCHAR NOT NULL,
    quantity     INT     NOT NULL,
    price        DECIMAL NOT NULL,
    currency     VARCHAR NOT NULL
);

CREATE TABLE invoice
(
    id             VARCHAR NOT NULL PRIMARY KEY,
    invoice_number INT     NOT NULL,
    generated_date VARCHAR NOT NULL,
    due_date       VARCHAR NOT NULL,
    customer_id    VARCHAR NOT NULL REFERENCES customer (id)
);

CREATE TABLE invoice_line
(
    id          VARCHAR NOT NULL PRIMARY KEY,
    invoice_id  VARCHAR NOT NULL REFERENCES invoice (id),
    line_number INT     NOT NULL,
    product_id  VARCHAR NOT NULL REFERENCES product (id),
    quantity    INT     NOT NULL,
    total_price DECIMAL NOT NULL
);
