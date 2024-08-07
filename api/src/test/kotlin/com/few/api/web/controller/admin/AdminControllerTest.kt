package com.few.api.web.controller.admin

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.admin.document.usecase.*
import com.few.api.domain.admin.document.usecase.dto.*
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.admin.request.*
import com.few.api.web.controller.admin.response.ImageSourceResponse
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.data.common.code.CategoryType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL

class AdminControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var adminController: AdminController

    @MockBean
    private lateinit var addArticleUseCase: AddArticleUseCase

    @MockBean
    private lateinit var addWorkbookUseCase: AddWorkbookUseCase

    @MockBean
    private lateinit var mapArticleUseCase: MapArticleUseCase

    @MockBean
    private lateinit var convertContentUseCase: ConvertContentUseCase

    @MockBean
    private lateinit var putImageUseCase: PutImageUseCase

    companion object {
        private val BASE_URL = "/api/v1/admin"
        private val TAG = "AdminController"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToController(adminController)
            .controllerAdvice(super.apiControllerExceptionHandler)
            .httpMessageCodecs {
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            }
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/workbooks")
    fun addWorkbook() {
        // given
        val api = "AddWorkbook"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/workbooks").build().toUriString()
        val title = "title"
        val mainImageUrl = URL("http://localhost:8080")
        val category = CategoryType.fromCode(0)!!.name
        val description = "description"
        val request = AddWorkbookRequest(title, mainImageUrl, category, description)
        val body = objectMapper.writeValueAsString(request)
        `when`(addWorkbookUseCase.execute(AddWorkbookUseCaseIn(title, mainImageUrl, category, description))).thenReturn(
            AddWorkbookUseCaseOut(1L)
        )

        // when
        this.webTestClient.post()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("학습지 추가")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.workbookId")
                                            .fieldWithNumber("워크북 Id")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/articles")
    fun addArticle() {
        // given
        val api = "AddArticle"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/articles").build().toUriString()
        val request = AddArticleRequest(
            "writer@gmail.com",
            URL("http://localhost:8080"),
            "title",
            CategoryType.fromCode(0)!!.name,
            "md",
            "content source",
            listOf(
                ProblemDto(
                    "title1",
                    listOf(
                        ProblemContentDto(1L, "content1"),
                        ProblemContentDto(2L, "content2"),
                        ProblemContentDto(3L, "content3"),
                        ProblemContentDto(4L, "content4")
                    ),
                    "1",
                    "explanation"
                ),
                ProblemDto(
                    "title2",
                    listOf(
                        ProblemContentDto(1L, "content1"),
                        ProblemContentDto(2L, "content2"),
                        ProblemContentDto(3L, "content3"),
                        ProblemContentDto(4L, "content4")
                    ),
                    "2",
                    "explanation"
                )
            )
        )
        val body = objectMapper.writeValueAsString(request)

        `when`(
            addArticleUseCase.execute(
                AddArticleUseCaseIn(
                    "writer@gmail.com",
                    URL("http://localhost:8080"),
                    "title",
                    CategoryType.fromCode(0)!!.name,
                    "md",
                    "content source",
                    listOf(
                        ProblemDetail(
                            "title1",
                            listOf(
                                ProblemContentDetail(1L, "content1"),
                                ProblemContentDetail(2L, "content2"),
                                ProblemContentDetail(3L, "content3"),
                                ProblemContentDetail(4L, "content4")
                            ),
                            "1",
                            "explanation"
                        ),
                        ProblemDetail(
                            "title2",
                            listOf(
                                ProblemContentDetail(1L, "content1"),
                                ProblemContentDetail(2L, "content2"),
                                ProblemContentDetail(3L, "content3"),
                                ProblemContentDetail(4L, "content4")
                            ),
                            "2",
                            "explanation"
                        )
                    )
                )
            )
        ).thenReturn(AddArticleUseCaseOut(1L))

        // when
        this.webTestClient.post()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("아티클 추가")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.articleId")
                                            .fieldWithNumber("아티클 Id")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/relations/articles")
    fun mapArticle() {
        // given
        val api = "MapArticle"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/relations/articles").build().toUriString()
        val request = MapArticleRequest(1L, 1L, 1)
        val body = objectMapper.writeValueAsString(request)
        doNothing().`when`(mapArticleUseCase).execute(MapArticleUseCaseIn(1L, 1L, 1))

        // when
        this.webTestClient.post()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder().description("아티클 매핑")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe()
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/utilities/conversion/content")
    fun convertContent() {
        // given
        val api = "ConvertContent"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/utilities/conversion/content").build().toUriString()
        val request = ConvertContentRequest(MockMultipartFile("content", "test.md", "text/markdown", "#test".toByteArray()))
        val useCaseOut = ConvertContentUseCaseOut("converted content", URL("http://localhost:8080/test.md"))
        val useCaseIn = ConvertContentUseCaseIn(request.content)
        `when`(convertContentUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            multipart(uri)
                .file(request.content as MockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary(api.toIdentifier())
                            .description("MD to HTML 변환")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.content")
                                            .fieldWithString("변환된 컨텐츠"),
                                        PayloadDocumentation.fieldWithPath("data.originDownLoadUrl")
                                            .fieldWithString("원본 컨텐츠 다운로드 URL")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/utilities/conversion/image")
    fun putImage() {
        // given
        val api = "PutImage"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/utilities/conversion/image").build().toUriString()
        val request = ImageSourceRequest(source = MockMultipartFile("source", "test.jpg", "image/jpeg", "test".toByteArray()))
        val response = ImageSourceResponse(URL("http://localhost:8080/test.jpg"), listOf("jpg", "webp"))
        val useCaseOut = PutImageUseCaseOut(response.url, response.supportSuffix)
        val useCaseIn = PutImageUseCaseIn(request.source)
        `when`(putImageUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            multipart(uri)
                .file(request.source as MockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary(api.toIdentifier())
                            .description("이미지 업로드")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.url")
                                            .fieldWithString(
                                                "이미지 URL"
                                            ),
                                        PayloadDocumentation.fieldWithPath("data.supportSuffix")
                                            .fieldWithArray(
                                                "지원하는 확장자"
                                            )
                                    )
                                )
                            ).build()
                    )
                )

            )
    }
}