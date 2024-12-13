package com.few.api.domain.member.usecase

import com.few.api.domain.common.vo.MemberType
import security.encryptor.IdEncryptor
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.command.UpdateMemberTypeCommand
import com.few.api.domain.member.repo.record.MemberEmailAndTypeRecord
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import security.AuthToken
import security.TokenGenerator
import security.TokenResolver

class TokenUseCaseTest : BehaviorSpec({
    lateinit var tokenGenerator: TokenGenerator
    lateinit var tokenResolver: TokenResolver
    lateinit var memberDao: MemberDao
    lateinit var idEncryption: IdEncryptor
    lateinit var useCase: TokenUseCase

    beforeContainer {
        tokenGenerator = mockk<TokenGenerator>()
        tokenResolver = mockk<TokenResolver>()
        memberDao = mockk<MemberDao>()
        idEncryption = mockk<IdEncryptor>()
        useCase = TokenUseCase(tokenGenerator, tokenResolver, memberDao, idEncryption)
    }

    given("리프레시 토큰이 포함된 토큰 갱신 요청이 온 상황에서") {
        val oldRefreshToken = "refreshToken"
        val useCaseIn = TokenUseCaseIn(
            token = null,
            refreshToken = oldRefreshToken,
            at = null,
            rt = null
        )

        `when`("유효한 리프레시 토큰인 경우") {
            val memberId = 1L
            every { tokenResolver.resolveId(any()) } returns memberId

            val email = "test@gmail.com"
            every { tokenResolver.resolveEmail(any()) } returns email

            val accessToken = "newAccessToken"
            val refreshToken = "newRefreshToken"
            every { tokenGenerator.generateAuthToken(any(), any(), any()) } returns AuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            then("새로운 토큰을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.accessToken shouldBe accessToken
                useCaseOut.refreshToken shouldBe refreshToken
                useCaseOut.isLogin shouldBe true

                verify(exactly = 1) { tokenResolver.resolveId(any()) }
                verify(exactly = 1) { tokenResolver.resolveEmail(any()) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any(), any()) }
            }
        }

        `when`("유효하지 않은 리프레시 토큰인 경우") {
            every { tokenResolver.resolveId(any()) } throws IllegalStateException()

            then("예외를 반환한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { tokenResolver.resolveId(any()) }
                verify(exactly = 0) { tokenResolver.resolveEmail(any()) }
                verify(exactly = 0) { tokenGenerator.generateAuthToken(any(), any(), any()) }
            }
        }
    }

    given("멤버 아이디 정보를 암호화한 토큰이 포함된 토큰 갱신 요청이 온 상황에서") {
        val encryptedIdToken = "token"
        val useCaseIn = TokenUseCaseIn(
            token = encryptedIdToken,
            refreshToken = null,
            at = null,
            rt = null
        )

        `when`("멤버 인증 토큰이 유효하고 인증을 위한 요청인 경우") {
            val decryptedId = "1"
            every { idEncryption.decrypt(any()) } returns decryptedId

            val accessToken = "newAccessToken"
            val refreshToken = "newRefreshToken"
            every { tokenGenerator.generateAuthToken(any(), any(), any(), any(), any()) } returns AuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            val email = "test@gmail.com"
            every { memberDao.selectMemberEmailAndType(any()) } returns MemberEmailAndTypeRecord(
                email = email,
                memberType = MemberType.NORMAL
            )

            then("새로운 토큰을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.accessToken shouldBe accessToken
                useCaseOut.refreshToken shouldBe refreshToken
                useCaseOut.isLogin shouldBe true

                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 1) { memberDao.selectMemberEmailAndType(any()) }
                verify(exactly = 0) { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any(), any(), any(), any()) }
            }
        }

        `when`("멤버 인증 토큰이 유효하고 가입을 위한 요청인 경우") {
            val decryptedId = "1"
            every { idEncryption.decrypt(any()) } returns decryptedId

            val accessToken = "newAccessToken"
            val refreshToken = "newRefreshToken"
            every { tokenGenerator.generateAuthToken(any(), any(), any(), any(), any()) } returns AuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            val email = "test@gmail.com"
            every { memberDao.selectMemberEmailAndType(any()) } returns MemberEmailAndTypeRecord(
                email = email,
                memberType = MemberType.PREAUTH
            )

            every { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) } returns Unit

            then("새로운 토큰을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.accessToken shouldBe accessToken
                useCaseOut.refreshToken shouldBe refreshToken
                useCaseOut.isLogin shouldBe false

                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 1) { memberDao.selectMemberEmailAndType(any()) }
                verify(exactly = 1) { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any(), any(), any(), any()) }
            }
        }

        `when`("유효하지 않은 멤버 인증 토큰인 경우") {
            every { idEncryption.decrypt(any()) } throws IllegalStateException()

            then("예외를 반환한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 0) { memberDao.selectMemberEmailAndType(any()) }
                verify(exactly = 0) { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) }
                verify(exactly = 0) { tokenGenerator.generateAuthToken(any(), any(), any(), any(), any()) }
            }
        }
    }
})