package com.few.api.repo.explain.article

import com.few.api.repo.dao.article.ArticleMainCardDao
import com.few.api.repo.dao.article.command.ArticleMainCardExcludeWorkbookCommand
import com.few.api.repo.dao.article.command.UpdateArticleMainCardWorkbookCommand
import com.few.api.repo.dao.article.command.WorkbookCommand
import com.few.api.repo.explain.ExplainGenerator
import com.few.api.repo.explain.InsertUpdateExplainGenerator
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import com.few.data.common.code.CategoryType
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.ArticleMainCard
import org.jooq.DSLContext
import org.jooq.JSON
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL
import java.time.LocalDateTime

@Tag("explain")
class ArticleMainCardDaoExplainGenerateTest : JooqTestSpec() {
    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var articleMainCardDao: ArticleMainCardDao

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(ArticleMainCard.ARTICLE_MAIN_CARD).execute()
        dslContext.insertInto(ArticleMainCard.ARTICLE_MAIN_CARD)
            .set(ArticleMainCard.ARTICLE_MAIN_CARD.ID, 1L)
            .set(ArticleMainCard.ARTICLE_MAIN_CARD.TITLE, "this is title1")
            .set(
                ArticleMainCard.ARTICLE_MAIN_CARD.MAIN_IMAGE_URL,
                "http://localhost:8080/image1.jpg"
            )
            .set(ArticleMainCard.ARTICLE_MAIN_CARD.CATEGORY_CD, CategoryType.fromCode(0)!!.code)
            .set(ArticleMainCard.ARTICLE_MAIN_CARD.CREATED_AT, LocalDateTime.now())
            .set(ArticleMainCard.ARTICLE_MAIN_CARD.WRITER_ID, 1L)
            .set(ArticleMainCard.ARTICLE_MAIN_CARD.WRITER_EMAIL, "writer@gmail.com")
            .set(
                ArticleMainCard.ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
                JSON.valueOf("{ \"name\": \"writer\", \"url\": \"http://localhost:8080/writer\", \"imgUrl\": \"http://localhost:8080/writer.jpg\" }")
            )
            .set(
                ArticleMainCard.ARTICLE_MAIN_CARD.WORKBOOKS,
                JSON.valueOf("[{\"id\": 1, \"title\": \"title\"}]")
            )
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectArticleMainCardsRecordQueryExplain() {
        val query = articleMainCardDao.selectArticleMainCardsRecordQuery(setOf(1L))

        val explain = ExplainGenerator.execute(dslContext, query)
        ResultGenerator.execute(query, explain, "selectArticleMainCardsRecordQueryExplain")
    }

    @Test
    fun insertArticleMainCardCommandExplain() {
        val command = ArticleMainCardExcludeWorkbookCommand(
            articleId = 2L,
            articleTitle = "this is title2",
            mainImageUrl = URL("http://localhost:8080/image2.jpg"),
            categoryCd = CategoryType.fromCode(0)!!.code,
            createdAt = LocalDateTime.now(),
            writerId = 1L,
            writerEmail = "writer@gmail.com",
            writerName = "writer",
            writerUrl = URL("http://localhost:8080/writer"),
            writerImgUrl = URL("http://localhost:8080/writer.jpg")
        ).let {
            articleMainCardDao.insertArticleMainCardCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "insertArticleMainCardCommandExplain")
    }

    @Test
    fun updateArticleMainCardSetWorkbookCommandExplain() {
        val command = UpdateArticleMainCardWorkbookCommand(
            articleId = 1L,
            workbooks = listOf(
                WorkbookCommand(
                    id = 1L,
                    title = "workbook1"
                ),
                WorkbookCommand(
                    id = 2L,
                    title = "workbook2"
                )
            )
        ).let {
            articleMainCardDao.updateArticleMainCardSetWorkbookCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateArticleMainCardSetWorkbookCommandExplain")
    }
}