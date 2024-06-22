package com.few.api.web.usecase.problem

import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.command.InsertSubmitHistoryCommand
import com.few.api.repo.dao.problem.query.SelectProblemAnswerQuery
import com.few.api.web.usecase.problem.`in`.CheckProblemUseCaseIn
import com.few.api.web.usecase.problem.out.CheckProblemUseCaseOut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CheckProblemUseCase(
    private val problemDao: ProblemDao,
    private val submitHistoryDao: SubmitHistoryDao
) {

    @Transactional
    fun execute(useCaseIn: CheckProblemUseCaseIn): CheckProblemUseCaseOut {
        val problemId = useCaseIn.problemId
        val submitAns = useCaseIn.sub

        val record = problemDao.selectProblemAnswerByProblemId(SelectProblemAnswerQuery(problemId))
        val isSolved = submitAns.equals(record.answer)

        val submitHistoryId = submitHistoryDao.insertSubmitHistory(
            InsertSubmitHistoryCommand(problemId, 1L, submitAns, isSolved)
        ) // not used 'submitHistoryId'

        return CheckProblemUseCaseOut(
            explanation = record.explanation,
            isSolved = isSolved,
            answer = record.answer
        )
    }
}