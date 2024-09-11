package com.few.api.security.authentication.token

import com.few.api.security.authentication.authority.AuthorityUtils
import com.few.api.security.exception.AccessTokenInvalidException
import com.few.api.security.token.TokenResolver
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class TokenUserDetailsService(
    private val tokenResolver: TokenResolver,
) : UserDetailsService {

    private val log = KotlinLogging.logger {}

    companion object {
        private const val MEMBER_ID_CLAIM_KEY = "memberId"
        private const val MEMBER_EMAIL_CLAIM_KEY = "memberEmail"
        private const val MEMBER_ROLE_CLAIM_KEY = "memberRole"
    }

    override fun loadUserByUsername(token: String?): UserDetails {
        val claims: Claims = tokenResolver
            .resolve(token)
            ?: throw AccessTokenInvalidException("Invalid access token. accessToken: $token")

        val id = claims.get(
            MEMBER_ID_CLAIM_KEY,
            Integer::class.java
        ).toLong()

        val roles = claims.get(
            MEMBER_ROLE_CLAIM_KEY,
            String::class.java
        )

        val email = claims.get(
            MEMBER_EMAIL_CLAIM_KEY,
            String::class.java
        )

        val authorities = AuthorityUtils.toAuthorities(roles)

        return TokenUserDetails(authorities, id.toString(), email)
    }
}