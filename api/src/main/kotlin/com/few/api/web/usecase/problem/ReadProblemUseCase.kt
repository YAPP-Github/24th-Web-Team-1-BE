package com.few.api.web.usecase.problem

import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.web.usecase.problem.`in`.ReadProblemUseCaseIn
import com.few.api.web.usecase.problem.out.ReadProblemUseCaseOut
import org.springframework.stereotype.Component

@Component
class ReadProblemUseCase(
    private val prblemDao: ProblemDao
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadProblemUseCaseIn): ReadProblemUseCaseOut {
        val problemId = useCaseIn.problemId

        val record = ProblemDao.selectProblem(SelectProblemQuery(problemId))

        return ReadProblemUseCaseOut(
            id = record.id,
            title = record.title,
            contents = record.contents // TODO: 리스트 변환
        )
    }
}