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
        private val TAG = "ProblemController"
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
            .path("$BASE_URL/{problemId}")
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
                            .description("문제 Id로 문제 조회")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("problemId").description("문제 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("문제 Id"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("문제"),
                                        PayloadDocumentation.fieldWithPath("data.contents[]")
                                            .fieldWithArray("문제 선지 목록"),
                                        PayloadDocumentation.fieldWithPath("data.contents[].number")
                                            .fieldWithNumber("문제 선지 번호"),
                                        PayloadDocumentation.fieldWithPath("data.contents[].content")
                                            .fieldWithString("문제 선지 내용")
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
            .path("$BASE_URL/{problemId}").build().toUriString()

        val body = objectMapper.writeValueAsString(CheckProblemBody(sub = "sub"))
        // set usecase mock

        // when
        this.webTestClient.post()
            .uri(uri, 1)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("문제 Id로 문제 정답 확인")
                            .summary(api.toIdentifier())
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
                                            .fieldWithString("문제 해설"),
                                        PayloadDocumentation.fieldWithPath("data.answer")
                                            .fieldWithString("문제 정답"),
                                        PayloadDocumentation.fieldWithPath("data.isSolved")
                                            .fieldWithBoolean("문제 정답 여부")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }
}