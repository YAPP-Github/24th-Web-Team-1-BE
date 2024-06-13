package com.few.api.web.controller.workbook

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.controller.workbook.request.SubWorkBookBody
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
        private val TAG = "WorkBookControllerTest"
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
            .path(BASE_URL)
            .toUriString()
        // set usecase mock

        // when
        this.webTestClient.get()
            .uri("$uri/{workbookId}", 1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description(api.toApiDescription())
                            .summary(api.toSummary())
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
                                            .fieldWithNumber("id"),
                                        PayloadDocumentation.fieldWithPath("data.name")
                                            .fieldWithNumber("name"),
                                        PayloadDocumentation.fieldWithPath("data.mainImageUrl")
                                            .fieldWithString("mainImageUrl"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("title"),
                                        PayloadDocumentation.fieldWithPath("data.description")
                                            .fieldWithString("description"),
                                        PayloadDocumentation.fieldWithPath("data.category")
                                            .fieldWithString("category"),
                                        PayloadDocumentation.fieldWithPath("data.createdAt")
                                            .fieldWithString("createdAt"),
                                        PayloadDocumentation.fieldWithPath("data.writerIds")
                                            .fieldWithArray("writerIds"),
                                        PayloadDocumentation.fieldWithPath("data.articles[]")
                                            .fieldWithArray("articles"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].id")
                                            .fieldWithNumber("articleId"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].title")
                                            .fieldWithString("articleTitle")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/workbooks/{workbookId}/subs")
    fun subWorkBook() {
        // given
        val api = "SubWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL).toUriString()

        val body = objectMapper.writeValueAsString(SubWorkBookBody(email = "test@gmail.com"))
        // set usecase mock

        // when
        this.webTestClient.post()
            .uri("$uri/{workbookId}/subs", 1)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description(api.toApiDescription())
                            .summary(api.toSummary())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe()
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[DELETE] /api/v1/workbooks/{workbookId}/subs")
    fun cancelSubWorkBook() {
        // given
        val api = "CancelSubWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .toUriString()
        // set usecase mock

        // when
        this.webTestClient.delete()
            .uri("$uri/{workbookId}/subs", 1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description(api.toApiDescription())
                            .summary(api.toSummary())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe()
                            )
                            .build()
                    )
                )
            )
    }
}