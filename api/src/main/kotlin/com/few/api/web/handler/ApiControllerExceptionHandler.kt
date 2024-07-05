package com.few.api.web.handler

import com.few.api.exception.common.ExternalIntegrationException
import com.few.api.exception.common.InsertException
import com.few.api.exception.common.NotFoundException
import com.few.api.exception.subscribe.SubscribeIllegalArgumentException
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.TypeMismatchException
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ServerWebInputException
import java.nio.file.AccessDeniedException

/** API 요청 처리 중 발생하는 예외를 처리하는 핸들러  */
@RestControllerAdvice
class ApiControllerExceptionHandler(
    private val loggingHandler: LoggingHandler
) {

    @ExceptionHandler(ExternalIntegrationException::class, InsertException::class, NotFoundException::class)
    fun handleCommonException(
        ex: Exception,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponseGenerator.fail(ex.message!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SubscribeIllegalArgumentException::class)
    fun handleSubscribeException(
        ex: Exception,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponseGenerator.fail(ex.message!!, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponseGenerator.fail(ExceptionMessage.FAIL.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        MethodArgumentTypeMismatchException::class,
        TypeMismatchException::class,
        WebExchangeBindException::class,
        BindException::class,
        MethodArgumentNotValidException::class,
        DecodingException::class,
        ConstraintViolationException::class,
        ServerWebInputException::class,
        HttpMessageNotReadableException::class
    )
    fun handleBadRequest(
        ex: Exception,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        return handleRequestDetails(ex)
    }

    private fun handleRequestDetails(ex: Exception): ApiResponse<ApiResponse.FailureBody> {
        if (ex is MethodArgumentTypeMismatchException) {
            return handleRequestDetail(ex)
        }
        if (ex is MethodArgumentNotValidException) {
            return handleRequestDetail(ex)
        }
        return ApiResponseGenerator.fail(
            ExceptionMessage.FAIL_REQUEST.message,
            HttpStatus.BAD_REQUEST
        )
    }

    private fun handleRequestDetail(ex: MethodArgumentTypeMismatchException): ApiResponse<ApiResponse.FailureBody> {
        val messageDetail = ExceptionMessage.REQUEST_INVALID_FORMAT.message + " : " + ex.name
        return ApiResponseGenerator.fail(messageDetail, HttpStatus.BAD_REQUEST)
    }

    private fun handleRequestDetail(ex: MethodArgumentNotValidException): ApiResponse<ApiResponse.FailureBody> {
        val filedErrors = ex.fieldErrors.toList()
        val messageDetail = ExceptionMessage.REQUEST_INVALID.message + " : " + filedErrors
        return ApiResponseGenerator.fail(
            messageDetail,
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        ex: Exception,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponseGenerator.fail(
            ExceptionMessage.FAIL.message,
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleForbidden(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        return ApiResponseGenerator.fail(
            ExceptionMessage.ACCESS_DENIED.message,
            HttpStatus.FORBIDDEN
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(
        ex: Exception,
        request: HttpServletRequest
    ): ApiResponse<ApiResponse.FailureBody> {
        loggingHandler.writeLog(ex, request)
        return ApiResponseGenerator.fail(
            ExceptionMessage.FAIL.message,
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}