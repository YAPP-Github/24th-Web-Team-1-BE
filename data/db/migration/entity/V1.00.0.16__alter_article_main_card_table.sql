-- article main card 테이블의 workbooks 컬럼 default 변경
ALTER TABLE ARTICLE_MAIN_CARD
    MODIFY COLUMN workbooks JSON NOT NULL DEFAULT (JSON_ARRAY());
