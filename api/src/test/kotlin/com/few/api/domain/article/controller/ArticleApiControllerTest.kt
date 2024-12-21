package com.few.api.domain.article.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.config.web.controller.ApiControllerTestSpec
import com.few.api.domain.article.usecase.dto.*
import com.few.api.domain.common.vo.CategoryType
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import web.description.Description
import web.helper.*
import java.net.URL
import java.time.LocalDateTime
import java.util.stream.IntStream

@Feature("Article API")
class ArticleApiControllerTest : ApiControllerTestSpec() {
    companion object {
        private const val BASE_URL = "/api/v1/articles"
        private const val TAG = "ArticleController"
    }

    /**
     * 인증/비인증 모두 가능한 API
     */
    @Test
    @DisplayName("[GET] /api/v1/articles/{articleId}")
    @Story("[GET] /api/v1/articles/{articleId}")
    fun readArticle() {
        // given
        val api = "ReadArticle"
        val token = "thisisaccesstoken"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/{articleId}")
                .build()
                .toUriString()
        val articleId = 1L

        val memberId = 1L
        `when`(tokenResolver.resolveId(token)).thenReturn(memberId)

        val useCaseIn = ReadArticleUseCaseIn(articleId, memberId)
        val useCaseOut =
            ReadArticleUseCaseOut(
                id = 1L,
                writer =
                    WriterDetail(
                        id = 1L,
                        name = "안나포",
                        url = URL("http://localhost:8080/api/v1/writers/1"),
                        imageUrl = URL("https://github.com/user-attachments/assets/28df9078-488c-49d6-9375-54ce5a250742"),
                    ),
                mainImageUrl =
                    URL(
                        "https://github.com/YAPP-Github/24th-Web-Team-1-BE/assets/102807742/0643d805-5f3a-4563-8c48-2a7d51795326",
                    ),
                title = "ETF(상장 지수 펀드)란? 모르면 손해라고?",
                content = CategoryType.fromCode(0)!!.name,
                problemIds = listOf(1L, 2L, 3L),
                category = "경제",
                createdAt = LocalDateTime.now(),
                views = 1L,
            )
        `when`(readArticleUseCase.execute(useCaseIn)).thenReturn(
            useCaseOut,
        )

        // when
        mockMvc
            .perform(
                get(uri, articleId)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("아티클 Id로 아티클 조회")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(
                                Description.authHeader(true),
                            ).pathParameters(parameterWithName("articleId").description("아티클 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.id", "아티클 Id").asNumber(),
                                    FieldDescription("data.writer", "아티클 작가").asObject(),
                                    FieldDescription("data.writer.id", "아티클 작가 Id").asNumber(),
                                    FieldDescription("data.writer.name", "아티클 작가 이름").asString(),
                                    FieldDescription("data.writer.url", "아티클 작가 링크").asString(),
                                    FieldDescription("data.writer.imageUrl", "아티클 작가 이미지 링크(non-null)").asString(),
                                    FieldDescription("data.mainImageUrl", "아티클 썸네일 이미지 링크").asString(),
                                    FieldDescription("data.title", "아티클 제목").asString(),
                                    FieldDescription("data.content", "아티클 내용").asString(),
                                    FieldDescription("data.problemIds", "아티클 문제 목록").asArray(),
                                    FieldDescription("data.category", "아티클 카테고리").asString(),
                                    FieldDescription("data.createdAt", "아티클 생성일").asString(),
                                    FieldDescription("data.views", "아티클 조회수").asNumber(),
                                    FieldDescription("data.workbooks", "아티클이 포함된 학습지 정보(해당 API에선 사용되지 않음)").asArray(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/articles?prevArticleId={optional}?categoryCd={optional}")
    @Story("[GET] /api/v1/articles?prevArticleId={optional}?categoryCd={optional}")
    fun readArticles() {
        // given
        val api = "ReadArticles"
        val prevArticleId = 1L
        val categoryCd: Byte = CategoryType.IT.code
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path(BASE_URL)
                .queryParam("prevArticleId", prevArticleId)
                .queryParam("categoryCd", categoryCd)
                .build()
                .toUriString()

        val useCaseIn = ReadArticlesUseCaseIn(prevArticleId, categoryCd)
        val useCaseOut =
            ReadArticlesUseCaseOut(
                IntStream
                    .range(0, 10)
                    .mapToObj {
                        ReadArticleUseCaseOut(
                            id = it.toLong(),
                            writer =
                                WriterDetail(
                                    id = 1L,
                                    name = "writer$it",
                                    url = URL("http://localhost:8080/api/v1/writers/$it"),
                                    imageUrl = URL("http://localhost:8080/api/v1/writers/images/$it"),
                                ),
                            mainImageUrl = URL("http://localhost:8080/api/v1/articles/main/images/$it"),
                            title = "title$it",
                            content = "content$it",
                            problemIds = emptyList(),
                            category = CategoryType.ECONOMY.displayName,
                            createdAt = LocalDateTime.now(),
                            views = it.toLong(),
                            workbooks =
                                IntStream
                                    .range(0, 2)
                                    .mapToObj { j ->
                                        WorkbookDetail(
                                            id = "$it$j".toLong(),
                                            title = "workbook$it$j",
                                        )
                                    }.toList(),
                        )
                    }.toList(),
                true,
            )
        `when`(browseArticlesUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc
            .perform(
                get(uri, prevArticleId, categoryCd).contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("아티 목록 10개씩 조회(조회수 기반 정렬)")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .queryParameters(
                                parameterWithName("prevArticleId").description("이전까지 조회한 아티클 Id"),
                                parameterWithName("categoryCd").description("아티클 카테고리 코드"),
                            ).responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.isLast", "마지막 스크롤 유무").asBoolean(),
                                    FieldDescription("data.articles", "아티클 목록").asArray(),
                                    FieldDescription("data.articles[].id", "아티클 Id").asNumber(),
                                    FieldDescription("data.articles[].writer", "아티클 작가").asObject(),
                                    FieldDescription("data.articles[].writer.id", "아티클 작가 Id").asNumber(),
                                    FieldDescription("data.articles[].writer.name", "아티클 작가 이름").asString(),
                                    FieldDescription("data.articles[].writer.url", "아티클 작가 링크").asString(),
                                    FieldDescription("data.articles[].writer.imageUrl", "아티클 작가 이미지 링크(non-null)").asString(),
                                    FieldDescription("data.articles[].mainImageUrl", "아틸컬 썸네일 이미지 링크").asString(),
                                    FieldDescription("data.articles[].title", "아티클 제목").asString(),
                                    FieldDescription("data.articles[].content", "아티클 내용").asString(),
                                    FieldDescription("data.articles[].problemIds", "아티클 문제 목록").asArray(),
                                    FieldDescription("data.articles[].category", "아티클 카테고리").asString(),
                                    FieldDescription("data.articles[].createdAt", "아티클 생성일").asString(),
                                    FieldDescription("data.articles[].views", "아티클 조회수").asNumber(),
                                    FieldDescription("data.articles[].workbooks", "아티클이 포함된 학습지 정보").asArray(),
                                    FieldDescription("data.articles[].workbooks[].id", "아티클이 포함된 학습지 정보(학습지ID)").asNumber(),
                                    FieldDescription("data.articles[].workbooks[].title", "아티클이 포함된 학습지 정보(학습지 제목)").asString(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/articles/categories")
    @Story("[GET] /api/v1/articles/categories")
    fun browseArticleCategories() {
        // given
        val api = "browseArticleCategories"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/categories")
                .build()
                .toUriString()

        // when, then
        mockMvc
            .perform(
                get(uri).contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("아티클 카테고리 code, name 조회")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.categories", "카테고리 목록").asArray(),
                                    FieldDescription("data.categories[].code", "카테고리 코드").asNumber(),
                                    FieldDescription("data.categories[].name", "카테고리 이름").asString(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }
}