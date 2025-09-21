--liquibase formatted sql

--changeset nemanja:db_setup_1
--preconditions onFail:HALT onError:HALT
CREATE TABLE tha_product (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             price NUMERIC(15,3) NOT NULL,
                             quantity INTEGER NOT NULL
);
