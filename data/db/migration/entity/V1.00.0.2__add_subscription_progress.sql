-- 구독 진행 사항 컬럼 추가
ALTER TABLE subscription ADD COLUMN progress BIGINT NOT NULL DEFAULT 0;
