package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseOut
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
    @Transactional
    fun execute(useCaseIn: SaveMemberUseCaseIn): SaveMemberUseCaseOut {
        /** email을 통해 가입 이력이 있는지 확인 */
        val isSignUpBeforeMember = SelectMemberByEmailNotConsiderDeletedAtQuery(
            email = useCaseIn.email
        ).let {
            memberDao.selectMemberByEmail(it)
        }

        /** 가입 이력이 없다면 회원 가입 처리 */
        val token = if (Objects.isNull(isSignUpBeforeMember)) {
            InsertMemberCommand(
                email = useCaseIn.email,
                memberType = MemberType.PREAUTH
            ).let {
                memberDao.insertMember(it) ?: throw IllegalStateException("Insert Member Error")
            }
        } else {
            /** 삭제한 회원이라면 회원 타입을 PREAUTH로 변경 */
            if (isSignUpBeforeMember!!.isDeleted) {
                UpdateDeletedMemberTypeCommand(
                    id = isSignUpBeforeMember.memberId,
                    memberType = MemberType.PREAUTH
                ).let {
                    memberDao.updateMemberType(it) ?: throw IllegalStateException("Update Member Type Error")
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
                    email = useCaseIn.email,
                    confirmLink = URL("https://www.fewletter.com/auth/validation/complete?token=$token")
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