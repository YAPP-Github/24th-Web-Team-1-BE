-- Workbook의 category_cd를 위한 인덱스 추가
CREATE INDEX workbook_category_idx
    ON WORKBOOK (category_cd);
