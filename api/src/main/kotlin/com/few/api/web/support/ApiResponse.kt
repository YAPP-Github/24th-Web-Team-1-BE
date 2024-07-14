package com.few.api.web.support

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.Serializable

/** API 응답 객체  */
class ApiResponse<B> : ResponseEntity<B> {
    constructor(status: HttpStatus?) : super(status!!)
    constructor(body: B, status: HttpStatus?) : super(body, status!!)

    /** API 응답 실패 객체  */
    class FailureBody(val message: String) : Serializable

    /**
     * API 응답 성공 객체
     *
     * @param <D> 데이터 타입
     * */
    class SuccessBody<D>(
        val data: D,
        val message: String,
    ) : Serializable

    /** API 응답 성공 객체  */
    class Success(
        val message: String,
    ) : Serializable
}