-- 배치 요청 기록 테이블
CREATE TABLE batch_call_execution
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    status      bit(1) NOT NULL,
    description JSON   NOT NULL DEFAULT (JSON_OBJECT()),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
