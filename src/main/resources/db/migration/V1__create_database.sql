CREATE TABLE application_user
(
    id             UUID         NOT NULL PRIMARY KEY,
    principal_id   VARCHAR(128) NOT NULL,
    login_provider VARCHAR(16)  NOT NULL,
    first_name     VARCHAR(128) NOT NULL,
    last_name      VARCHAR(128) NOT NULL,
    email_address  VARCHAR(128) NOT NULL,
    scopes         VARCHAR(512) NOT NULL,
    inactive       BOOLEAN      DEFAULT FALSE
);

CREATE TABLE settings
(
    id                     UUID       NOT NULL PRIMARY KEY,
    default_due_days       INT        NOT NULL,
    last_invoice_number    INT        NOT NULL,
    default_tax_percentage DECIMAL    NOT NULL,
    default_currency       VARCHAR(8) NOT NULL,
    default_email_subject  VARCHAR(128),
    default_email_text     VARCHAR(1024)
);

CREATE TABLE household
(
    id       UUID         NOT NULL PRIMARY KEY,
    name     VARCHAR(128),
    address  VARCHAR(128) NOT NULL,
    address2 VARCHAR(128),
    zip_code VARCHAR(16)  NOT NULL,
    city     VARCHAR(128) NOT NULL,
    country  VARCHAR(128),
    inactive BOOLEAN      DEFAULT FALSE
);

CREATE TABLE person
(
    id            UUID         NOT NULL PRIMARY KEY,
    household_id  UUID         NOT NULL REFERENCES household (id),
    first_name    VARCHAR(128) NOT NULL,
    last_name     VARCHAR(128) NOT NULL,
    email_address VARCHAR(128),
    phone_number  VARCHAR(32)
);

CREATE TABLE product
(
    id             UUID         NOT NULL PRIMARY KEY,
    product_code   VARCHAR(64)  NOT NULL,
    product_name   VARCHAR(128) NOT NULL,
    quantity       INT          NOT NULL,
    price          DECIMAL      NOT NULL,
    currency       VARCHAR(8)   NOT NULL,
    tax_percentage DECIMAL      NOT NULL,
    total_price    DECIMAL      NOT NULL,
    inactive       BOOLEAN      DEFAULT FALSE
);

CREATE TABLE invoice
(
    id                UUID           NOT NULL PRIMARY KEY,
    invoice_number    INT            NOT NULL UNIQUE,
    status            VARCHAR(32)    NOT NULL,
    generated_date    VARCHAR(64)    NOT NULL,
    due_date          VARCHAR(64)    NOT NULL,
    finalized_date    VARCHAR(64),
    household_id      UUID           NOT NULL REFERENCES household (id),
    price             DECIMAL        NOT NULL,
    currency          VARCHAR(8)     NOT NULL,
    tax               DECIMAL        NOT NULL,
    total_price       DECIMAL        NOT NULL,
    invoice_file_path VARCHAR(512)
);

CREATE TABLE invoice_line
(
    id          UUID       NOT NULL PRIMARY KEY,
    invoice_id  UUID       NOT NULL REFERENCES invoice (id),
    line_number INT        NOT NULL,
    product_id  UUID       NOT NULL REFERENCES product (id),
    quantity    INT        NOT NULL,
    price       DECIMAL    NOT NULL,
    currency    VARCHAR(8) NOT NULL,
    tax         DECIMAL    NOT NULL,
    total_price DECIMAL    NOT NULL
);

CREATE TABLE tag
(
    id       UUID         NOT NULL PRIMARY KEY,
    name     VARCHAR(128) NOT NULL UNIQUE,
    inactive BOOLEAN      DEFAULT FALSE
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
    id        UUID        NOT NULL PRIMARY KEY,
    timestamp VARCHAR(64) NOT NULL,
    item_id   VARCHAR(64) NOT NULL,
    item      VARCHAR(1024),
    message   VARCHAR(128),
    user_id   VARCHAR(64)
);
