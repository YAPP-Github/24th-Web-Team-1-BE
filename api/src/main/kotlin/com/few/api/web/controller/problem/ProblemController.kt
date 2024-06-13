package com.few.api.web.controller.problem

import com.few.api.web.controller.problem.request.CheckProblemBody
import com.few.api.web.controller.problem.response.CheckProblemResponse
import com.few.api.web.controller.problem.response.ProblemContents
import com.few.api.web.controller.problem.response.ReadProblemResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v1/problems")
class ProblemController {

    @GetMapping("/{problemId}")
    fun readProblem(
        @PathVariable(value = "problemId") problemId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadProblemResponse>> {
        val data = ReadProblemResponse(
            id = 1L,
            title = "title",
            contents = listOf(ProblemContents(1L, "content1"), ProblemContents(2L, "content2"))
        )
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }

    @PostMapping("/{problemId}")
    fun checkProblem(
        @PathVariable(value = "problemId") problemId: Long,
        @RequestBody body: CheckProblemBody
    ): ApiResponse<ApiResponse.SuccessBody<CheckProblemResponse>> {
        val data = CheckProblemResponse(
            explanation = "explanation",
            answer = "answer",
            isSolved = true
        )
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }
}