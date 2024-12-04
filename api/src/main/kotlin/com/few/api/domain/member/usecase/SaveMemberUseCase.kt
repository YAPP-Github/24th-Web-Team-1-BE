package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.SaveMemberTxCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseOut
import com.few.api.domain.member.usecase.transaction.SaveMemberTxCase
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectMemberByEmailNotConsiderDeletedAtQuery
import com.few.email.service.member.SendAuthEmailService
import com.few.email.service.member.dto.Content
import com.few.email.service.member.dto.SendAuthEmailArgs
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URL

@Component
class SaveMemberUseCase(
    private val memberDao: MemberDao,
    private val sendAuthEmailService: SendAuthEmailService,
    private val idEncryption: IdEncryption,
    private val saveMemberTxCase: SaveMemberTxCase,
) {
    @Transactional
    fun execute(useCaseIn: SaveMemberUseCaseIn): SaveMemberUseCaseOut {
        /** email을 통해 가입 이력이 있는지 확인 */
        val (headComment, subComment, memberId) = memberDao.selectMemberByEmail(
            SelectMemberByEmailNotConsiderDeletedAtQuery(
                email = useCaseIn.email
            )
        ).let {
            /** 가입 이력 여부를 기준으로 가입 처리 */
            saveMemberTxCase.execute(
                SaveMemberTxCaseIn(
                    record = it,
                    email = useCaseIn.email
                )
            )
        }

        /** 회원 ID를 암호화하여 토큰으로 사용 */
        val token = idEncryption.encrypt(memberId.toString())

        runCatching {
            sendAuthEmailService.send(
                SendAuthEmailArgs(
                    to = useCaseIn.email,
                    subject = "[FEW] 인증 이메일 주소를 확인해주세요.",
                    template = "auth",
                    content = Content(
                        headComment = headComment,
                        subComment = subComment,
                        email = useCaseIn.email,
                        confirmLink = URL("https://www.fewletter.com/auth/validation/complete?auth_token=$token")
                    )
                )
            )
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