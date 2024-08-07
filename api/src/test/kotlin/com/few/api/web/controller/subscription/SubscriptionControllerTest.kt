package com.few.api.web.controller.subscription

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.subscription.usecase.BrowseSubscribeWorkbooksUseCase
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.subscription.request.UnsubscribeWorkbookRequest
import com.few.api.domain.subscription.usecase.SubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeAllUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.dto.*
import com.few.api.web.controller.helper.*
import com.few.api.web.support.ViewCategory
import com.few.api.web.support.WorkBookStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.util.UriComponentsBuilder

class SubscriptionControllerTest : ControllerTestSpec() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var subscriptionController: SubscriptionController

    @MockBean
    private lateinit var subscribeWorkbookUseCase: SubscribeWorkbookUseCase

    @MockBean
    private lateinit var unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase

    @MockBean
    private lateinit var unsubscribeAllUseCase: UnsubscribeAllUseCase

    @MockBean
    private lateinit var browseSubscribeWorkbooksUseCase: BrowseSubscribeWorkbooksUseCase

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
    @DisplayName("[GET] /api/v1/subscriptions/workbooks")
    @WithUserDetails(userDetailsServiceBeanName = "testTokenUserDetailsService")
    fun browseSubscribeWorkbooks() {
        // given
        val api = "BrowseSubscribeWorkBooks"
        val view = ViewCategory.MAIN_CARD
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/subscriptions/workbooks")
            .queryParam("view", view)
            .build()
            .toUriString()

        // set usecase mock
        val memberId = 1L
        val useCaseIn = BrowseSubscribeWorkbooksUseCaseIn(memberId)
        val useCaseOut = BrowseSubscribeWorkbooksUseCaseOut(
            workbooks = listOf(
                SubscribeWorkbookDetail(
                    workbookId = 1L,
                    isActiveSub = WorkBookStatus.ACTIVE,
                    currentDay = 1,
                    totalDay = 3,
                    rank = 0,
                    totalSubscriber = 100,
                    articleInfo = "{\"articleId\":1}"
                ),
                SubscribeWorkbookDetail(
                    workbookId = 2L,
                    isActiveSub = WorkBookStatus.ACTIVE,
                    currentDay = 2,
                    totalDay = 3,
                    rank = 0,
                    totalSubscriber = 1,
                    articleInfo = "{\"articleId\":5}"
                ),
                SubscribeWorkbookDetail(
                    workbookId = 3L,
                    isActiveSub = WorkBookStatus.DONE,
                    currentDay = 3,
                    totalDay = 3,
                    rank = 0,
                    totalSubscriber = 2,
                    articleInfo = "{}"
                )
            )
        )

        doReturn(useCaseOut).`when`(browseSubscribeWorkbooksUseCase).execute(useCaseIn)

        // when
        mockMvc.perform(
            get(uri)
                .header("Authorization", "Bearer thisisaccesstoken")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("구독한 학습지 정보 목록을 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Authorization")
                                    .defaultValue("{{accessToken}}")
                                    .description("Bearer 어세스 토큰")
                            )
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks")
                                            .fieldWithArray("학습지 목록"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].id")
                                            .fieldWithNumber("학습지 Id"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].status")
                                            .fieldWithString("구독 상태"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].totalDay")
                                            .fieldWithNumber("총 일수"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].currentDay")
                                            .fieldWithNumber("현재 일수"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].rank")
                                            .fieldWithNumber("순위"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].totalSubscriber")
                                            .fieldWithNumber("전체 구독자 수"),
                                        PayloadDocumentation.fieldWithPath("data.workbooks[].articleInfo")
                                            .fieldWithString("학습지 정보(Json 타입)")
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
    @WithUserDetails(userDetailsServiceBeanName = "testTokenUserDetailsService")
    fun subscribeWorkbook() {
        // given
        val api = "SubscribeWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/workbooks/{workbookId}/subs")
            .build().toUriString()

        val email = "test@gmail.com"

        // set usecase mock
        val memberId = 1L
        val workbookId = 1L
        val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, memberId = memberId)
        doNothing().`when`(subscribeWorkbookUseCase).execute(useCaseIn)

        // when
        mockMvc.perform(
            post(uri, workbookId)
                .header("Authorization", "Bearer thisisaccesstoken")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("이메일을 입력하여 학습지를 구독합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Authorization")
                                    .defaultValue("{{accessToken}}")
                                    .description("Bearer 어세스 토큰")
                            )
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
    @WithUserDetails(userDetailsServiceBeanName = "testTokenUserDetailsService")
    fun unsubscribeWorkbook() {
        // given
        val api = "UnsubscribeWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/workbooks/{workbookId}/unsubs")
            .build()
            .toUriString()

        // set usecase mock
        val workbookId = 1L
        val memberId = 1L
        val opinion = "취소합니다."
        val body = objectMapper.writeValueAsString(
            UnsubscribeWorkbookRequest(opinion = opinion)
        )
        val useCaseIn = UnsubscribeWorkbookUseCaseIn(
            workbookId = workbookId,
            memberId = memberId,
            opinion = opinion
        )
        doNothing().`when`(unsubscribeWorkbookUseCase).execute(useCaseIn)

        // when
        mockMvc.perform(
            post(uri, workbookId)
                .header("Authorization", "Bearer thisisaccesstoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
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
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Authorization")
                                    .defaultValue("{{accessToken}}")
                                    .description("Bearer 어세스 토큰")
                            )
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
    @WithUserDetails(userDetailsServiceBeanName = "testTokenUserDetailsService")
    fun deactivateAllSubscriptions() {
        // given
        val api = "UnsubscribeAll"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/subscriptions/unsubs")
            .build()
            .toUriString()

        // set usecase mock
        val memberId = 1L
        val opinion = "전체 수신거부합니다."
        val body = objectMapper.writeValueAsString(
            UnsubscribeWorkbookRequest(opinion = opinion)
        )
        val useCaseIn = UnsubscribeAllUseCaseIn(
            memberId = memberId,
            opinion = opinion
        )
        doNothing().`when`(unsubscribeAllUseCase).execute(useCaseIn)

        // when
        mockMvc.perform(
            post(uri)
                .header("Authorization", "Bearer thisisaccesstoken")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("학습지 구독을 취소합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Authorization")
                                    .defaultValue("{{accessToken}}")
                                    .description("Bearer 어세스 토큰")
                            )
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