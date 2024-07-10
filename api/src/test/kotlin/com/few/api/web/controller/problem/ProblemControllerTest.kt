package com.few.api.web.controller.problem

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.problem.usecase.BrowseProblemsUseCase
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.controller.problem.request.CheckProblemRequest
import com.few.api.domain.problem.usecase.CheckProblemUseCase
import com.few.api.domain.problem.usecase.ReadProblemUseCase
import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseIn
import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseIn
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseOut
import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseOut
import com.few.api.domain.problem.usecase.dto.ReadProblemContentsUseCaseOutDetail
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseOut
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
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

    @MockBean
    private lateinit var browseProblemsUseCase: BrowseProblemsUseCase

    @MockBean
    private lateinit var readProblemUseCase: ReadProblemUseCase

    @MockBean
    private lateinit var checkProblemUseCase: CheckProblemUseCase

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
    @DisplayName("[GET] /api/v1/problems?articleId=")
    fun browseProblems() {
        // given
        val api = "BrowseProblems"
        val articleId = 1L
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .queryParam("articleId", articleId)
            .build()
            .toUriString()

        val useCaseIn = BrowseProblemsUseCaseIn(articleId)
        val useCaseOut = BrowseProblemsUseCaseOut(listOf(1L, 2L, 3L))
        `when`(browseProblemsUseCase.execute(useCaseIn))
            .thenReturn(useCaseOut)

        // when
        this.webTestClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("아티클 Id로 문제 목록 조회")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .queryParameters(parameterWithName("articleId").description("아티클 Id").optional())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.problemIds[]")
                                            .fieldWithArray("문제 Id 목록")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/problems/{problemId}")
    fun readProblem() {
        // given
        val api = "ReadProblem"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/{problemId}")
            .build()
            .toUriString()

        // set usecase mock
        val problemId = 1L
        val useCaseIn = ReadProblemUseCaseIn(problemId)
        val useCaseOut = ReadProblemUseCaseOut(
            id = problemId,
            title = "ETF(상장지수펀드)의 특징이 아닌것은?",
            contents = listOf(
                ReadProblemContentsUseCaseOutDetail(1L, "분산투자"),
                ReadProblemContentsUseCaseOutDetail(2L, "높은 운용 비용"),
                ReadProblemContentsUseCaseOutDetail(3L, "유동성"),
                ReadProblemContentsUseCaseOutDetail(4L, "투명성")
            )
        )
        Mockito.`when`(readProblemUseCase.execute(useCaseIn))
            .thenReturn(useCaseOut)

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

        // set usecase mock
        val problemId = 1L
        val sub = "제출답"
        val body = objectMapper.writeValueAsString(CheckProblemRequest(sub = sub))
        val useCaseIn = CheckProblemUseCaseIn(problemId, sub = sub)
        val useCaseOut = CheckProblemUseCaseOut(
            explanation = "ETF는 일반적으로 낮은 운용 비용을 특징으로 합니다.이는 ETF가 보통 지수 추종(passive management) 방식으로 운용되기 때문입니다. 지수를 추종하는 전략은 액티브 매니지먼트(active management)에 비해 관리가 덜 복잡하고, 따라서 비용이 낮습니다.",
            answer = "2",
            isSolved = true
        )
        `when`(checkProblemUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        this.webTestClient.post()
            .uri(uri, problemId)
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