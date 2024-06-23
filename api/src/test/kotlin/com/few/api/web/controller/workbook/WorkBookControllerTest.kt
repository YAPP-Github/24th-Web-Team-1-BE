package com.few.api.web.controller.workbook

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

class WorkBookControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var workBookController: WorkBookController

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
    @DisplayName("[GET] /api/v1/workbooks/{workbookId}")
    fun readWorkBook() {
        // given
        val api = "ReadWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/{workbookId}")
            .build()
            .toUriString()
        // set usecase mock

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