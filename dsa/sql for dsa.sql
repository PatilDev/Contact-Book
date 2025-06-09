CREATE DATABASE ContactBook;
USE ContactBook;

CREATE TABLE contacts (
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(255),
    UNIQUE(phone)
);