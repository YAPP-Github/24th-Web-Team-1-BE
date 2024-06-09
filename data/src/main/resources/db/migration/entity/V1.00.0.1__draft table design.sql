-- 작가 및 유저
CREATE TABLE user
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    type       TINYINT      NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

-- 아티클 마스터
CREATE TABLE article_mst
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
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
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    category   TINYINT      NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
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
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (article_id)
);

-- 풀이 히스토리
CREATE TABLE solve_history
(
    id              BIGINT    NOT NULL AUTO_INCREMENT,
    problem_id      BIGINT    NOT NULL,
    user_id         BIGINT    NOT NULL,
    selected_answer TINYINT   NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (problem_id, user_id)
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
