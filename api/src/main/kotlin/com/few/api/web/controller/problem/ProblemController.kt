package com.few.api.web.controller.problem

import com.few.api.web.controller.problem.request.CheckProblemBody
import com.few.api.web.controller.problem.response.CheckProblemResponse
import com.few.api.web.controller.problem.response.ProblemContents
import com.few.api.web.controller.problem.response.ReadProblemResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.domain.problem.usecase.CheckProblemUseCase
import com.few.api.domain.problem.usecase.ReadProblemUseCase
import com.few.api.domain.problem.usecase.`in`.CheckProblemUseCaseIn
import com.few.api.domain.problem.usecase.`in`.ReadProblemUseCaseIn
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RestController
@RequestMapping(value = ["/api/v1/problems"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ProblemController(
    private val readProblemUseCase: ReadProblemUseCase,
    private val checkProblemUseCase: CheckProblemUseCase
) {

    @GetMapping("/{problemId}")
    fun readProblem(
        @PathVariable(value = "problemId")
        @Min(value = 1, message = "{min.id}")
        problemId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadProblemResponse>> {
        val useCaseOut = readProblemUseCase.execute(ReadProblemUseCaseIn(problemId))

        val response = ReadProblemResponse(
            id = useCaseOut.id,
            title = useCaseOut.title,
            contents = useCaseOut.contents
                .map { c -> ProblemContents(c.number, c.content) }
                .toCollection(LinkedList())
        )

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }

    @PostMapping("/{problemId}")
    fun checkProblem(
        @PathVariable(value = "problemId")
        @Min(value = 1, message = "{min.id}")
        problemId: Long,
        @Valid @RequestBody
        body: CheckProblemBody
    ): ApiResponse<ApiResponse.SuccessBody<CheckProblemResponse>> {
        val useCaseOut = checkProblemUseCase.execute(CheckProblemUseCaseIn(problemId, body.sub))

        val response = CheckProblemResponse(
            explanation = useCaseOut.explanation,
            answer = useCaseOut.answer,
            isSolved = useCaseOut.isSolved
        )

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }
}