package com.few.api.web.controller.member

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.member.usecase.SaveMemberUseCase
import com.few.api.domain.member.usecase.TokenUseCase
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseOut
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.domain.member.usecase.dto.TokenUseCaseOut
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.controller.member.request.SaveMemberRequest
import com.few.api.web.controller.member.request.TokenRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder

class MemberControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberController: MemberController

    @MockBean
    private lateinit var saveMemberUseCase: SaveMemberUseCase

    @MockBean
    private lateinit var tokenUseCase: TokenUseCase

    companion object {
        private val BASE_URL = "/api/v1/members"
        private val TAG = "MemberController"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient
            .bindToController(memberController)
            .controllerAdvice(super.apiControllerExceptionHandler)
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[POST] /api/v1/members")
    fun saveMember() {
        // given
        val api = "SaveMember"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .build()
            .toUriString()
        val email = "test@gmail.com"
        val body = objectMapper.writeValueAsString(SaveMemberRequest(email = email))

        // set mock
        val useCaseIn = SaveMemberUseCaseIn(email = email)
        `when`(saveMemberUseCase.execute(useCaseIn)).thenReturn(SaveMemberUseCaseOut(isSendAuthEmail = true))

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
                        ResourceSnippetParameters.builder()
                            .description("회원가입")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.isSendAuth")
                                            .fieldWithBoolean("이메일 인증 전송 여부")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/members/token")
    fun token() {
        // given
        val api = "Token"
        val auth_token = "edb966d6ba882cc0df51579c9a94aca0"
        val at = 1000L
        val rt = 1000L
        val tokenRequest = TokenRequest(refreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NTcsIm1lbWJlclJvbGUiOiJbUk9MRV9VU0VSXSIsImlhdCI6MTcyMjI1NTE4MywiZXhwIjoxNzUzODEyNzgzfQ.1KXRim0MVvz1vxOQB_700XPCD9zPQtHNItF_A9upvA8")
        val body = objectMapper.writeValueAsString(tokenRequest)

        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/token")
            .build()
            .toUriString()

        // set mock
        val useCaseIn = TokenUseCaseIn(
            token = "edb966d6ba882cc0df51579c9a94aca0",
            at = at,
            rt = rt,
            refreshToken = tokenRequest.refreshToken
        )
        `when`(tokenUseCase.execute(useCaseIn)).thenReturn(
            TokenUseCaseOut(
                accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NTcsIm1lbWJlclJvbGUiOiJbUk9MRV9VU0VSXSIsImlhdCI6MTcyMjI1NTE4MywiZXhwIjoxNzUzODEyNzgzfQ.1KXRim0MVvz1vxOQB_700XPCD9zPQtHNItF_A9upvA8",
                refreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NTcsIm1lbWJlclJvbGUiOiJbUk9MRV9VU0VSXSIsImlhdCI6MTcyMjI1NTE4MywiZXhwIjoxNzUzODEyNzgzfQ.1KXRim0MVvz1vxOQB_700XPCD9zPQtHNItF_A9upvA8",
                isLogin = false
            )
        )

        // when
        mockMvc.perform(
            post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("auth_token", auth_token)
                .queryParam("at", at.toString())
                .queryParam("rt", rt.toString())
                .content(body)
        ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .summary(api.toIdentifier())
                            .description("토큰 발급")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .queryParameters(
                                ResourceDocumentation.parameterWithName("auth_token")
                                    .description("아이디").optional(),
                                ResourceDocumentation.parameterWithName("at")
                                    .description("액세스 토큰 만료 시간").optional(),
                                ResourceDocumentation.parameterWithName("rt")
                                    .description("리프레시 토큰 만료 시간").optional()
                            )
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.accessToken")
                                            .fieldWithString("accessToken"),
                                        PayloadDocumentation.fieldWithPath("data.refreshToken")
                                            .fieldWithString("refreshToken"),
                                        PayloadDocumentation.fieldWithPath("data.isLogin")
                                            .fieldWithBoolean("로그인 여부")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }
}