package com.few.api.web.controller.workbook

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.workbook.usecase.dto.ArticleDetail
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseOut
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import com.few.api.domain.workbook.usecase.ReadWorkbookUseCase
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.support.WorkBookCategory
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import com.few.data.common.code.CategoryType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.time.LocalDateTime

class WorkBookControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var workBookController: WorkBookController

    @MockBean
    private lateinit var readWorkbookUseCase: ReadWorkbookUseCase

    companion object {
        private val BASE_URL = "/api/v1/workbooks"
        private val TAG = "WorkBookController"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient
            .bindToController(workBookController)
            .controllerAdvice(super.apiControllerExceptionHandler).httpMessageCodecs {
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            }
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks")
    fun browseWorkBooks() {
        // given
        val api = "BrowseWorkBooks"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .queryParam("category", WorkBookCategory.All.parameterName)
            .build()
            .toUriString()

        // set usecase mock

        // when
        mockMvc.perform(
            get(uri)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().is2xxSuccessful
        ).andDo(
            document(
                api.toIdentifier(),
                resource(
                    ResourceSnippetParameters.builder()
                        .summary(api.toIdentifier())
                        .description("학습지 목록을 조회합니다.")
                        .tag(TAG)
                        .requestSchema(Schema.schema(api.toRequestSchema()))
                        .queryParameters(
                            parameterWithName("category").description("학습지 카테고리")
                        )
                        .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                            *Description.describe(
                                arrayOf(
                                    PayloadDocumentation.fieldWithPath("data")
                                        .fieldWithObject("data"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[]")
                                        .fieldWithArray("워크북 목록"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].id")
                                        .fieldWithNumber("워크북 Id"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].mainImageUrl")
                                        .fieldWithString("워크북 대표 이미지 Url"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].title")
                                        .fieldWithString("워크북 제목"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].description")
                                        .fieldWithString("워크북 개요"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].category")
                                        .fieldWithString("워크북 카테고리"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].createdAt")
                                        .fieldWithString("워크북 생성일시"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[]")
                                        .fieldWithArray("워크북 작가 목록"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[].id")
                                        .fieldWithNumber("워크북 작가 Id"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[].name")
                                        .fieldWithString("워크북 작가 이름"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[].url")
                                        .fieldWithString("워크북 작가 링크"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].subscriberCount")
                                        .fieldWithNumber("워크북 구독자 수")
                                )
                            )
                        ).build()
                )
            )
        )
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks/{workbookId}")
    fun readWorkBook() {
        // given
        val api = "ReadWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/{workbookId}")
            .build()
            .toUriString()
        // set usecase mock
        val workbookId = 1L
        `when`(readWorkbookUseCase.execute(ReadWorkbookUseCaseIn(workbookId))).thenReturn(
            ReadWorkbookUseCaseOut(
                id = 1L,
                mainImageUrl = URL("http://localhost:8080/api/v1/workbooks/1/image"),
                title = "재태크, 투자 필수 용어 모음집",
                description = "사회 초년생부터, 직장인, 은퇴자까지 모두가 알아야 할 기본적인 재태크, 투자 필수 용어 모음집 입니다.",
                category = CategoryType.fromCode(0)!!.name,
                createdAt = LocalDateTime.now(),
                writers = listOf(
                    WriterDetail(1L, "안나포", URL("http://localhost:8080/api/v1/users/1")),
                    WriterDetail(2L, "퓨퓨", URL("http://localhost:8080/api/v1/users/2")),
                    WriterDetail(3L, "프레소", URL("http://localhost:8080/api/v1/users/3"))
                ),
                articles = listOf(ArticleDetail(1L, "ISA(개인종합자산관리계좌)란?"), ArticleDetail(2L, "ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란?"))
            )
        )

        // when
        this.webTestClient.get()
            .uri(uri, 1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("학습지 Id를 입력하여 학습지 정보를 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("학습지 Id"),
                                        PayloadDocumentation.fieldWithPath("data.mainImageUrl")
                                            .fieldWithString("학습지 대표 이미지 Url"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("학습지 제목"),
                                        PayloadDocumentation.fieldWithPath("data.description")
                                            .fieldWithString("학습지 개요"),
                                        PayloadDocumentation.fieldWithPath("data.category")
                                            .fieldWithString("학습지 카테고리"),
                                        PayloadDocumentation.fieldWithPath("data.createdAt")
                                            .fieldWithString("학습지 생성일시"),
                                        PayloadDocumentation.fieldWithPath("data.writers[]")
                                            .fieldWithArray("학습지 작가 목록"),
                                        PayloadDocumentation.fieldWithPath("data.writers[].id")
                                            .fieldWithNumber("학습지 작가 Id"),
                                        PayloadDocumentation.fieldWithPath("data.writers[].name")
                                            .fieldWithString("학습지 작가 이름"),
                                        PayloadDocumentation.fieldWithPath("data.writers[].url")
                                            .fieldWithString("학습지 작가 링크"),
                                        PayloadDocumentation.fieldWithPath("data.articles[]")
                                            .fieldWithArray("학습지에 포함된 아티클 목록"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].id")
                                            .fieldWithNumber("학습지에 포함된 아티클 Id"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].title")
                                            .fieldWithString("학습지에 포함된 아티클 제목")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }
}