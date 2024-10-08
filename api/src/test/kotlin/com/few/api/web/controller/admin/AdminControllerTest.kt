package com.few.api.web.controller.admin

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.few.api.domain.admin.document.usecase.dto.*
import com.few.api.web.controller.ControllerTestSpec
import com.few.api.web.controller.admin.request.*
import com.few.api.web.controller.description.Description
import com.few.api.web.controller.helper.*
import com.few.data.common.code.CategoryType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.util.stream.IntStream

class AdminControllerTest : ControllerTestSpec() {

    companion object {
        private const val BASE_URL = "/api/v1/admin"
        private const val TAG = "AdminController"
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/workbooks")
    fun addWorkbook() {
        // given
        val api = "AddWorkbook"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/workbooks").build().toUriString()
        val title = "title"
        val mainImageUrl = URL("http://localhost:8080")
        val category = CategoryType.fromCode(0)!!.name
        val description = "description"
        val request = AddWorkbookRequest(title, mainImageUrl, category, description)
        val body = objectMapper.writeValueAsString(request)

        val useCaseIn = AddWorkbookUseCaseIn(title, mainImageUrl, category, description)
        val useCaseOut = AddWorkbookUseCaseOut(1L)
        `when`(addWorkbookUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            post(uri)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder().description("학습지 추가")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.workbookId")
                                            .fieldWithNumber("워크북 Id")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/articles")
    fun addArticle() {
        // given
        val api = "AddArticle"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/articles").build().toUriString()
        val writerEmail = "writer@gmail.com"
        val articleImageURL = URL("http://localhost:8080")
        val title = "title"
        val category = CategoryType.fromCode(0)!!.name
        val contentType = "md"
        val contentSource = "content source"
        val problemDTOs = IntStream.range(0, 2).mapToObj {
            ProblemDto(
                "title$it",
                IntStream.range(0, 4).mapToObj { ProblemContentDto(it.toLong(), "content$it") }.toList(),
                "$it",
                "explanation$it"
            )
        }.toList()
        val problemDetails = problemDTOs.map {
            ProblemDetail(
                it.title,
                it.contents.map { content -> ProblemContentDetail(content.number, content.content) },
                it.answer,
                it.explanation
            )
        }
        val request = AddArticleRequest(
            writerEmail,
            articleImageURL,
            title,
            category,
            contentType,
            contentSource,
            problemDTOs
        )
        val body = objectMapper.writeValueAsString(request)

        val useCaseIn = AddArticleUseCaseIn(
            writerEmail,
            articleImageURL,
            title,
            category,
            contentType,
            contentSource,
            problemDetails
        )
        val useCaseOut = AddArticleUseCaseOut(1L)
        `when`(
            addArticleUseCase.execute(
                useCaseIn
            )
        ).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            post(uri)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder().description("아티클 추가")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.articleId")
                                            .fieldWithNumber("아티클 Id")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/relations/articles")
    fun mapArticle() {
        // given
        val api = "MapArticle"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/relations/articles").build().toUriString()
        val workbookId = 1L
        val articleId = 1L
        val dayCol = 1
        val request = MapArticleRequest(workbookId, articleId, dayCol)
        val body = objectMapper.writeValueAsString(request)

        val useCaseIn = MapArticleUseCaseIn(workbookId, articleId, dayCol)
        doNothing().`when`(mapArticleUseCase).execute(useCaseIn)

        // when
        mockMvc.perform(
            post(uri)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder().description("아티클 매핑")
                            .summary(api.toIdentifier()).privateResource(false).deprecated(false)
                            .tag(TAG).requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe()
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/admin/utilities/conversion/content")
    fun convertContent() {
        // given
        val api = "ConvertContent"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/utilities/conversion/content").build().toUriString()
        val name = "content"
        val originalFilename = "test.md"
        val contentType = "text/markdown"
        val content = "#test".toByteArray()
        val request = ConvertContentRequest(MockMultipartFile(name, originalFilename, contentType, content))

        val useCaseOut = ConvertContentUseCaseOut("converted content", URL("http://localhost:8080/test.md"))
        val useCaseIn = ConvertContentUseCaseIn(request.content)
        `when`(convertContentUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            multipart(uri)
                .file(request.content as MockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    api.toIdentifier(),
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary(api.toIdentifier())
                            .description("MD to HTML 변환")
                            .tag(TAG)
                            .requestSchema(Schema.schema(api.toRequestSchema()))
                            .responseSchema(Schema.schema(api.toResponseSchema())).responseFields(
                                *Description.describe(
                                    arrayOf(
                                        PayloadDocumentation.fieldWithPath("data")
                                            .fieldWithObject("data"),
                                        PayloadDocumentation.fieldWithPath("data.content")
                                            .fieldWithString("변환된 컨텐츠"),
                                        PayloadDocumentation.fieldWithPath("data.originDownLoadUrl")
                                            .fieldWithString("원본 컨텐츠 다운로드 URL")
                                    )
                                )
                            ).build()
                    )
                )
            )
    }

    @Test
    @DisplayName("[POST] /api/v1/utilities/conversion/image")
    fun putImage() {
        // given
        val api = "PutImage"
        val uri = UriComponentsBuilder.newInstance().path("$BASE_URL/utilities/conversion/image").build().toUriString()
        val name = "source"
        val originalFilename = "test.jpg"
        val contentType = "image/jpeg"
        val content = "test".toByteArray()
        val request = ImageSourceRequest(source = MockMultipartFile(name, originalFilename, contentType, content))

        val useCaseOut = PutImageUseCaseOut(URL("http://localhost:8080/test.jpg"), listOf("jpg", "webp"))
        val useCaseIn = PutImageUseCaseIn(request.source)
        `when`(putImageUseCase.execute(useCaseIn)).thenReturn(useCaseOut)

        // when
        mockMvc.perform(
            multipart(uri)
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
                                            ),
                                        PayloadDocumentation.fieldWithPath("data.supportSuffix")
                                            .fieldWithArray(
                                                "지원하는 확장자"
                                            )
                                    )
                                )
                            ).build()
                    )
                )

            )
    }
}