package com.few.api.web.controller.article

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
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
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

class ArticleControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var articleController: ArticleController

    companion object {
        private val BASE_URL = "/api/v1/articles"
        private val TAG = "ArticleControllerTest"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToController(articleController)
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
    @DisplayName("[GET] /api/v1/articles/{articleId}")
    fun readArticle() {
        // given
        val api = "ReadArticle"
        val uri = UriComponentsBuilder.newInstance().path(BASE_URL).toUriString()
        // set usecase mock

        // when
        this.webTestClient.get().uri("$uri/{articleId}", 1).accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus().isOk().expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder().description("아티클 Id로 아티클 조회")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("articleId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("아티클 Id"),
                                        PayloadDocumentation.fieldWithPath("data.userId")
                                            .fieldWithNumber("작가 Id"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("아티클 제목"),
                                        PayloadDocumentation.fieldWithPath("data.content")
                                            .fieldWithString("아티클 내용"),
                                        PayloadDocumentation.fieldWithPath("data.problemIds")
                                            .fieldWithArray("아티클 문제 목록"),
                                        PayloadDocumentation.fieldWithPath("data.category")
                                            .fieldWithString("아티클 카테고리"),
                                        PayloadDocumentation.fieldWithPath("data.createdAt")
                                            .fieldWithString("아티클 생성일")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }
}