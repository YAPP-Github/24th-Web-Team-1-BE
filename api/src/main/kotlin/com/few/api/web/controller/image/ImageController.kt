package com.few.api.web.controller.image

import com.few.api.domain.image.dto.PutImageUseCaseIn
import com.few.api.domain.image.usecase.PutImageUseCase
import com.few.api.web.controller.image.request.ImageSourceRequest
import com.few.api.web.controller.image.response.ImageSourceResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/images"])
class ImageController(
    private val putImageUseCase: PutImageUseCase
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun putImage(request: ImageSourceRequest): ApiResponse<ApiResponse.SuccessBody<ImageSourceResponse>> {
        val useCaseOut = PutImageUseCaseIn(request.source).let { useCaseIn: PutImageUseCaseIn ->
            putImageUseCase.execute(useCaseIn)
        }

        return ImageSourceResponse(useCaseOut.url).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}