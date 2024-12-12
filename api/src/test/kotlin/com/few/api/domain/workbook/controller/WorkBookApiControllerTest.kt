package com.few.api.domain.workbook.controller

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.domain.workbook.usecase.dto.*
import com.few.api.config.web.controller.ApiControllerTestSpec
import web.description.Description
import web.helper.toIdentifier
import web.helper.toRequestSchema
import web.helper.toResponseSchema
import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.common.vo.ViewCategory
import com.few.api.domain.common.vo.WorkBookCategory
import web.helper.fieldWithArray
import web.helper.fieldWithNumber
import web.helper.fieldWithObject
import web.helper.fieldWithString
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.time.LocalDateTime

class WorkBookApiControllerTest : ApiControllerTestSpec() {

    companion object {
        private const val BASE_URL = "/api/v1/workbooks"
        private const val TAG = "WorkBookController"
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks/categories")
    fun browseWorkBookCategories() {
        // given
        val api = "BrowseWorkBookCategories"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/categories")
            .build()
            .toUriString()

        // when
        mockMvc.perform(
            get(uri)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().is2xxSuccessful
        ).andDo(
            document(
                api.toIdentifier(),
                resource(
                    ResourceSnippetParameters.builder()
                        .summary(api.toIdentifier())
                        .description("학습지 카테고리 목록을 조회합니다.")
                        .tag(TAG)
                        .responseSchema(Schema.schema(api.toResponseSchema()))
                        .responseFields(
                            *Description.describe(
                                arrayOf(
                                    PayloadDocumentation.fieldWithPath("data")
                                        .fieldWithObject("data"),
                                    PayloadDocumentation.fieldWithPath("data.categories[]")
                                        .fieldWithArray("카테고리 목록"),
                                    PayloadDocumentation.fieldWithPath("data.categories[].code")
                                        .fieldWithNumber("카테고리 코드"),
                                    PayloadDocumentation.fieldWithPath("data.categories[].name")
                                        .fieldWithString("카테고리 이름")
                                )
                            )
                        ).build()
                )
            )
        )
    }

    /**
     * 인증/비인증 모두 가능한 API
     */
    @Test
    @DisplayName("[GET] /api/v1/workbooks")
    fun browseWorkBooks() {
        // given
        val api = "BrowseWorkBooks"
        val token = "thisisaccesstoken"
        val viewCategory = ViewCategory.MAIN_CARD
        val uri = UriComponentsBuilder.newInstance()
            .path(BASE_URL)
            .queryParam("category", WorkBookCategory.ECONOMY.code)
            .queryParam("view", viewCategory.viewName)
            .build()
            .toUriString()

        val memberId = 1L
        `when`(tokenResolver.resolveId(token)).thenReturn(memberId)

        val useCaseIn =
            BrowseWorkbooksUseCaseIn(WorkBookCategory.ECONOMY, viewCategory, memberId)
        val useCaseOut = BrowseWorkbooksUseCaseOut(
            workbooks = listOf(
                BrowseWorkBookDetail(
                    id = 1L,
                    mainImageUrl = URL("http://localhost:8080/api/v1/workbooks/1/image"),
                    title = "재태크, 투자 필수 용어 모음집",
                    description = "사회 초년생부터, 직장인, 은퇴자까지 모두가 알아야 할 기본적인 재태크, 투자 필수 용어 모음집 입니다.",
                    category = CategoryType.fromCode(0)!!.name,
                    createdAt = LocalDateTime.now(),
                    writerDetails = listOf(
                        WriterDetail(1L, "안나포", URL("http://localhost:8080/api/v1/users/1")),
                        WriterDetail(2L, "퓨퓨", URL("http://localhost:8080/api/v1/users/2")),
                        WriterDetail(3L, "프레소", URL("http://localhost:8080/api/v1/users/3"))
                    ),
                    subscriptionCount = 100
                )
            )
        )
        `when`(browseWorkBooksUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            get(uri)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().is2xxSuccessful
        ).andDo(
            document(
                api.toIdentifier(),
                resource(
                    ResourceSnippetParameters.builder()
                        .summary(api.toIdentifier())
                        .description("학습지 목록을 조회합니다.")
                        .tag(TAG)
                        .requestSchema(Schema.schema(api.toRequestSchema()))
                        .queryParameters(
                            parameterWithName("category").description("학습지 카테고리").optional(),
                            parameterWithName("view").description("뷰 카테고리").optional()
                        )
                        .requestHeaders(
                            ResourceDocumentation.headerWithName("Authorization")
                                .defaultValue("{{accessToken}}")
                                .description("Bearer 어세스 토큰")
                                .optional()
                        )
                        .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                            *Description.describe(
                                arrayOf(
                                    PayloadDocumentation.fieldWithPath("data")
                                        .fieldWithObject("data"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[]")
                                        .fieldWithArray("워크북 목록"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].id")
                                        .fieldWithNumber("워크북 Id"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].mainImageUrl")
                                        .fieldWithString("워크북 대표 이미지 Url"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].title")
                                        .fieldWithString("워크북 제목"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].description")
                                        .fieldWithString("워크북 개요"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].category")
                                        .fieldWithString("워크북 카테고리"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].createdAt")
                                        .fieldWithString("워크북 생성일시"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[]")
                                        .fieldWithArray("워크북 작가 목록"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[].id")
                                        .fieldWithNumber("워크북 작가 Id"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[].name")
                                        .fieldWithString("워크북 작가 이름"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].writers[].url")
                                        .fieldWithString("워크북 작가 외부 링크"),
                                    PayloadDocumentation.fieldWithPath("data.workbooks[].subscriberCount")
                                        .fieldWithNumber("워크북 현재 구독자 수")
                                )
                            )
                        ).build()
                )
            )
        )
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks/{workbookId}")
    fun readWorkBook() {
        // given
        val api = "ReadWorkBook"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/{workbookId}")
            .build()
            .toUriString()

        val workbookId = 1L
        val useCaseIn = ReadWorkbookUseCaseIn(workbookId)
        val useCaseOut = ReadWorkbookUseCaseOut(
            id = 1L,
            mainImageUrl = URL("http://localhost:8080/api/v1/workbooks/1/image"),
            title = "재태크, 투자 필수 용어 모음집",
            description = "사회 초년생부터, 직장인, 은퇴자까지 모두가 알아야 할 기본적인 재태크, 투자 필수 용어 모음집 입니다.",
            category = CategoryType.fromCode(0)!!.name,
            createdAt = LocalDateTime.now(),
            writers = listOf(
                WriterDetail(1L, "안나포", URL("http://localhost:8080/api/v1/users/1")),
                WriterDetail(2L, "퓨퓨", URL("http://localhost:8080/api/v1/users/2")),
                WriterDetail(3L, "프레소", URL("http://localhost:8080/api/v1/users/3"))
            ),
            articles = listOf(
                ArticleDetail(1L, "ISA(개인종합자산관리계좌)란?"),
                ArticleDetail(
                    2L,
                    "ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란?"
                )
            )
        )
        `when`(readWorkbookUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            get(uri, workbookId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .description("학습지 Id를 입력하여 학습지 정보를 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(parameterWithName("workbookId").description("학습지 Id"))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.id")
                                            .fieldWithNumber("학습지 Id"),
                                        PayloadDocumentation.fieldWithPath("data.mainImageUrl")
                                            .fieldWithString("학습지 대표 이미지 Url"),
                                        PayloadDocumentation.fieldWithPath("data.title")
                                            .fieldWithString("학습지 제목"),
                                        PayloadDocumentation.fieldWithPath("data.description")
                                            .fieldWithString("학습지 개요"),
                                        PayloadDocumentation.fieldWithPath("data.category")
                                            .fieldWithString("학습지 카테고리"),
                                        PayloadDocumentation.fieldWithPath("data.createdAt")
                                            .fieldWithString("학습지 생성일시"),
                                        PayloadDocumentation.fieldWithPath("data.writers[]")
                                            .fieldWithArray("학습지 작가 목록"),
                                        PayloadDocumentation.fieldWithPath("data.writers[].id")
                                            .fieldWithNumber("학습지 작가 Id"),
                                        PayloadDocumentation.fieldWithPath("data.writers[].name")
                                            .fieldWithString("학습지 작가 이름"),
                                        PayloadDocumentation.fieldWithPath("data.writers[].url")
                                            .fieldWithString("학습지 작가 링크"),
                                        PayloadDocumentation.fieldWithPath("data.articles[]")
                                            .fieldWithArray("학습지에 포함된 아티클 목록"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].id")
                                            .fieldWithNumber("학습지에 포함된 아티클 Id"),
                                        PayloadDocumentation.fieldWithPath("data.articles[].title")
                                            .fieldWithString("학습지에 포함된 아티클 제목")
                                    )
                                )
                            )
                            .build()
                    )
                )
            )
    }
}