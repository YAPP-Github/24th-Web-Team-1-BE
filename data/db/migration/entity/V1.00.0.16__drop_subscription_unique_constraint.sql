-- Subscription의 unique constraint를 삭제합니다.
ALTER TABLE SUBSCRIPTION
    DROP CONSTRAINT subscription_unique_member_id_target_member_id;

ALTER TABLE SUBSCRIPTION
    DROP CONSTRAINT subscription_unique_member_id_target_workbook_id;
