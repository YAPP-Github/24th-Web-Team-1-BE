package com.few.api.web.controller.image

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.domain.image.dto.PutImageUseCaseIn
import com.few.api.domain.image.dto.PutImageUseCaseOut
import com.few.api.domain.image.usecase.PutImageUseCase
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.api.web.controller.image.request.ImageSourceRequest
import com.few.api.web.controller.image.response.ImageSourceResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URL

class ImageControllerTest : ControllerTestSpec() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var putImageUseCase: PutImageUseCase

    companion object {
        private val BASE_URL = "/api/v1/images"
        private val TAG = "ImageController"
    }

    @Test
    @DisplayName("[POST] /api/v1/images")
    fun putImage() {
        // given
        val api = "PutImage"
        val request = ImageSourceRequest(source = MockMultipartFile("source", "test.jpg", "image/jpeg", "test".toByteArray()))
        val response = ImageSourceResponse(URL("http://localhost:8080/test.jpg"))
        val useCaseOut = PutImageUseCaseOut(response.url)
        val useCaseIn = PutImageUseCaseIn(request.source)
        `when`(putImageUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            multipart(BASE_URL)
                .file(request.source as MockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary(api.toIdentifier())
                            .description("이미지 업로드")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.url")
                                            .fieldWithString(
                                                "이미지 URL"
                                            )
                                    )
                                )
                            ).build()
                    )
                )

            )
    }
}