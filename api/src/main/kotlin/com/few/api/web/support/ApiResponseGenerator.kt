package com.few.api.web.support

import org.springframework.http.HttpStatus

/** API 응답 객체 생성기  */
object ApiResponseGenerator {
    /**
     * Http 상태 코드만 포함하는 API 응답 성공 객체 생성한다.
     *
     * @param status Http 상태 코드
     * @return API 응답 객체
     */
    fun success(status: HttpStatus): ApiResponse<ApiResponse.Success> {
        return ApiResponse(
            ApiResponse.Success(MessageCode.SUCCESS.value),
            status
        )
    }

    /**
     * Http 상태 코드와 메시지 코드를 포함하는 API 응답 성공 객체 생성한다.
     *
     * @param status Http 상태 코드
     * @param code 메시지 코드
     * @return API 응답 객체
     */
    fun success(
        status: HttpStatus,
        code: MessageCode,
    ): ApiResponse<ApiResponse.Success> {
        return ApiResponse(ApiResponse.Success(code.value), status)
    }

    /**
     * Http 상태 코드와 데이터를 포함하는 API 응답 성공 객체 생성한다.
     *
     * @param data 데이터
     * @param status Http 상태 코드
     * @return API 응답 객체
     * @param <D> 데이터 타입
     </D> */
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

    /**
     * Http 상태 코드, 데이터, 메시지 코드를 포함하는 API 응답 성공 객체 생성한다.
     *
     * @param data 데이터
     * @param status Http 상태 코드
     * @param code 메시지 코드
     * @return API 응답 객체
     * @param <D> 데이터 타입
     </D> */
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

    /**
     * Http 상태 코드만 포함하는 API 응답 실패 객체 생성한다.
     *
     * @param status Http 상태 코드
     * @return API 응답 객체
     */
    fun fail(status: HttpStatus): ApiResponse<Void> {
        return ApiResponse(status)
    }

    /**
     * Http 상태 코드와 응답 실패 바디를 포함하는 API 응답 실패 객체 생성한다.
     *
     * @param body 응답 실패 바디
     * @param status Http 상태 코드
     * @return API 응답 객체
     */
    fun fail(
        body: ApiResponse.FailureBody,
        status: HttpStatus,
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponse(body, status)
    }

    /**
     * Http 상태 코드와 코드 그리고 응답 실패 바디를 포함하는 API 응답 실패 객체 생성한다.
     *
     * @param message 메시지
     * @param status Http 상태 코드
     * @return API 응답 객체
     */
    fun fail(
        message: String,
        status: HttpStatus,
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponse(ApiResponse.FailureBody(message), status)
    }
}