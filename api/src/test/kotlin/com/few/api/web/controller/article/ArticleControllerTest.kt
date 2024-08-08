package com.few.api.web.controller.article

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.domain.article.usecase.dto.*
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.data.common.code.CategoryType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.time.LocalDateTime

class ArticleControllerTest : ControllerTestSpec() {

    @Autowired
    lateinit var articleController: ArticleController

    companion object {
        private val BASE_URL = "/api/v1/articles"
        private val TAG = "ArticleController"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToController(articleController)
            .controllerAdvice(super.apiControllerExceptionHandler)
            .httpMessageCodecs {
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            }
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    /**
     * 인증/비인증 모두 가능한 API
     */
    @Test
    @DisplayName("[GET] /api/v1/articles/{articleId}")
    fun readArticle() {
        // given
        val api = "ReadArticle"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/{articleId}").build().toUriString()
        // set usecase mock
        val articleId = 1L
        val memberId = 1L

        `when`(tokenResolver.resolveId("thisisaccesstoken")).thenReturn(memberId)
        `when`(readArticleUseCase.execute(ReadArticleUseCaseIn(articleId, memberId))).thenReturn(
            ReadArticleUseCaseOut(
                id = 1L,
                writer = WriterDetail(
                    id = 1L,
                    name = "안나포",
                    url = URL("http://localhost:8080/api/v1/writers/1"),
                    imageUrl = URL("https://github.com/user-attachments/assets/28df9078-488c-49d6-9375-54ce5a250742")
                ),
                mainImageUrl = URL("https://github.com/YAPP-Github/24th-Web-Team-1-BE/assets/102807742/0643d805-5f3a-4563-8c48-2a7d51795326"),
                title = "ETF(상장 지수 펀드)란? 모르면 손해라고?",
                content = CategoryType.fromCode(0)!!.name,
                problemIds = listOf(1L, 2L, 3L),
                category = "경제",
                createdAt = LocalDateTime.now(),
                views = 1L
            )
        )

        // when
        mockMvc.perform(
            get(uri, articleId)
                .header("Authorization", "Bearer thisisaccesstoken")
        ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("아티클 Id로 아티클 조회")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Authorization")
                                    .defaultValue("{{accessToken}}")
                                    .description("Bearer 어세스 토큰")
                                    .optional()
                            )
                            .pathParameters(parameterWithName("articleId").description("아티클 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("아티클 Id"),
                                        PayloadDocumentation.fieldWithPath("data.writer")
                                            .fieldWithObject("아티클 작가"),
                                        PayloadDocumentation.fieldWithPath("data.writer.id")
                                            .fieldWithNumber("아티클 작가 Id"),
                                        PayloadDocumentation.fieldWithPath("data.writer.name")
                                            .fieldWithString("아티클 작가 이름"),
                                        PayloadDocumentation.fieldWithPath("data.writer.url")
                                            .fieldWithString("아티클 작가 링크"),
                                        PayloadDocumentation.fieldWithPath("data.writer.imageUrl")
                                            .fieldWithString("아티클 작가 이미지 링크(non-null)"),
                                        PayloadDocumentation.fieldWithPath("data.mainImageUrl")
                                            .fieldWithString("아티클 썸네일 이미지 링크"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("아티클 제목"),
                                        PayloadDocumentation.fieldWithPath("data.content")
                                            .fieldWithString("아티클 내용"),
                                        PayloadDocumentation.fieldWithPath("data.problemIds")
                                            .fieldWithArray("아티클 문제 목록"),
                                        PayloadDocumentation.fieldWithPath("data.category")
                                            .fieldWithString("아티클 카테고리"),
                                        PayloadDocumentation.fieldWithPath("data.createdAt")
                                            .fieldWithString("아티클 생성일"),
                                        PayloadDocumentation.fieldWithPath("data.views")
                                            .fieldWithNumber("아티클 조회수"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks")
                                            .fieldWithArray("아티클이 포함된 학습지 정보(해당 API에선 사용되지 않음)")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/articles?prevArticleId={optional}?categoryCd={optional}")
    fun readArticles() {
        // given
        val api = "ReadArticles"
        val prevArticleId = 1L
        val categoryCd: Byte = CategoryType.IT.code
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL")
            .queryParam("prevArticleId", prevArticleId)
            .queryParam("categoryCd", categoryCd)
            .build()
            .toUriString()
        // set usecase mock
        `when`(readArticlesUseCase.execute(ReadArticlesUseCaseIn(prevArticleId, categoryCd))).thenReturn(
            ReadArticlesUseCaseOut(
                listOf(
                    ReadArticleUseCaseOut(
                        id = 1L,
                        writer = WriterDetail(
                            id = 1L,
                            name = "안나포",
                            url = URL("http://localhost:8080/api/v1/writers/1"),
                            imageUrl = URL("https://github.com/user-attachments/assets/28df9078-488c-49d6-9375-54ce5a250742")
                        ),
                        mainImageUrl = URL("https://github.com/YAPP-Github/24th-Web-Team-1-BE/assets/102807742/0643d805-5f3a-4563-8c48-2a7d51795326"),
                        title = "ETF(상장 지수 펀드)란? 모르면 손해라고?",
                        content = CategoryType.fromCode(0)!!.name,
                        problemIds = emptyList(),
                        category = CategoryType.IT.displayName,
                        createdAt = LocalDateTime.now(),
                        views = 9876543210L,
                        workbooks = listOf(
                            WorkbookDetail(
                                id = 1,
                                title = "워크북1 타이틀"
                            ),
                            WorkbookDetail(
                                id = 2,
                                title = "워크북2 타이틀"
                            )
                        )
                    )
                ),
                true
            )
        )

        // when
        this.webTestClient.get().uri(uri).accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk().expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("아티 목록 10개씩 조회(조회수 기반 정렬)")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .queryParameters(
                                parameterWithName("prevArticleId").description("이전까지 조회한 아티클 Id"),
                                parameterWithName("categoryCd").description("아티클 카테고리 코드")
                            )
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.isLast")
                                            .fieldWithBoolean("마지막 스크롤 유무"),
                                        PayloadDocumentation.fieldWithPath("data.articles")
                                            .fieldWithArray("아티클 목록"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].id")
                                            .fieldWithNumber("아티클 Id"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].writer")
                                            .fieldWithObject("아티클 작가"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].writer.id")
                                            .fieldWithNumber("아티클 작가 Id"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].writer.name")
                                            .fieldWithString("아티클 작가 이름"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].writer.url")
                                            .fieldWithString("아티클 작가 링크"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].writer.imageUrl")
                                            .fieldWithString("아티클 작가 이미지 링크(non-null)"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].mainImageUrl")
                                            .fieldWithString("아티클 썸네일 이미지 링크"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].title")
                                            .fieldWithString("아티클 제목"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].content")
                                            .fieldWithString("아티클 내용"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].problemIds")
                                            .fieldWithArray("아티클 문제 목록"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].category")
                                            .fieldWithString("아티클 카테고리"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].createdAt")
                                            .fieldWithString("아티클 생성일"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].views")
                                            .fieldWithNumber("아티클 조회수"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].workbooks")
                                            .fieldWithArray("아티클이 포함된 학습지 정보"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].workbooks[].id")
                                            .fieldWithNumber("아티클이 포함된 학습지 정보(학습지ID)"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].workbooks[].title")
                                            .fieldWithString("아티클이 포함된 학습지 정보(학습지 제목)")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/articles/categories")
    fun browseArticleCategories() {
        // given
        val api = "browseArticleCategories"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/categories")
            .build()
            .toUriString()

        // when, then
        this.webTestClient.get().uri(uri).accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk().expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("아티클 카테고리 code, name 조회")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.categories")
                                            .fieldWithArray("카테고리 목록"),
                                        PayloadDocumentation.fieldWithPath("data.categories[].code")
                                            .fieldWithNumber("카테고리 code"),
                                        PayloadDocumentation.fieldWithPath("data.categories[].name")
                                            .fieldWithString("카테고리 name")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }
}