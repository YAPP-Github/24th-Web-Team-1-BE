package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.SaveMemberTxCaseOut
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.domain.member.usecase.transaction.SaveMemberTxCase
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectMemberByEmailNotConsiderDeletedAtQuery
import com.few.api.repo.dao.member.record.MemberIdAndIsDeletedRecord
import com.few.email.service.member.SendAuthEmailService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.mockito.ArgumentMatchers.any

class SaveMemberUseCaseTest : BehaviorSpec({
    lateinit var memberDao: MemberDao
    lateinit var sendAuthEmailService: SendAuthEmailService
    lateinit var idEncryption: IdEncryption
    lateinit var saveMemberTxCase: SaveMemberTxCase
    lateinit var useCase: SaveMemberUseCase

    beforeContainer {
        memberDao = mockk<MemberDao>()
        sendAuthEmailService = mockk<SendAuthEmailService>()
        idEncryption = mockk<IdEncryption>()
        saveMemberTxCase = mockk<SaveMemberTxCase>()
        useCase = SaveMemberUseCase(memberDao, sendAuthEmailService, idEncryption, saveMemberTxCase)
    }

    given("회원가입/로그인 요청이 온 상황에서") {
        val email = "test@gmail.com"
        val useCaseIn = SaveMemberUseCaseIn(email = email)

        `when`("요청의 이메일이 가입 이력이 없는 경우") {
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns null

            every { saveMemberTxCase.execute(any()) } returns SaveMemberTxCaseOut(
                headComment = "headComment",
                subComment = "subComment",
                memberId = 1L
            )

            val token = "encryptedToken"
            every { idEncryption.encrypt(any()) } returns token

            every { sendAuthEmailService.send(any()) } returns Unit

            then("인증 이메일 발송 성공 응답을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.isSendAuthEmail shouldBe true

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 1) { saveMemberTxCase.execute(any()) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
                verify(exactly = 1) { sendAuthEmailService.send(any()) }
            }
        }

        `when`("요청의 이메일이 가입되어 있는 경우") {
            val memberId = 1L
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns MemberIdAndIsDeletedRecord(
                memberId = memberId,
                isDeleted = false
            )

            every { saveMemberTxCase.execute(any()) } returns SaveMemberTxCaseOut(
                headComment = "headComment",
                subComment = "subComment",
                memberId = 1L
            )

            val token = "encryptedToken"
            every { idEncryption.encrypt(any()) } returns token

            every { sendAuthEmailService.send(any()) } returns Unit

            then("인증 이메일 발송 성공 응답을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.isSendAuthEmail shouldBe true

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 1) { saveMemberTxCase.execute(any()) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
                verify(exactly = 1) { sendAuthEmailService.send(any()) }
            }
        }

        `when`("요청의 이메일이 삭제된 회원인 경우") {
            val memberId = 1L
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns MemberIdAndIsDeletedRecord(
                memberId = memberId,
                isDeleted = true
            )

            every { saveMemberTxCase.execute(any()) } returns SaveMemberTxCaseOut(
                headComment = "headComment",
                subComment = "subComment",
                memberId = 1L
            )

            val token = "encryptedToken"
            every { idEncryption.encrypt(any()) } returns token

            every { sendAuthEmailService.send(any()) } returns Unit

            then("인증 이메일 발송 성공 응답을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.isSendAuthEmail shouldBe true

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 1) { saveMemberTxCase.execute(any()) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
                verify(exactly = 1) { sendAuthEmailService.send(any()) }
            }
        }

        `when`("인증 이메일 발송에 실패한 경우") {
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns null

            every { saveMemberTxCase.execute(any()) } returns SaveMemberTxCaseOut(
                headComment = "headComment",
                subComment = "subComment",
                memberId = 1L
            )

            val token = "encryptedToken"
            every { idEncryption.encrypt(any()) } returns token

            every { sendAuthEmailService.send(any()) } throws Exception()

            then("인증 이메일 발송 실패 응답을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.isSendAuthEmail shouldBe false

                shouldThrow<Exception> {
                    sendAuthEmailService.send(any())
                }
            }
        }
    }
})