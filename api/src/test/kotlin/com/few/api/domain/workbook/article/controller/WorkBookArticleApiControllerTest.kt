package com.few.api.domain.workbook.article.controller

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleUseCaseIn
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleOut
import com.few.api.domain.workbook.article.dto.WriterDetail
import com.few.api.config.web.controller.ApiControllerTestSpec
import web.description.Description
import com.few.api.domain.common.vo.CategoryType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.util.UriComponentsBuilder
import web.helper.*
import java.net.URL
import java.time.LocalDateTime

class WorkBookArticleApiControllerTest : ApiControllerTestSpec() {

    companion object {
        private const val BASE_URL = "/api/v1/workbooks/{workbookId}/articles"
        private const val TAG = "WorkBookArticleController"
    }

    @Test
    @DisplayName("[GET] /api/v1/workbooks/{workbookId}/articles/{articleId}")
    @WithUserDetails(userDetailsServiceBeanName = "apiTestTokenUserDetailsService")
    fun readWorkBookArticle() {
        // given
        val api = "ReadWorkBookArticle"
        val token = "thisisaccesstoken"
        val uri = UriComponentsBuilder.newInstance()
            .path("$BASE_URL/{articleId}")
            .build()
            .toUriString()

        val memberId = 1L
        `when`(tokenResolver.resolveId(token)).thenReturn(memberId)

        val workbookId = 1L
        val articleId = 1L
        val useCaseIn =
            ReadWorkBookArticleUseCaseIn(workbookId, articleId, memberId = memberId)
        val useCaseOut = ReadWorkBookArticleOut(
            id = 1L,
            writer = WriterDetail(
                id = 1L,
                name = "안나포",
                url = URL("http://localhost:8080/api/v1/writers/1")
            ),
            title = "ETF(상장 지수 펀드)란? 모르면 손해라고?",
            content = "content",
            problemIds = listOf(1L, 2L, 3L),
            category = CategoryType.fromCode(0)!!.name,
            createdAt = LocalDateTime.now(),
            day = 1L
        )
        `when`(readWorkBookArticleUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            get(uri, workbookId, articleId)
                .header("Authorization", "Bearer $token")
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                MockMvcRestDocumentation.document(
                    api.toIdentifier(),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("학습지 Id와 아티클 Id를 입력하여 아티클 정보를 조회합니다.")
                            .summary(api.toIdentifier())
                            .privateResource(false)
                            .deprecated(false)
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .pathParameters(
                                parameterWithName("workbookId").description("학습지 Id"),
                                parameterWithName("articleId").description("아티클 Id")
                            )
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.fields(
                                    FieldDescription("data", "data").asObject(),
                                    FieldDescription("data.id", "아티클 Id").asNumber(),
                                    FieldDescription("data.writer", "아티클 작가").asObject(),
                                    FieldDescription("data.writer.id", "아티클 작가 Id").asNumber(),
                                    FieldDescription("data.writer.name", "아티클 작가 이름").asString(),
                                    FieldDescription("data.writer.url", "아티클 작가 링크").asString(),
                                    FieldDescription("data.title", "아티클 제목").asString(),
                                    FieldDescription("data.content", "아티클 내용").asString(),
                                    FieldDescription("data.problemIds", "아티클 문제 목록").asArray(),
                                    FieldDescription("data.category", "아티클 카테고리").asString(),
                                    FieldDescription("data.createdAt", "아티클 생성일").asString(),
                                    FieldDescription("data.day", "아티클 Day 정보").asNumber()
                                )
                            )
                            .build()
                    )
                )
            )
    }
}