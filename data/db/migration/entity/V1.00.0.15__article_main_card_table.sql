-- article main card 테이블 (main card 뷰 용 조합 테이블)
CREATE TABLE ARTICLE_MAIN_CARD
(
    id             BIGINT        NOT NULL,
    title          varchar(255)  NOT NULL,
    main_image_url varchar(1000) NOT NULL,
    category_cd    TINYINT       NOT NULL,
    created_at     timestamp     NOT NULL,
    member_id      BIGINT        NOT NULL,
    member_email   varchar(255)  NOT NULL,
    member_name    JSON          NOT NULL,
    member_img_url JSON          NOT NULL,
    workbook_list  JSON          NOT NULL,
    CONSTRAINT article_main_card_pk PRIMARY KEY (id)
);
