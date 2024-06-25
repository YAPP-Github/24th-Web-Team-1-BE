package com.few.api.web.controller.subscription

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.toIdentifier
import com.few.api.web.controller.helper.toRequestSchema
import com.few.api.web.controller.helper.toResponseSchema
import com.few.api.web.controller.subscription.request.SubscribeWorkbookRequest
import com.few.api.web.controller.subscription.request.UnsubscribeWorkbookRequest
import com.few.api.domain.subscription.usecase.SubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeAllUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.`in`.SubscribeWorkbookUseCaseIn
import com.few.api.domain.subscription.usecase.`in`.UnsubscribeAllUseCaseIn
import com.few.api.domain.subscription.usecase.`in`.UnsubscribeWorkbookUseCaseIn
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

class SubscriptionControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var subscriptionController: SubscriptionController

    @MockBean
    private lateinit var subscribeWorkbookUseCase: SubscribeWorkbookUseCase

    @MockBean
    private lateinit var unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase

    @MockBean
    private lateinit var unsubscribeAllUseCase: UnsubscribeAllUseCase

    companion object {
        private val BASE_URL = "/api/v1/"
        private val TAG = "WorkbookSubscriptionController"
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient
            .bindToController(subscriptionController)
            .controllerAdvice(super.apiControllerExceptionHandler).httpMessageCodecs {
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            }
            .configureClient()
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @DisplayName("[POST] /api/v1/workbooks/{workbookId}/subs")
    fun subscribeWorkbook() {
        // given
        val api = "SubscribeWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/workbooks/{workbookId}/subs").build().toUriString()

        val email = "test@gmail.com"
        val body = objectMapper.writeValueAsString(SubscribeWorkbookRequest(email = email))

        // set usecase mock
        val workbookId = 1L
        val memberId = 1L

        val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = email)
        doNothing().`when`(subscribeWorkbookUseCase).execute(useCaseIn)

        // when
        this.webTestClient.post()
            .uri(uri, workbookId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("이메일을 입력하여 학습지를 구독합니다.")
                            .summary(api.toIdentifier())
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
    @DisplayName("[POST] /api/v1/workbooks/{workbookId}/unsubs")
    fun unsubscribeWorkbook() {
        // given
        val api = "UnsubscribeWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("${SubscriptionControllerTest.BASE_URL}/workbooks/{workbookId}/unsubs")
            .build()
            .toUriString()

        // set usecase mock
        val workbookId = 1L
        val memberId = 1L
        val email = "test@gmail.com"
        val opinion = "취소합니다."
        val body = objectMapper.writeValueAsString(
            UnsubscribeWorkbookRequest(email = email, opinion = opinion)
        )
        val useCaseIn = UnsubscribeWorkbookUseCaseIn(
            workbookId = workbookId,
            email = email,
            opinion = opinion
        )
        doNothing().`when`(unsubscribeWorkbookUseCase).execute(useCaseIn)

        // when
        this.webTestClient.post()
            .uri(uri, workbookId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange().expectStatus().is2xxSuccessful()
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("학습지 구독을 취소합니다.")
                            .summary(api.toIdentifier())
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
    @DisplayName("[POST] /api/v1/subscriptions/unsubs")
    fun deactivateAllSubscriptions() {
        // given
        val api = "UnsubscribeAll"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/subscriptions/unsubs")
            .build()
            .toUriString()

        // set usecase mock
        val memberId = 1L
        val email = "test@gmail.com"
        val opinion = "전체 수신거부합니다."
        val body = objectMapper.writeValueAsString(
            UnsubscribeWorkbookRequest(email = email, opinion = opinion)
        )
        val useCaseIn = UnsubscribeAllUseCaseIn(
            email = email,
            opinion = opinion
        )
        doNothing().`when`(unsubscribeAllUseCase).execute(useCaseIn)

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
                            .description("학습지 구독을 취소합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
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