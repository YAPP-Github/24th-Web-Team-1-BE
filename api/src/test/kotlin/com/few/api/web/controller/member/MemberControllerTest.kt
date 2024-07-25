package com.few.api.web.controller.member

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.controller.member.request.SaveMemberRequest
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

class MemberControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var memberController: MemberController

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
                                        PayloadDocumentation.fieldWithPath("data.sendAuth")
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
        val id = "adfabdfk3"
        val at = 1000L
        val rt = 1000L

        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/token")
            .queryParam("id", id)
            .queryParam("at", at)
            .queryParam("rt", rt)
            .build()
            .toUriString()

        // set mock

        // when
        this.webTestClient.post()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("토큰 발급")
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