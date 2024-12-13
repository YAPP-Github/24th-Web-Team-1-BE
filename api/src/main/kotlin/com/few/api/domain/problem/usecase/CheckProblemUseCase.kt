package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.SubmitHistoryDao
import com.few.api.domain.problem.repo.command.InsertSubmitHistoryCommand
import com.few.api.domain.problem.repo.query.SelectProblemAnswerQuery
import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseIn
import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseOut
import com.few.api.domain.common.exception.InsertException
import com.few.api.domain.common.exception.NotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CheckProblemUseCase(
    private val problemDao: ProblemDao,
    private val submitHistoryDao: SubmitHistoryDao,
) {

    @Transactional
    fun execute(useCaseIn: CheckProblemUseCaseIn): CheckProblemUseCaseOut {
        val memberId = useCaseIn.memberId
        val problemId = useCaseIn.problemId
        val submitAns = useCaseIn.sub

        val record = problemDao.selectProblemAnswer(SelectProblemAnswerQuery(problemId)) ?: throw NotFoundException("problem.notfound.id")
        val isSolved = record.answer == submitAns

        submitHistoryDao.insertSubmitHistory(
            InsertSubmitHistoryCommand(problemId, memberId, submitAns, isSolved)
        ) ?: throw InsertException("submit.insertfail.record")

        return CheckProblemUseCaseOut(
            explanation = record.explanation,
            isSolved = isSolved,
            answer = record.answer
        )
    }
}