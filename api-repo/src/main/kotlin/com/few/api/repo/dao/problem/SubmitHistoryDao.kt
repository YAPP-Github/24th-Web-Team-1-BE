package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.command.InsertSubmitHistoryCommand
import jooq.jooq_dsl.Tables.SUBMIT_HISTORY
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class SubmitHistoryDao(
    private val dslContext: DSLContext,
) {

    fun insertSubmitHistory(command: InsertSubmitHistoryCommand): Long? {
        val result = insertSubmitCommand(command)
            .returning(SUBMIT_HISTORY.ID)
            .fetchOne()

        return result?.getValue(SUBMIT_HISTORY.ID)
    }

    fun insertSubmitCommand(command: InsertSubmitHistoryCommand) =
        dslContext.insertInto(SUBMIT_HISTORY)
            .set(SUBMIT_HISTORY.PROBLEM_ID, command.problemId)
            .set(SUBMIT_HISTORY.MEMBER_ID, command.memberId)
            .set(SUBMIT_HISTORY.SUBMIT_ANS, command.submitAns)
            .set(SUBMIT_HISTORY.IS_SOLVED, command.isSolved)
}