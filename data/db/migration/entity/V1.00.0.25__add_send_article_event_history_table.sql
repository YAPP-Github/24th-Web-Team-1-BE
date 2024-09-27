-- 전송 아티클 이벤트 히스토리 테이블
CREATE TABLE SEND_ARTICLE_EVENT_HISTORY
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    member_id   BIGINT       NOT NULL,
    article_id  BIGINT       NOT NULL,
    message_id   VARCHAR(255) NOT NULL,
    event_type_cd TINYINT      NOT NULL,
    send_type_cd TINYINT      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
