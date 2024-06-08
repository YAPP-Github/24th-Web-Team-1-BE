CREATE TABLE foo_tb
(
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    name    VARCHAR(255) NOT NULL,
    deleted BIT          NOT NULL,
    primary key (id)
);
