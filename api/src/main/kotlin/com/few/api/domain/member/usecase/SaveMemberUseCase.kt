package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseOut
import com.few.api.exception.common.InsertException
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.command.UpdateDeletedMemberTypeCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailNotConsiderDeletedAtQuery
import com.few.data.common.code.MemberType
import com.few.email.service.member.SendAuthEmailService
import com.few.email.service.member.dto.Content
import com.few.email.service.member.dto.SendAuthEmailArgs
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.util.*

@Component
class SaveMemberUseCase(
    private val memberDao: MemberDao,
    private val sendAuthEmailService: SendAuthEmailService,
    private val idEncryption: IdEncryption,
) {
    companion object {
        private const val AUTH_HEAD_COMMENT = "few 로그인 링크입니다."
        private const val AUTH_SUB_COMMENT = "로그인하시려면 아래 버튼을 눌러주세요!"
        private const val SIGNUP_HEAD_COMMENT = "few에 가입해주셔서 감사합니다."
        private const val SIGNUP_SUB_COMMENT = "가입하신 이메일 주소를 확인해주세요."
    }

    @Transactional
    fun execute(useCaseIn: SaveMemberUseCaseIn): SaveMemberUseCaseOut {
        /** email을 통해 가입 이력이 있는지 확인 */
        val isSignUpBeforeMember = SelectMemberByEmailNotConsiderDeletedAtQuery(
            email = useCaseIn.email
        ).let {
            memberDao.selectMemberByEmail(it)
        }

        var headComment = AUTH_HEAD_COMMENT
        var subComment = AUTH_SUB_COMMENT
        var email = ""

        /** 가입 이력이 없다면 회원 가입 처리 */
        val token = if (Objects.isNull(isSignUpBeforeMember)) {
            headComment = SIGNUP_HEAD_COMMENT
            subComment = SIGNUP_SUB_COMMENT
            email = useCaseIn.email
            InsertMemberCommand(
                email = useCaseIn.email,
                memberType = MemberType.PREAUTH
            ).let {
                memberDao.insertMember(it) ?: throw InsertException("member.insertfail.record")
            }
        } else {
            /** 삭제한 회원이라면 회원 타입을 PREAUTH로 변경 */
            if (isSignUpBeforeMember!!.isDeleted) {
                UpdateDeletedMemberTypeCommand(
                    id = isSignUpBeforeMember.memberId,
                    memberType = MemberType.PREAUTH
                ).let {
                    val isUpdate = memberDao.updateMemberType(it)
                    if (isUpdate != 1L) {
                        throw InsertException("member.updatefail.record")
                    }
                    memberDao.selectMemberByEmail(
                        SelectMemberByEmailNotConsiderDeletedAtQuery(
                            email = useCaseIn.email
                        )
                    )?.memberId ?: throw InsertException("member.selectfail.record")
                }
            } else {
                /** 이미 가입한 회원이라면 회원 ID를 반환 */
                isSignUpBeforeMember.memberId
            }
        }.let {
            /** 회원 ID를 암호화하여 토큰으로 사용 */
            idEncryption.encrypt(it.toString())
        }

        runCatching {
            SendAuthEmailArgs(
                to = useCaseIn.email,
                subject = "[FEW] 인증 이메일 주소를 확인해주세요.",
                template = "auth",
                content = Content(
                    headComment = headComment,
                    subComment = subComment,
                    email = email,
                    confirmLink = URL("https://www.fewletter.com/auth/validation/complete?auth_token=$token")
                )
            ).let {
                sendAuthEmailService.send(it)
            }
        }.onFailure {
            return SaveMemberUseCaseOut(
                isSendAuthEmail = false
            )
        }

        return SaveMemberUseCaseOut(
            isSendAuthEmail = true
        )
    }
}