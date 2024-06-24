CREATE
    USER 'few-test-local'@'localhost' IDENTIFIED BY 'few-test-local';
CREATE
    USER 'few-test-local'@'%' IDENTIFIED BY 'few-test-local';

GRANT ALL PRIVILEGES ON *.* TO
    'few-test-local'@'localhost';
GRANT ALL PRIVILEGES ON *.* TO
    'few-test-local'@'%';

CREATE
    DATABASE api DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
