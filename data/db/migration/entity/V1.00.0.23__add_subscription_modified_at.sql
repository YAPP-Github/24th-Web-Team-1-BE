-- 구독 정보 수정 시간을 추가합니다.
ALTER TABLE SUBSCRIPTION ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
