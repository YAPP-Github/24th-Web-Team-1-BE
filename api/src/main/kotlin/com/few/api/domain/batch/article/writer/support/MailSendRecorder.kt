package com.few.api.domain.batch.article.writer.support

private data class MemberMappedWorkBook(
    val memberId: Long,
    val workbookId: Long,
)

/** 이메일 전송 성공/실패 기록을 위한 정보 기록 클래스 */
class MailSendRecorder(
    mailServiceArgs: List<com.few.api.domain.batch.article.writer.support.MailServiceArg>,
) {
    private var records: MutableMap<MemberMappedWorkBook, Boolean> =
        mailServiceArgs.associate { MemberMappedWorkBook(it.memberId, it.workbookId) to true }.toMutableMap()
    private var failRecords: MutableMap<String, ArrayList<MemberMappedWorkBook>> = mutableMapOf()

    fun recordFail(
        memberId: Long,
        workbookId: Long,
        reason: String,
    ) {
        records[MemberMappedWorkBook(memberId, workbookId)] = false
        failRecords[reason] =
            failRecords.getOrDefault(reason, arrayListOf()).apply {
                add(MemberMappedWorkBook(memberId, workbookId))
            }
    }

    fun getSuccessMemberIds(): Set<Long> =
        records
            .filter { it.value }
            .keys
            .map { it.memberId }
            .toSet()

    fun getExecutionResult(): Map<Any, Any> {
        val executionRecords =
            records.keys
                .groupBy { it.memberId }
                .mapValues { it -> it.value.associate { it.workbookId to records[it] } }
        return if (failRecords.isNotEmpty()) {
            mapOf("records" to executionRecords, "fail" to failRecords)
        } else {
            mapOf("records" to executionRecords)
        }
    }
}