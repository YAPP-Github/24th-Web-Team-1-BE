package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.UpdateDeletedMemberTypeCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailNotConsiderDeletedAtQuery
import com.few.api.repo.dao.member.record.MemberIdAndIsDeletedRecord
import com.few.email.service.member.SendAuthEmailService
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SaveMemberUseCaseTest : BehaviorSpec({
    lateinit var memberDao: MemberDao
    lateinit var sendAuthEmailService: SendAuthEmailService
    lateinit var idEncryption: IdEncryption
    lateinit var useCase: SaveMemberUseCase
    val useCaseIn = SaveMemberUseCaseIn(email = "test@gmail.com")

    beforeContainer {
        memberDao = mockk<MemberDao>()
        sendAuthEmailService = mockk<SendAuthEmailService>()
        idEncryption = mockk<IdEncryption>()
        useCase = SaveMemberUseCase(memberDao, sendAuthEmailService, idEncryption)
    }

    given("회원가입/로그인 요청이 온 상황에서") {
        `when`("요청의 이메일이 가입 이력이 없는 경우") {
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns null
            every { memberDao.insertMember(any()) } returns 1L
            every { idEncryption.encrypt(any()) } returns "encryptedToken"
            every { sendAuthEmailService.send(any()) } returns Unit

            then("인증 이메일 발송 성공 응답을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 1) { memberDao.insertMember(any()) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
            }
        }

        `when`("요청의 이메일이 가입되어 있는 경우") {
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns MemberIdAndIsDeletedRecord(
                memberId = 1L,
                isDeleted = false
            )
            every { idEncryption.encrypt(any()) } returns "encryptedToken"
            every { sendAuthEmailService.send(any()) } returns Unit

            then("인증 이메일 발송 성공 응답을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 0) { memberDao.insertMember(any()) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
            }
        }

        `when`("요청의 이메일이 삭제된 회원인 경우") {
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns MemberIdAndIsDeletedRecord(
                memberId = 1L,
                isDeleted = true
            )
            every { memberDao.updateMemberType(any(UpdateDeletedMemberTypeCommand::class)) } returns 1L
            every { idEncryption.encrypt(any()) } returns "encryptedToken"

            then("인증 이메일 발송 성공 응답을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 0) { memberDao.insertMember(any()) }
                verify(exactly = 1) { memberDao.updateMemberType(any(UpdateDeletedMemberTypeCommand::class)) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
            }
        }

        `when`("인증 이메일 발송에 실패한 경우") {
            every { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) } returns null
            every { memberDao.insertMember(any()) } returns 1L
            every { idEncryption.encrypt(any()) } returns "encryptedToken"

            every { sendAuthEmailService.send(any()) } throws Exception()

            then("인증 이메일 발송 실패 응답을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { memberDao.selectMemberByEmail(any(SelectMemberByEmailNotConsiderDeletedAtQuery::class)) }
                verify(exactly = 1) { memberDao.insertMember(any()) }
                verify(exactly = 1) { idEncryption.encrypt(any()) }
            }
        }
    }
})