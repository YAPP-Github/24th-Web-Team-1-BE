package com.few.api.domain.workbook.controller

import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.config.web.controller.ApiControllerTestSpec
import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.common.vo.ViewCategory
import com.few.api.domain.common.vo.WorkBookCategory
import com.few.api.domain.workbook.usecase.dto.*
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import web.description.Description
import web.helper.*
import java.net.URL
import java.time.LocalDateTime

@Feature("WorkBook API")
class WorkBookApiControllerTest : ApiControllerTestSpec() {
    companion object {
        private const val BASE_URL = "/api/v1/workbooks"
        private const val TAG = "WorkBookController"
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks/categories")
    @Story("[GET] /api/v1/workbooks/categories")
    fun browseWorkBookCategories() {
        // given
        val api = "BrowseWorkBookCategories"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/categories")
                .build()
                .toUriString()

        // when
        mockMvc
            .perform(
                get(uri)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(
                status().is2xxSuccessful,
            ).andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .summary(api.toIdentifier())
                            .description("학습지 카테고리 목록을 조회합니다.")
                            .tag(TAG)
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.categories[]", "카테고리 목록").asArray(),
                                    FieldDescription("data.categories[].code", "카테고리 코드").asNumber(),
                                    FieldDescription("data.categories[].name", "카테고리 이름").asString(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }

    /**
     * 인증/비인증 모두 가능한 API
     */
    @Test
    @DisplayName("[GET] /api/v1/workbooks")
    @Story("[GET] /api/v1/workbooks")
    fun browseWorkBooks() {
        // given
        val api = "BrowseWorkBooks"
        val token = "thisisaccesstoken"
        val viewCategory = ViewCategory.MAIN_CARD
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path(BASE_URL)
                .queryParam("category", WorkBookCategory.ECONOMY.code)
                .queryParam("view", viewCategory.viewName)
                .build()
                .toUriString()

        val memberId = 1L
        `when`(tokenResolver.resolveId(token)).thenReturn(memberId)

        val useCaseIn =
            BrowseWorkbooksUseCaseIn(WorkBookCategory.ECONOMY, viewCategory, memberId)
        val useCaseOut =
            BrowseWorkbooksUseCaseOut(
                workbooks =
                    listOf(
                        BrowseWorkBookDetail(
                            id = 1L,
                            mainImageUrl = URL("http://localhost:8080/api/v1/workbooks/1/image"),
                            title = "재태크, 투자 필수 용어 모음집",
                            description = "사회 초년생부터, 직장인, 은퇴자까지 모두가 알아야 할 기본적인 재태크, 투자 필수 용어 모음집 입니다.",
                            category = CategoryType.fromCode(0)!!.name,
                            createdAt = LocalDateTime.now(),
                            writerDetails =
                                listOf(
                                    WriterDetail(1L, "안나포", URL("http://localhost:8080/api/v1/users/1")),
                                    WriterDetail(2L, "퓨퓨", URL("http://localhost:8080/api/v1/users/2")),
                                    WriterDetail(3L, "프레소", URL("http://localhost:8080/api/v1/users/3")),
                                ),
                            subscriptionCount = 100,
                        ),
                    ),
            )
        `when`(browseWorkBooksUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc
            .perform(
                get(uri)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(
                status().is2xxSuccessful,
            ).andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .summary(api.toIdentifier())
                            .description("학습지 목록을 조회합니다.")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .queryParameters(
                                parameterWithName("category").description("학습지 카테고리").optional(),
                                parameterWithName("view").description("뷰 카테고리").optional(),
                            ).requestHeaders(Description.authHeader(true))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.workbooks[]", "워크북 목록").asArray(),
                                    FieldDescription("data.workbooks[].id", "워크북 Id").asNumber(),
                                    FieldDescription("data.workbooks[].mainImageUrl", "워크북 대표 이미지 Url").asString(),
                                    FieldDescription("data.workbooks[].title", "워크북 제목").asString(),
                                    FieldDescription("data.workbooks[].description", "워크북 개요").asString(),
                                    FieldDescription("data.workbooks[].category", "워크북 카테고리").asString(),
                                    FieldDescription("data.workbooks[].createdAt", "워크북 생성일시").asString(),
                                    FieldDescription("data.workbooks[].writers[]", "워크북 작가 목록").asArray(),
                                    FieldDescription("data.workbooks[].writers[].id", "워크북 작가 Id").asNumber(),
                                    FieldDescription("data.workbooks[].writers[].name", "워크북 작가 이름").asString(),
                                    FieldDescription("data.workbooks[].writers[].url", "워크북 작가 외부 링크").asString(),
                                    FieldDescription("data.workbooks[].subscriberCount", "워크북 현재 구독자 수").asNumber(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks/{workbookId}")
    @Story("[GET] /api/v1/workbooks/{workbookId}")
    fun readWorkBook() {
        // given
        val api = "ReadWorkBook"
        val uri =
            UriComponentsBuilder
                .newInstance()
                .path("$BASE_URL/{workbookId}")
                .build()
                .toUriString()

        val workbookId = 1L
        val useCaseIn = ReadWorkbookUseCaseIn(workbookId)
        val useCaseOut =
            ReadWorkbookUseCaseOut(
                id = 1L,
                mainImageUrl = URL("http://localhost:8080/api/v1/workbooks/1/image"),
                title = "재태크, 투자 필수 용어 모음집",
                description = "사회 초년생부터, 직장인, 은퇴자까지 모두가 알아야 할 기본적인 재태크, 투자 필수 용어 모음집 입니다.",
                category = CategoryType.fromCode(0)!!.name,
                createdAt = LocalDateTime.now(),
                writers =
                    listOf(
                        WriterDetail(1L, "안나포", URL("http://localhost:8080/api/v1/users/1")),
                        WriterDetail(2L, "퓨퓨", URL("http://localhost:8080/api/v1/users/2")),
                        WriterDetail(3L, "프레소", URL("http://localhost:8080/api/v1/users/3")),
                    ),
                articles =
                    listOf(
                        ArticleDetail(1L, "ISA(개인종합자산관리계좌)란?"),
                        ArticleDetail(
                            2L,
                            "ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란?",
                        ),
                    ),
            )
        `when`(readWorkbookUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc
            .perform(
                get(uri, workbookId)
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .description("학습지 Id를 입력하여 학습지 정보를 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.id", "학습지 Id").asNumber(),
                                    FieldDescription("data.mainImageUrl", "학습지 대표 이미지 Url").asString(),
                                    FieldDescription("data.title", "학습지 제목").asString(),
                                    FieldDescription("data.description", "학습지 개요").asString(),
                                    FieldDescription("data.category", "학습지 카테고리").asString(),
                                    FieldDescription("data.createdAt", "학습지 생성일시").asString(),
                                    FieldDescription("data.writers[]", "학습지 작가 목록").asArray(),
                                    FieldDescription("data.writers[].id", "학습지 작가 Id").asNumber(),
                                    FieldDescription("data.writers[].name", "학습지 작가 이름").asString(),
                                    FieldDescription("data.writers[].url", "학습지 작가 링크").asString(),
                                    FieldDescription("data.articles[]", "학습지에 포함된 아티클 목록").asArray(),
                                    FieldDescription("data.articles[].id", "학습지에 포함된 아티클 Id").asNumber(),
                                    FieldDescription("data.articles[].title", "학습지에 포함된 아티클 제목").asString(),
                                ),
                            ).build(),
                    ),
                ),
            )
    }
}