-- Subscription의 target_workbook_id에 대한 index를 추가합니다.
CREATE INDEX subscription_target_workbook_idx
    ON SUBSCRIPTION (target_workbook_id);
