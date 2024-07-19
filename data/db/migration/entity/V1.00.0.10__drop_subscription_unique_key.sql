-- SUBSCRIPTION 테이블의 target_member_id, target_workbook_id 컬럼에 대한 유니크 키 제약을 제거합니다.
-- 해당 제약 조건 제거의 경우 직접 SQL을 작성하여 제거합니다.

-- ALTER TABLE SUBSCRIPTION
--     DROP KEY target_member_id;
--
-- ALTER TABLE SUBSCRIPTION
--     DROP KEY target_workbook_id;
