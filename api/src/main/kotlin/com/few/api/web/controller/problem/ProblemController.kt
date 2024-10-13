package com.few.api.web.controller.problem

import com.few.api.domain.problem.usecase.BrowseProblemsUseCase
import com.few.api.domain.problem.usecase.BrowseUndoneProblemsUseCase
import com.few.api.web.controller.problem.request.CheckProblemRequest
import com.few.api.web.controller.problem.response.CheckProblemResponse
import com.few.api.web.controller.problem.response.ProblemContents
import com.few.api.web.controller.problem.response.ReadProblemResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.domain.problem.usecase.CheckProblemUseCase
import com.few.api.domain.problem.usecase.ReadProblemUseCase
import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseIn
import com.few.api.domain.problem.usecase.dto.BrowseUndoneProblemsUseCaseIn
import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseIn
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.web.controller.problem.response.BrowseProblemsResponse
import com.few.api.web.support.method.UserArgument
import com.few.api.web.support.method.UserArgumentDetails
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
    private val browseProblemsUseCase: BrowseProblemsUseCase,
    private val readProblemUseCase: ReadProblemUseCase,
    private val checkProblemUseCase: CheckProblemUseCase,
    private val browseUndoneProblemsUseCase: BrowseUndoneProblemsUseCase,
) {

    @GetMapping
    fun browseProblems(@RequestParam(value = "articleId", required = false) articleId: Long?): ApiResponse<ApiResponse.SuccessBody<BrowseProblemsResponse>> {
        articleId?.let { id ->
            val useCaseOut = BrowseProblemsUseCaseIn(id).let { useCaseIn ->
                browseProblemsUseCase.execute(useCaseIn)
            }

            val response = BrowseProblemsResponse(useCaseOut.problemIds)

            return ApiResponseGenerator.success(response, HttpStatus.OK)
        }

        throw IllegalArgumentException("Invalid Parameter")
    }

    @GetMapping("/{problemId}")
    fun readProblem(
        @PathVariable(value = "problemId")
        @Min(value = 1, message = "{min.id}")
        problemId: Long,
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
        @UserArgument userArgumentDetails: UserArgumentDetails,
        @PathVariable(value = "problemId")
        @Min(value = 1, message = "{min.id}")
        problemId: Long,
        @Valid @RequestBody
        body: CheckProblemRequest,
    ): ApiResponse<ApiResponse.SuccessBody<CheckProblemResponse>> {
        val memberId = userArgumentDetails.id.toLong()
        val useCaseOut = checkProblemUseCase.execute(
            CheckProblemUseCaseIn(
                memberId,
                problemId,
                body.sub
            )
        )

        val response = CheckProblemResponse(
            explanation = useCaseOut.explanation,
            answer = useCaseOut.answer,
            isSolved = useCaseOut.isSolved
        )

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }

    @GetMapping("/unsubmitted")
    fun browseUndoneProblems(@UserArgument userArgumentDetails: UserArgumentDetails): ApiResponse<ApiResponse.SuccessBody<BrowseProblemsResponse>> {
        val memberId = userArgumentDetails.id.toLong()

        val useCaseOut = BrowseUndoneProblemsUseCaseIn(memberId).let { useCaseIn ->
            browseUndoneProblemsUseCase.execute(useCaseIn)
        }

        val response = BrowseProblemsResponse(useCaseOut.problemIds, useCaseOut.problemIds.size)

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }
}