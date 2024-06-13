package com.few.api.web.controller.hello

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

class _HelloControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    companion object {
        private val BASE_URL = "/api/v1/hello"
        private val TAG = "HelloControllerTest"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient
            .bindToController(_HelloController())
            .controllerAdvice(super.apiControllerExceptionHandler)
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[GET] /api/v1/hello")
    fun helloApi() {
        // given
        val api = "GetHelloApi"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .queryParam("name", "few")
            .queryParam("age", 30)
            .queryParam("club", "yapp").build().toUriString()

        // set usecase mock

        // when
        this.webTestClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk()
            .expectBody().consumeWith(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description(api.toApiDescription())
                            .summary(api.toSummary())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .queryParameters(
                                parameterWithName("name").description("name").optional(),
                                parameterWithName("age").description("age").optional(),
                                parameterWithName("club").description("club")
                            )
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.hello")
                                            .fieldWithString("hello"),
                                        PayloadDocumentation.fieldWithPath("data.name")
                                            .fieldWithString("name"),
                                        PayloadDocumentation.fieldWithPath("data.age")
                                            .fieldWithString("age"),
                                        PayloadDocumentation.fieldWithPath("data.club")
                                            .fieldWithString("club")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/hello")
    fun helloPost() {
        // given
        val api = "PostHelloApi"
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL).build().toUriString()

        // set usecase mock
        val body = objectMapper.writeValueAsString(mapOf("name" to "few"))

        // when
        this.webTestClient.post().uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().isOk()
            .expectBody().consumeWith(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description(api.toApiDescription())
                            .summary(api.toSummary())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data").fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.hello").fieldWithString("hello"),
                                        PayloadDocumentation.fieldWithPath("data.name").fieldWithString("name")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/hello/error")
    fun helloError() {
        // given
        val api = "GetHelloError"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/error").build().toUriString()

        // set usecase mock

        // when
        this.webTestClient.get().uri(uri).accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().is5xxServerError()
            .expectBody()
            .consumeWith(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description(api.toApiDescription())
                            .summary(api.toSummary())
                            .privateResource(false)
                            .deprecated(true)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("message")
                                            .fieldWithString("message")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }
}