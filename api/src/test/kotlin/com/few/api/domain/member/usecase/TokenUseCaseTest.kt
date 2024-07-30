package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.UpdateMemberTypeCommand
import com.few.api.repo.dao.member.record.MemberIdAndTypeRecord
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

    given("토큰 요청이 온 상황에서") {
        `when`("요청에 refreshToken이 포함되어 있는 경우") {
            every { tokenResolver.resolveId(any()) } returns 1L
            every { tokenGenerator.generateAuthToken(any(), any()) } returns AuthToken(
                accessToken = "accessToken",
                refreshToken = "refreshToken"
            )
            then("새로운 토큰을 반환한다") {
                useCase.execute(
                    TokenUseCaseIn(
                        token = null,
                        refreshToken = "refreshToken",
                        at = null,
                        rt = null
                    )
                )
                verify(exactly = 1) { tokenResolver.resolveId(any()) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any()) }
            }
        }

        `when`("요청에 로그인을 하려는 멤버의 token이 포함되어 있는 경우") {
            every { idEncryption.decrypt(any()) } returns "1"
            every { tokenGenerator.generateAuthToken(any(), any(), any(), any()) } returns AuthToken(
                accessToken = "accessToken",
                refreshToken = "refreshToken"
            )
            every { memberDao.selectMemberIdAndType(any()) } returns MemberIdAndTypeRecord(
                memberId = 1L,
                memberType = MemberType.NORMAL
            )

            then("새로운 토큰을 반환한다") {
                useCase.execute(
                    TokenUseCaseIn(
                        token = "token",
                        refreshToken = null,
                        at = null,
                        rt = null
                    )
                )
                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any(), any(), any()) }
                verify(exactly = 1) { memberDao.selectMemberIdAndType(any()) }
            }
        }

        `when`("요청에 회원가입을 완료 하려는 멤버의 token이 포함되어 있는 경우") {
            every { idEncryption.decrypt(any()) } returns "1"
            every { tokenGenerator.generateAuthToken(any(), any(), any(), any()) } returns AuthToken(
                accessToken = "accessToken",
                refreshToken = "refreshToken"
            )
            every { memberDao.selectMemberIdAndType(any()) } returns MemberIdAndTypeRecord(
                memberId = 1L,
                memberType = MemberType.PREAUTH
            )

            every { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) } returns Unit

            then("새로운 토큰을 반환한다") {
                useCase.execute(
                    TokenUseCaseIn(
                        token = "token",
                        refreshToken = null,
                        at = null,
                        rt = null
                    )
                )
                verify(exactly = 1) { idEncryption.decrypt(any()) }
                verify(exactly = 1) { tokenGenerator.generateAuthToken(any(), any(), any(), any()) }
                verify(exactly = 1) { memberDao.selectMemberIdAndType(any()) }
                verify(exactly = 1) { memberDao.updateMemberType(any(UpdateMemberTypeCommand::class)) }
            }
        }
    }
})