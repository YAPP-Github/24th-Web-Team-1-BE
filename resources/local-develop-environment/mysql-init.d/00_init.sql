CREATE
    USER 'few-local'@'localhost' IDENTIFIED BY 'few-local';
CREATE
    USER 'few-local'@'%' IDENTIFIED BY 'few-local';

GRANT ALL PRIVILEGES ON *.* TO
    'few-local'@'localhost';
GRANT ALL PRIVILEGES ON *.* TO
    'few-local'@'%';

CREATE
    DATABASE few DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE
