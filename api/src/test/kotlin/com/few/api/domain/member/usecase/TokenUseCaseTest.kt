package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.UpdateMemberTypeCommand
import com.few.api.repo.dao.member.record.MemberEmailAndTypeRecord
import com.few.api.security.token.AuthToken
import com.few.api.security.token.TokenGenerator
import com.few.api.security.token.TokenResolver
import com.few.data.common.code.MemberType
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TokenUseCaseTest : BehaviorSpec({
    lateinit var tokenGenerator: TokenGenerator
    lateinit var tokenResolver: TokenResolver
    lateinit var memberDao: MemberDao
    lateinit var idEncryption: IdEncryption
    lateinit var useCase: TokenUseCase

    beforeContainer {
        tokenGenerator = mockk<TokenGenerator>()
        tokenResolver = mockk<TokenResolver>()
        memberDao = mockk<MemberDao>()
        idEncryption = mockk<IdEncryption>()
        useCase = TokenUseCase(tokenGenerator, tokenResolver, memberDao, idEncryption)
    }

    given("리프레시 토큰이 포함된 요청이 온 상황에서") {
        val oldRefreshToken = "refreshToken"
        val useCaseIn = TokenUseCaseIn(
            token = null,
            refreshToken = oldRefreshToken,
            at = null,
            rt = null
        )

        `when`("요청에 refreshToken이 포함되어 있는 경우") {
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
                useCase.execute(useCaseIn)

                verify(exactly = 1) { tokenResolver.resolveId(any()) }
                verify(exactly = 1) { tokenResolver.resolveEmail(any()) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any(), any()) }
            }
        }
    }

    given("멤버 아이디 정보를 암호화한 토큰이 포함된 요청이 온 상황에서") {
        val encryptedIdToken = "token"
        val useCaseIn = TokenUseCaseIn(
            token = encryptedIdToken,
            refreshToken = null,
            at = null,
            rt = null
        )

        `when`("로그인하려는 멤버의 토큰이 포함되어 있는 경우") {
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
                useCase.execute(useCaseIn)

                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 1) {
                    tokenGenerator.generateAuthToken(
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                    )
                }
                verify(exactly = 1) { memberDao.selectMemberEmailAndType(any()) }
            }
        }

        `when`("회원가입을 완료 하려는 멤버의 토큰이 포함되어 있는 경우") {
            every { idEncryption.decrypt(any()) } returns "1"

            every { tokenGenerator.generateAuthToken(any(), any(), any(), any(), any()) } returns AuthToken(
                accessToken = "accessToken",
                refreshToken = "refreshToken"
            )

            every { memberDao.selectMemberEmailAndType(any()) } returns MemberEmailAndTypeRecord(
                email = "test@gmail.com",
                memberType = MemberType.PREAUTH
            )

            every { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) } returns Unit

            then("새로운 토큰을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 1) {
                    tokenGenerator.generateAuthToken(
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                    )
                }
                verify(exactly = 1) { memberDao.selectMemberEmailAndType(any()) }
                verify(exactly = 1) { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) }
            }
        }
    }
})