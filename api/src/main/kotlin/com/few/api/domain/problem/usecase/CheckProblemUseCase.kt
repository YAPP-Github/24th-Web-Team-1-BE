package com.few.api.domain.problem.usecase

import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.command.InsertSubmitHistoryCommand
import com.few.api.repo.dao.problem.query.SelectProblemAnswerQuery
import com.few.api.domain.problem.usecase.`in`.CheckProblemUseCaseIn
import com.few.api.domain.problem.usecase.out.CheckProblemUseCaseOut
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

        val record = problemDao.selectProblemAnswer(SelectProblemAnswerQuery(problemId)) ?: throw RuntimeException("Problem Answer with ID $problemId not found") // TODO: 에러 표준화
        val isSolved = record.answer == submitAns

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