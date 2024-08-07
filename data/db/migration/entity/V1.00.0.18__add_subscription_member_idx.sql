-- Subscription의 member_id에 대한 index를 추가합니다.
CREATE INDEX subscription_member_idx
    ON SUBSCRIPTION (member_id);
