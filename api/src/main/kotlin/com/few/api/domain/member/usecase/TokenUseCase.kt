package com.few.api.domain.member.usecase

import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.vo.MemberType
import com.few.api.domain.member.exception.NotValidTokenException
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.command.UpdateMemberTypeCommand
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.domain.member.usecase.dto.TokenUseCaseOut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import security.Roles
import security.TokenGenerator
import security.TokenResolver
import security.encryptor.IdEncryptor

@Suppress("ktlint:standard:backing-property-naming")
@Component
class TokenUseCase(
    private val tokenGenerator: TokenGenerator,
    private val tokenResolver: TokenResolver,
    private val memberDao: MemberDao,
    private val idEncryption: IdEncryptor,
) {
    @Transactional
    fun execute(useCaseIn: TokenUseCaseIn): TokenUseCaseOut {
        var isLogin = true

        /** refreshToken이 요청에 포함되어 있으면 refreshToken을 통해 memberId를 추출하여 새로운 토큰을 발급 */
        var _memberId: Long? = null
        var _memberEmail: String? = null
        useCaseIn.refreshToken?.let { token ->
            runCatching {
                /** refreshToken을 통해 memberId를 추출 */
                _memberId = tokenResolver.resolveId(token)
                _memberEmail = tokenResolver.resolveEmail(token)
            }.onSuccess {
                tokenGenerator
                    .generateAuthToken(
                        memberId = _memberId,
                        memberEmail = _memberEmail,
                        memberRoles = listOf(Roles.ROLE_USER),
                    ).let { token ->
                        return TokenUseCaseOut(
                            accessToken = token.accessToken,
                            refreshToken = token.refreshToken,
                            isLogin = true,
                        )
                    }
            }.onFailure {
                throw NotValidTokenException("member.notvalid.token")
            }
        }

        val accessTokenValidTime: Long? = useCaseIn.at
        val refreshTokenValidTime: Long? = useCaseIn.rt
        val memberId =
            useCaseIn.token?.let {
                idEncryption.decrypt(it).toLong()
            } ?: throw IllegalStateException("member.notvalid.fromEmailId")

        val memberEmailAndTypeRecord =
            memberDao.selectMemberEmailAndType(memberId)
                ?: throw NotFoundException("member.notfound.id")

        if (memberEmailAndTypeRecord.memberType == MemberType.PREAUTH) {
            isLogin = false
            memberDao.updateMemberType(
                UpdateMemberTypeCommand(
                    id = memberId,
                    memberType = MemberType.NORMAL,
                ),
            )
        }

        /** id가 요청에 포함되어 있으면 id를 통해 새로운 토큰을 발급 */
        val token =
            tokenGenerator.generateAuthToken(
                memberId = memberId,
                memberEmail = memberEmailAndTypeRecord.email,
                memberRoles = listOf(Roles.ROLE_USER),
                accessTokenValidTime = accessTokenValidTime,
                refreshTokenValidTime = refreshTokenValidTime,
            )

        return TokenUseCaseOut(
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
            isLogin = isLogin,
        )
    }
}