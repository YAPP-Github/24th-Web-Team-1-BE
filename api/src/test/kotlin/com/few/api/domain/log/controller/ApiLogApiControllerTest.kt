package com.few.api.domain.log.controller

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.config.web.controller.ApiControllerTestSpec
import com.few.api.domain.log.controller.request.ApiLogRequest
import com.few.api.domain.log.dto.AddApiLogUseCaseIn
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import web.description.Description
import web.helper.toIdentifier
import web.helper.toRequestSchema
import web.helper.toResponseSchema

@Feature("API Log API")
class ApiLogApiControllerTest : ApiControllerTestSpec() {
    companion object {
        private const val BASE_URL = "/api/v1/logs"
        private const val TAG = "ApiLogControllerTest"
    }

    @Test
    @DisplayName("[POST] /api/v1/logs")
    @Story("[POST] /api/v1/logs")
    fun addApiLog() {
        // Given
        val api = "addApiLog"
        val history =
            objectMapper.writeValueAsString(mapOf("from" to "email", "to" to "readArticle"))
        val request = ApiLogRequest(history)
        val body = objectMapper.writeValueAsString(request)

        val useCaseIn = AddApiLogUseCaseIn(history)
        Mockito.doNothing().`when`(addApiLogUseCase).execute(useCaseIn)

        // When
        mockMvc
            .perform(
                post(BASE_URL)
                    .content(body)
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
                            .description("API 로그를 기록")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema()))
                            .responseFields(
                                *Description.describe(),
                            ).build(),
                    ),
                ),
            )
    }
}