-- SUBSCRIPTION 테이블의 유니크 키 제약을 추가합니다.
ALTER TABLE SUBSCRIPTION
    ADD CONSTRAINT subscription_unique_member_id_target_member_id UNIQUE (member_id, target_member_id);

ALTER TABLE SUBSCRIPTION
    ADD CONSTRAINT subscription_unique_member_id_target_workbook_id UNIQUE (member_id, target_workbook_id);
