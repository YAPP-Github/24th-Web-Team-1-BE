-- 아티클 조회수 저장 테이블
CREATE TABLE ARTICLE_VIEW_HIS
(
    id             BIGINT    NOT NULL AUTO_INCREMENT,
    article_mst_id BIGINT    NOT NULL,
    member_id      BIGINT    NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id)
);
