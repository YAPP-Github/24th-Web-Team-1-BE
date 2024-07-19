-- MAPPING_WORKBOOK_ARTICLE 테이블의 기본키 제약 조건을 추가합니다.
ALTER TABLE MAPPING_WORKBOOK_ARTICLE
    ADD CONSTRAINT mapping_workbook_article_pk PRIMARY KEY (article_id, workbook_id);
