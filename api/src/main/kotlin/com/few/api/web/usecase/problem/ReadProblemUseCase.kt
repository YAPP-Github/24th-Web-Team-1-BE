package com.few.api.web.usecase.problem

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.web.usecase.problem.`in`.ReadProblemUseCaseIn
import com.few.api.web.usecase.problem.out.ReadProblemContentsUseCaseOut
import com.few.api.web.usecase.problem.out.ReadProblemUseCaseOut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadProblemUseCase(
    private val prblemDao: ProblemDao,
    private val objectMapper: ObjectMapper
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadProblemUseCaseIn): ReadProblemUseCaseOut {
        val problemId = useCaseIn.problemId

        val record = prblemDao.selectProblem(SelectProblemQuery(problemId))

        val contents: List<ReadProblemContentsUseCaseOut> = objectMapper.readValue(record.contents)

        return ReadProblemUseCaseOut(
            id = record.id,
            title = record.title,
            contents = contents
        )
    }
}