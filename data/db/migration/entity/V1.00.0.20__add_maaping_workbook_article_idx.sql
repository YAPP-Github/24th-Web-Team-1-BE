-- MAPPING_WORKBOOK_ARTICLE 테이블에 인덱스 추가
CREATE INDEX mapping_workbook_article_workbook_id_idx
    ON MAPPING_WORKBOOK_ARTICLE (workbook_id);
