-- 작가 및 유저
CREATE TABLE users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    type_cd    TINYINT      NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

-- 아티클 마스터
CREATE TABLE article_mst
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    category_cd TINYINT      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (user_id)
);

-- 아티클 인포
CREATE TABLE article_ifo
(
    article_mst_id BIGINT NOT NULL,
    content        TEXT   NOT NULL,
    deleted_at     TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (article_mst_id)
);

-- 학습지
CREATE TABLE workbook
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
CREATE TABLE mapping_user_workbook
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    workbook_id BIGINT NOT NULL,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (workbook_id, user_id)
);

-- 학습지-아티클 매핑테이블(다대다)
CREATE TABLE mapping_workbook_article
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    workbook_id BIGINT NOT NULL,
    article_id  BIGINT NOT NULL,
    day_col     int    NOT NULL,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (article_id, workbook_id)
);

-- 문제, 정답, 해설(메타데이터)
CREATE TABLE problem
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    article_id  BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     JSON         NOT NULL,
    answer      TINYINT      NOT NULL,
    explanation VARCHAR(255) NOT NULL,
    creator_id  BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (article_id)
);

-- 풀이 히스토리
CREATE TABLE solve_history
(
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    problem_id BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    choice_ans TINYINT   NOT NULL,
    is_solved  bit(1)    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- 구독
CREATE TABLE subscription
(
    id                 BIGINT    NOT NULL AUTO_INCREMENT,
    user_id            BIGINT    NOT NULL,
    target_user_id     BIGINT    NOT NULL,
    target_workbook_id BIGINT    NOT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at         TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (user_id, target_user_id),
    UNIQUE (user_id, target_workbook_id)
);

-- 인덱스 추가
CREATE INDEX users_idx0 ON users (deleted_at)
CREATE INDEX article_mst_idx0 ON article_mst (deleted_at)
CREATE INDEX article_ifo_idx0 ON article_ifo (deleted_at)
CREATE INDEX workbook_idx0 ON workbook (deleted_at)
CREATE INDEX mapping_user_workbook_idx0 ON mapping_user_workbook (deleted_at)
CREATE INDEX mapping_workbook_article_idx0 ON mapping_workbook_article (deleted_at)
CREATE INDEX problem_idx0 ON problem (deleted_at)
CREATE INDEX solve_history_idx0 ON solve_history (deleted_at)
CREATE INDEX subscription_idx0 ON subscription (deleted_at)