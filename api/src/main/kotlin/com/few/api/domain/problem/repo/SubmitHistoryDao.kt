package com.few.api.domain.problem.repo

import com.few.api.domain.problem.repo.command.InsertSubmitHistoryCommand
import com.few.api.domain.problem.repo.query.SelectSubmittedProblemIdsQuery
import com.few.api.domain.problem.repo.record.SubmittedProblemIdsRecord
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

    fun selectProblemIdByProblemIds(query: SelectSubmittedProblemIdsQuery): SubmittedProblemIdsRecord {
        return selectProblemIdByProblemIdsQuery(query)
            .fetch()
            .map { it[SUBMIT_HISTORY.PROBLEM_ID] }
            .let { SubmittedProblemIdsRecord(it) }
    }

    fun selectProblemIdByProblemIdsQuery(query: SelectSubmittedProblemIdsQuery) = dslContext
        .select(SUBMIT_HISTORY.PROBLEM_ID)
        .from(SUBMIT_HISTORY)
        .where(SUBMIT_HISTORY.PROBLEM_ID.`in`(query.problemIds))
        .and(SUBMIT_HISTORY.MEMBER_ID.eq(query.memberId))
        .and(SUBMIT_HISTORY.DELETED_AT.isNull)
}