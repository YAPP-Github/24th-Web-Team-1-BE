-- subscription 테이블에 reason 컬럼 추가 (한국어로만 100자 가능)
ALTER TABLE SUBSCRIPTION
    ADD COLUMN unsubs_opinion VARCHAR(300) NULL;
