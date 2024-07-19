-- MAPPING_MEMBER_WORKBOOK 테이블의 기본키 제약 조건을 추가합니다.
ALTER TABLE MAPPING_MEMBER_WORKBOOK
    ADD CONSTRAINT mapping_member_workbook_pk PRIMARY KEY (member_id, workbook_id);
