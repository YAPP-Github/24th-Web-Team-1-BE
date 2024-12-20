package com.few.api.domain.subscription.controller

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.config.web.controller.ApiControllerTestSpec
import com.few.api.domain.common.vo.DayCode
import com.few.api.domain.common.vo.ViewCategory
import com.few.api.domain.common.vo.WorkBookStatus
import com.few.api.domain.subscription.controller.request.UnsubscribeWorkbookRequest
import com.few.api.domain.subscription.controller.request.UpdateSubscriptionDayRequest
import com.few.api.domain.subscription.controller.request.UpdateSubscriptionTimeRequest
import com.few.api.domain.subscription.usecase.dto.*
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.util.UriComponentsBuilder
import web.description.Description
import web.helper.*
import java.time.LocalTime

@Feature("Subscription API")
class SubscriptionApiControllerTest : ApiControllerTestSpec() {
    companion object {
        private const val BASE_URL = "/api/v1/"
        private const val TAG = "WorkbookSubscriptionController"
    }

    @Test
    @DisplayName("[GET] /api/v1/subscriptions/workbooks?view=mainCard")
    @Story("[GET] /api/v1/subscriptions/workbooks?view=mainCard")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun browseSubscribeWorkbooksViewMainCard() {
        // given
        val api = "BrowseSubscribeWorkBooksViewMainCard"
        val token = "thisisaccesstoken"
        val view = ViewCategory.MAIN_CARD
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/subscriptions/workbooks")
                .queryParam("view", view.viewName)
                .build()
                .toUriString()

        val memberId = 1L
        val useCaseIn = BrowseSubscribeWorkbooksUseCaseIn(memberId, view)
        val useCaseOut =
            BrowseSubscribeWorkbooksUseCaseOut(
                clazz = MainCardSubscribeWorkbookDetail::class.java,
                workbooks =
                    listOf(
                        MainCardSubscribeWorkbookDetail(
                            workbookId = 1L,
                            isActiveSub = WorkBookStatus.ACTIVE,
                            currentDay = 1,
                            totalDay = 3,
                            rank = 0,
                            totalSubscriber = 100,
                            subscription = Subscription(),
                            articleInfo = "{\"articleId\":1}",
                        ),
                        MainCardSubscribeWorkbookDetail(
                            workbookId = 2L,
                            isActiveSub = WorkBookStatus.ACTIVE,
                            currentDay = 2,
                            totalDay = 3,
                            rank = 0,
                            totalSubscriber = 1,
                            subscription = Subscription(),
                            articleInfo = "{\"articleId\":5}",
                        ),
                        MainCardSubscribeWorkbookDetail(
                            workbookId = 3L,
                            isActiveSub = WorkBookStatus.DONE,
                            currentDay = 3,
                            totalDay = 3,
                            rank = 0,
                            totalSubscriber = 2,
                            subscription = Subscription(),
                            articleInfo = "{\"articleId\":6}",
                        ),
                    ),
            )
        doReturn(useCaseOut).`when`(browseSubscribeWorkbooksUseCase).execute(useCaseIn)

        // when
        mockMvc
            .perform(
                get(uri)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("메인 카드에 표시할 구독한 학습지 정보 목록을 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(Description.authHeader())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.workbooks", "학습지 목록").asArray(),
                                    FieldDescription("data.workbooks[].id", "학습지 Id").asNumber(),
                                    FieldDescription("data.workbooks[].status", "구독 상태").asString(),
                                    FieldDescription("data.workbooks[].totalDay", "총 일수").asNumber(),
                                    FieldDescription("data.workbooks[].currentDay", "현재 일수").asNumber(),
                                    FieldDescription("data.workbooks[].rank", "순위").asNumber(),
                                    FieldDescription("data.workbooks[].totalSubscriber", "누적 구독자 수").asNumber(),
                                    FieldDescription("data.workbooks[].articleInfo", "아티클 정보(Json 타입)").asString(),
                                    FieldDescription("data.workbooks[].subscription", "구독 정보").asObject(),
                                    FieldDescription("data.workbooks[].subscription.time", "구독 시간").asString(),
                                    FieldDescription("data.workbooks[].subscription.dateTimeCode", "구독 시간 코드").asString(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/subscriptions/workbooks?view=myPage")
    @Story("[GET] /api/v1/subscriptions/workbooks?view=myPage")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun browseSubscribeWorkbooksViewMyPage() {
        // given
        val api = "BrowseSubscribeWorkBooksViewMyPage"
        val token = "thisisaccesstoken"
        val view = ViewCategory.MY_PAGE
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/subscriptions/workbooks")
                .queryParam("view", view.viewName)
                .build()
                .toUriString()

        val memberId = 1L
        val useCaseIn = BrowseSubscribeWorkbooksUseCaseIn(memberId, view)
        val useCaseOut =
            BrowseSubscribeWorkbooksUseCaseOut(
                clazz = MyPageSubscribeWorkbookDetail::class.java,
                workbooks =
                    listOf(
                        MyPageSubscribeWorkbookDetail(
                            workbookId = 1L,
                            isActiveSub = WorkBookStatus.ACTIVE,
                            currentDay = 1,
                            totalDay = 3,
                            rank = 0,
                            totalSubscriber = 100,
                            subscription = Subscription(),
                            workbookInfo = "{\"id\":1, \"title\":\"title1\"}",
                        ),
                        MyPageSubscribeWorkbookDetail(
                            workbookId = 2L,
                            isActiveSub = WorkBookStatus.ACTIVE,
                            currentDay = 2,
                            totalDay = 3,
                            rank = 0,
                            totalSubscriber = 1,
                            subscription = Subscription(),
                            workbookInfo = "{\"id\":2, \"title\":\"title2\"}",
                        ),
                    ),
            )
        doReturn(useCaseOut).`when`(browseSubscribeWorkbooksUseCase).execute(useCaseIn)

        // when
        mockMvc
            .perform(
                get(uri)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("마이 페이지에 표시할 구독한 학습지 정보 목록을 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(Description.authHeader())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.workbooks", "학습지 목록").asArray(),
                                    FieldDescription("data.workbooks[].id", "학습지 Id").asNumber(),
                                    FieldDescription("data.workbooks[].status", "구독 상태").asString(),
                                    FieldDescription("data.workbooks[].totalDay", "총 일수").asNumber(),
                                    FieldDescription("data.workbooks[].currentDay", "현재 일수").asNumber(),
                                    FieldDescription("data.workbooks[].rank", "순위").asNumber(),
                                    FieldDescription("data.workbooks[].totalSubscriber", "누적 구독자 수").asNumber(),
                                    FieldDescription("data.workbooks[].workbookInfo", "학습지 정보(Json 타입)").asString(),
                                    FieldDescription("data.workbooks[].subscription", "구독 정보").asObject(),
                                    FieldDescription("data.workbooks[].subscription.time", "구독 시간").asString(),
                                    FieldDescription("data.workbooks[].subscription.dateTimeCode", "구독 시간 코드").asString(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/workbooks/{workbookId}/subs")
    @Story("[POST] /api/v1/workbooks/{workbookId}/subs")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun subscribeWorkbook() {
        // given
        val api = "SubscribeWorkBook"
        val token = "thisisaccesstoken"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/workbooks/{workbookId}/subs")
                .build()
                .toUriString()

        val memberId = 1L
        val workbookId = 1L
        val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, memberId = memberId)
        doNothing().`when`(subscribeWorkbookUseCase).execute(useCaseIn)

        // when
        mockMvc
            .perform(
                post(uri, workbookId)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("이메일을 입력하여 학습지를 구독합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(Description.authHeader())
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/workbooks/{workbookId}/unsubs")
    @Story("[POST] /api/v1/workbooks/{workbookId}/unsubs")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun unsubscribeWorkbook() {
        // given
        val api = "UnsubscribeWorkBook"
        val token = "thisisaccesstoken"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/workbooks/{workbookId}/unsubs")
                .build()
                .toUriString()

        val workbookId = 1L
        val memberId = 1L
        val opinion = "cancel."
        val body =
            objectMapper.writeValueAsString(
                UnsubscribeWorkbookRequest(opinion = opinion),
            )
        val useCaseIn =
            UnsubscribeWorkbookUseCaseIn(
                workbookId = workbookId,
                memberId = memberId,
                opinion = opinion,
            )
        doNothing().`when`(unsubscribeWorkbookUseCase).execute(useCaseIn)

        // when
        mockMvc
            .perform(
                post(uri, workbookId)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("학습지 구독을 취소합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .requestHeaders(Description.authHeader())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/subscriptions/unsubs")
    @Story("[POST] /api/v1/subscriptions/unsubs")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun deactivateAllSubscriptions() {
        // given
        val api = "UnsubscribeAll"
        val token = "thisisaccesstoken"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/subscriptions/unsubs")
                .build()
                .toUriString()

        val opinion = "전체 수신거부합니다."
        val body =
            objectMapper.writeValueAsString(
                UnsubscribeWorkbookRequest(opinion = opinion),
            )

        val memberId = 1L
        val useCaseIn =
            UnsubscribeAllUseCaseIn(
                memberId = memberId,
                opinion = opinion,
            )
        doNothing().`when`(unsubscribeAllUseCase).execute(useCaseIn)

        // when
        mockMvc
            .perform(
                post(uri)
                    .header("Authorization", "Bearer $token")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("학습지 구독을 취소합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(Description.authHeader())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[PATCH] /api/v1/subscriptions/time")
    @Story("[PATCH] /api/v1/subscriptions/time")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun updateSubscriptionTime() {
        // given
        val api = "UpdateSubscriptionTime"
        val token = "thisisaccesstoken"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/subscriptions/time")
                .build()
                .toUriString()

        val time = LocalTime.of(8, 0)
        val workbookId = 1L
        val body =
            objectMapper.writeValueAsString(
                UpdateSubscriptionTimeRequest(
                    time = time,
                    workbookId = workbookId,
                ),
            )

        // when
        mockMvc
            .perform(
                patch(uri)
                    .header("Authorization", "Bearer $token")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("구독 시간을 변경합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(Description.authHeader())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[PATCH] /api/v1/subscriptions/day")
    @Story("[PATCH] /api/v1/subscriptions/day")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun updateSubscriptionDay() {
        // given
        val api = "UpdateSubscriptionDay"
        val token = "thisisaccesstoken"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/subscriptions/day")
                .build()
                .toUriString()

        val dateTimeCode = DayCode.MON_TUE_WED_THU_FRI_SAT_SUN
        val workbookId = 1L
        val body =
            objectMapper.writeValueAsString(
                UpdateSubscriptionDayRequest(
                    workbookId = workbookId,
                    dayCode = dateTimeCode.code,
                ),
            )

        // when
        mockMvc
            .perform(
                patch(uri)
                    .header("Authorization", "Bearer $token")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("구독 요일을 변경합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .requestHeaders(Description.authHeader())
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(),
                            ).build(),
                    ),
                ),
            )
    }
}