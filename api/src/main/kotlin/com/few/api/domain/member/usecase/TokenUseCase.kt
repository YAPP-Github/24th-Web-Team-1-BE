package com.few.api.domain.member.usecase

import com.few.api.config.crypto.IdEncryption
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.domain.member.usecase.dto.TokenUseCaseOut
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.UpdateMemberTypeCommand
import com.few.api.security.authentication.authority.Roles
import com.few.api.security.token.TokenGenerator
import com.few.api.security.token.TokenResolver
import com.few.data.common.code.MemberType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TokenUseCase(
    private val tokenGenerator: TokenGenerator,
    private val tokenResolver: TokenResolver,
    private val memberDao: MemberDao,
    private val idEncryption: IdEncryption,
) {

    @Transactional
    fun execute(useCaseIn: TokenUseCaseIn): TokenUseCaseOut {
        /** refreshToken이 요청에 포함되어 있으면 refreshToken을 통해 memberId를 추출하여 새로운 토큰을 발급 */
        useCaseIn.refreshToken?.let { token ->
            runCatching {
                /** refreshToken을 통해 memberId를 추출 */
                tokenResolver.resolveId(token)
            }.onSuccess {
                tokenGenerator.generateAuthToken(
                    memberId = it,
                    memberRoles = listOf(Roles.ROLE_USER)
                ).let { token ->
                    return TokenUseCaseOut(
                        accessToken = token.accessToken,
                        refreshToken = token.refreshToken,
                        isLogin = true
                    )
                }
            }.onFailure {
                // todo 별도 에러 구현 & 메시지 논의
                throw IllegalStateException("Token Resolve Error")
            }
        }

        val accessTokenValidTime: Long? = useCaseIn.at
        val refreshTokenValidTime: Long? = useCaseIn.rt
        val memberId = useCaseIn.token?.let {
            idEncryption.decrypt(it).toLong()
        } ?: throw IllegalStateException("Cannot Decrypt Id")

        /** id가 요청에 포함되어 있으면 id를 통해 새로운 토큰을 발급 */
        val token = tokenGenerator.generateAuthToken(
            memberId = memberId,
            memberRoles = listOf(Roles.ROLE_USER),
            accessTokenValidTime = accessTokenValidTime,
            refreshTokenValidTime = refreshTokenValidTime
        )

        // 요청에서 파람을 통해 로그인 혹은 회원가입인지 파악할 수 있으면 해당 로직 제거
        val memberType = memberDao.selectMemberIdAndType(memberId)
            ?: throw IllegalStateException("Member Not Found")

        if (memberType.memberType == MemberType.PREAUTH) {
            UpdateMemberTypeCommand(
                id = memberId,
                memberType = MemberType.NORMAL
            ).let { command ->
                memberDao.updateMemberType(command)
            }
        }

        return TokenUseCaseOut(
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
            isLogin = false
        )
    }
}