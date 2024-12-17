package com.few.api.domain.member.usecase.transaction

import com.few.api.domain.common.exception.InsertException
import com.few.api.domain.common.vo.MemberType
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.command.InsertMemberCommand
import com.few.api.domain.member.repo.command.UpdateDeletedMemberTypeCommand
import com.few.api.domain.member.repo.record.MemberIdAndIsDeletedRecord
import com.few.api.domain.member.usecase.dto.SaveMemberTxCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberTxCaseOut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SaveMemberTxCase(
    private val memberDao: MemberDao,
) {
    companion object {
        private const val AUTH_HEAD_COMMENT = "few 로그인 링크입니다."
        private const val AUTH_SUB_COMMENT = "로그인하시려면 아래 버튼을 눌러주세요!"
        private const val SIGNUP_HEAD_COMMENT = "few에 가입해주셔서 감사합니다."
        private const val SIGNUP_SUB_COMMENT = "가입하신 이메일 주소를 확인해주세요."
    }

    @Transactional
    fun execute(dto: SaveMemberTxCaseIn): SaveMemberTxCaseOut =
        dto.record?.let { signUpBeforeMember ->
            signUpBeforeMember.takeIf { it.isDeleted }?.let {
                ifDeletedMember(it.memberId)
            } ?: ifMember(signUpBeforeMember)
        } ?: ifNewMember(dto.email)

    private fun ifDeletedMember(memberId: Long): SaveMemberTxCaseOut {
        memberDao
            .updateMemberType(
                UpdateDeletedMemberTypeCommand(
                    id = memberId,
                    memberType = MemberType.PREAUTH,
                ),
            ).also {
                if (it != 1L) {
                    throw InsertException("member.updatefail.record")
                }
            }

        return SaveMemberTxCaseOut(
            headComment = AUTH_HEAD_COMMENT,
            subComment = AUTH_SUB_COMMENT,
            memberId = memberId,
        )
    }

    private fun ifMember(signUpBeforeMember: MemberIdAndIsDeletedRecord): SaveMemberTxCaseOut =
        SaveMemberTxCaseOut(
            headComment = AUTH_HEAD_COMMENT,
            subComment = AUTH_SUB_COMMENT,
            memberId = signUpBeforeMember.memberId,
        )

    private fun ifNewMember(email: String): SaveMemberTxCaseOut =
        memberDao
            .insertMember(
                InsertMemberCommand(
                    email = email,
                    memberType = MemberType.PREAUTH,
                ),
            )?.let {
                SaveMemberTxCaseOut(
                    headComment = SIGNUP_HEAD_COMMENT,
                    subComment = SIGNUP_SUB_COMMENT,
                    memberId = it,
                )
            } ?: throw InsertException("member.insertfail.record")
}