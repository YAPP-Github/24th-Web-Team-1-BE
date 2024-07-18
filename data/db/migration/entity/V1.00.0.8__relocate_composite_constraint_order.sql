-- 복합 제약 조건을 제거
ALTER TABLE MAPPING_MEMBER_WORKBOOK
    DROP PRIMARY KEY;

ALTER TABLE MAPPING_WORKBOOK_ARTICLE
    DROP PRIMARY KEY;

ALTER TABLE SUBSCRIPTION
    DROP KEY target_member_id;

ALTER TABLE SUBSCRIPTION
    DROP KEY target_workbook_id;

-- 복합 제약 조건을 추가
ALTER TABLE MAPPING_MEMBER_WORKBOOK
    ADD CONSTRAINT mapping_member_workbook_pk PRIMARY KEY (member_id, workbook_id);

ALTER TABLE MAPPING_WORKBOOK_ARTICLE
    ADD CONSTRAINT mapping_workbook_article_pk PRIMARY KEY (article_id, workbook_id);

ALTER TABLE SUBSCRIPTION
    ADD CONSTRAINT subscription_unique_member_id_target_member_id UNIQUE (member_id, target_member_id);

ALTER TABLE SUBSCRIPTION
    ADD CONSTRAINT subscription_unique_member_id_target_workbook_id UNIQUE (member_id, target_workbook_id);
