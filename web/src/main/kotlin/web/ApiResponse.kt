package web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.Serializable

class ApiResponse<B> : ResponseEntity<B> {
    constructor(status: HttpStatus?) : super(status!!)
    constructor(body: B, status: HttpStatus?) : super(body, status!!)

    class FailureBody(val message: String) : Serializable

    class SuccessBody<D>(
        val data: D,
        val message: String,
    ) : Serializable

    class Success(
        val message: String,
    ) : Serializable
}