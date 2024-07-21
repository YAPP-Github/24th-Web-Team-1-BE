-- article 별 조회수 저장 테이블
CREATE TABLE ARTICLE_VIEW_COUNT
(
    article_id  BIGINT  NOT NULL,
    view_count  BIGINT  NOT NULL,
    category_cd TINYINT NOT NULL,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT article_view_count_pk PRIMARY KEY (article_id)
);

-- 조회수 순으로 아티클 조회시 사용하기 위한 인덱스
-- ex. SELECT * FROM ARTICLE_VIEW_COUNT ORDER BY view_count DESC LIMIT 10;
CREATE INDEX article_view_count_idx1 ON ARTICLE_VIEW_COUNT (view_count DESC);

-- 카테고리 별 필터링을 위한 인덱스
CREATE INDEX article_view_count_idx2 ON ARTICLE_VIEW_COUNT (category_cd);
