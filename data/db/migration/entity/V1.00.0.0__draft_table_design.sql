-- 작가 및 유저
CREATE TABLE MEMBER
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL,
    type_cd     TINYINT      NOT NULL,
    description JSON         NOT NULL DEFAULT (JSON_OBJECT()),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

-- 아티클 마스터
CREATE TABLE ARTICLE_MST
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    member_id      BIGINT       NOT NULL,
    main_image_url VARCHAR(255) NOT NULL,
    title          VARCHAR(255) NOT NULL,
    category_cd    TINYINT      NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- 아티클 인포
CREATE TABLE ARTICLE_IFO
(
    article_mst_id BIGINT NOT NULL,
    content        TEXT   NOT NULL,
    deleted_at     TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (article_mst_id)
);

-- 학습지
CREATE TABLE WORKBOOK
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    title          VARCHAR(255) NOT NULL,
    main_image_url VARCHAR(255) NOT NULL,
    category_cd    TINYINT      NOT NULL,
    description    VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- 작가-학습지 매핑테이블(다대다)
CREATE TABLE MAPPING_MEMBER_WORKBOOK
(
    member_id   BIGINT NOT NULL,
    workbook_id BIGINT NOT NULL,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (workbook_id, member_id)
);

-- 학습지-아티클 매핑테이블(다대다)
CREATE TABLE MAPPING_WORKBOOK_ARTICLE
(
    workbook_id BIGINT NOT NULL,
    article_id  BIGINT NOT NULL,
    day_col     int    NOT NULL,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (workbook_id, article_id)
);

-- 문제, 정답, 해설(메타데이터)
CREATE TABLE PROBLEM
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    article_id  BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    contents    JSON         NOT NULL,
    answer      VARCHAR(255) NOT NULL,
    explanation VARCHAR(255) NOT NULL,
    creator_id  BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- 풀이 히스토리
CREATE TABLE SUBMIT_HISTORY
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    problem_id BIGINT       NOT NULL,
    member_id  BIGINT       NOT NULL,
    submit_ans VARCHAR(255) NOT NULL,
    is_solved  bit(1)       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- 구독
CREATE TABLE SUBSCRIPTION
(
    id                 BIGINT    NOT NULL AUTO_INCREMENT,
    member_id          BIGINT    NOT NULL,
    target_member_id   BIGINT NULL,
    target_workbook_id BIGINT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at         TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (target_member_id, member_id),
    UNIQUE (target_workbook_id, member_id)
);

-- [인덱스 추가] --
-- problem_idx1: problem 테이블에서 article_id 기반으로 문제 조회시 사용
CREATE INDEX problem_idx1 ON PROBLEM (article_id);

-- article_mst_idx1: 작가가 작성한 아티클 조회시 사용
CREATE INDEX article_mst_idx1 ON ARTICLE_MST (member_id);
