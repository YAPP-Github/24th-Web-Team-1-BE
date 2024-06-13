package com.few.api.web.controller.problem

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.controller.problem.request.CheckProblemBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

class ProblemControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var problemController: ProblemController

    companion object {
        private val BASE_URL = "/api/v1/problems"
        private val TAG = "ProblemControllerTest"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient
            .bindToController(problemController)
            .controllerAdvice(super.apiControllerExceptionHandler)
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[GET] /api/v1/problems/{problemId}")
    fun readArticle() {
        // given
        val api = "ReadProblem"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .toUriString()
        // set usecase mock

        // when
        this.webTestClient.get()
            .uri("$uri/{problemId}", 1)
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
                            .pathParameters(parameterWithName("problemId").description("문 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("id"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("title"),
                                        PayloadDocumentation.fieldWithPath("data.contents[]")
                                            .fieldWithArray("contents"),
                                        PayloadDocumentation.fieldWithPath("data.contents[].number")
                                            .fieldWithNumber("contentNumber"),
                                        PayloadDocumentation.fieldWithPath("data.contents[].content")
                                            .fieldWithString("content")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/problems/{problemId}")
    fun checkProblem() {
        // given
        val api = "CheckProblem"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL).toUriString()

        val body = objectMapper.writeValueAsString(CheckProblemBody(sub = "sub"))
        // set usecase mock

        // when
        this.webTestClient.post()
            .uri("$uri/{problemId}", 1)
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
                            .pathParameters(parameterWithName("problemId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.explanation")
                                            .fieldWithString("explanation"),
                                        PayloadDocumentation.fieldWithPath("data.answer")
                                            .fieldWithString("answer"),
                                        PayloadDocumentation.fieldWithPath("data.isSolved")
                                            .fieldWithBoolean("isSolved")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }
}