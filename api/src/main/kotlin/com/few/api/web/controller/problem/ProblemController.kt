package com.few.api.web.controller.problem

import com.few.api.web.controller.problem.request.CheckProblemBody
import com.few.api.web.controller.problem.response.CheckProblemResponse
import com.few.api.web.controller.problem.response.ProblemContents
import com.few.api.web.controller.problem.response.ReadProblemResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.usecase.problem.ReadProblemUseCase
import com.few.api.web.usecase.problem.`in`.ReadProblemUseCaseIn
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RestController
@RequestMapping("/api/v1/problems")
class ProblemController(
    private val readProblemUseCase: ReadProblemUseCase
) {

    @GetMapping("/{problemId}")
    fun readProblem(
        @PathVariable(value = "problemId") problemId: Long
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
        @PathVariable(value = "problemId") problemId: Long,
        @RequestBody body: CheckProblemBody
    ): ApiResponse<ApiResponse.SuccessBody<CheckProblemResponse>> {
        val data = CheckProblemResponse(
            explanation = "ETF는 일반적으로 낮은 운용 비용을 특징으로 합니다.이는 ETF가 보통 지수 추종(passive management) 방식으로 운용되기 때문입니다. 지수를 추종하는 전략은 액티브 매니지먼트(active management)에 비해 관리가 덜 복잡하고, 따라서 비용이 낮습니다.",
            answer = "2",
            isSolved = true
        )
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }
}