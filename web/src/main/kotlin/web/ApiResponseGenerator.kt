package web

import org.springframework.http.HttpStatus

object ApiResponseGenerator {

    fun success(status: HttpStatus): ApiResponse<ApiResponse.Success> {
        return ApiResponse(
            ApiResponse.Success(MessageCode.SUCCESS.value),
            status
        )
    }

    fun success(
        status: HttpStatus,
        code: MessageCode,
    ): ApiResponse<ApiResponse.Success> {
        return ApiResponse(ApiResponse.Success(code.value), status)
    }

    fun <D> success(
        data: D,
        status: HttpStatus,
    ): ApiResponse<ApiResponse.SuccessBody<D>> {
        return ApiResponse(
            ApiResponse.SuccessBody(
                data,
                MessageCode.SUCCESS.value
            ),
            status
        )
    }

    fun <D> success(
        data: D,
        status: HttpStatus,
        code: MessageCode,
    ): ApiResponse<ApiResponse.SuccessBody<D>> {
        return ApiResponse(
            ApiResponse.SuccessBody(data, code.value),
            status
        )
    }

    fun fail(status: HttpStatus): ApiResponse<Void> {
        return ApiResponse(status)
    }

    fun fail(
        body: ApiResponse.FailureBody,
        status: HttpStatus,
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponse(body, status)
    }

    fun fail(
        message: String,
        status: HttpStatus,
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponse(ApiResponse.FailureBody(message), status)
    }
}