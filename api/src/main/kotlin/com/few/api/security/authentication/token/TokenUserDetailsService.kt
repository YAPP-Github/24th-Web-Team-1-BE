package com.few.api.security.authentication.token

import com.few.api.security.authentication.authority.Roles
import com.few.api.security.exception.AccessTokenInvalidException
import com.few.api.security.token.TokenResolver
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import org.apache.commons.lang3.StringUtils
import org.springframework.security.core.GrantedAuthority
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
            Long::class.java
        )
        val roles = claims.get(
            MEMBER_ROLE_CLAIM_KEY,
            String::class.java
        )

        val email = claims.get(
            MEMBER_EMAIL_CLAIM_KEY,
            String::class.java
        )

        val authorities = toAuthorities(roles)

        return TokenUserDetails(authorities, id.toString(), email)
    }

    private fun toAuthorities(roles: String): List<GrantedAuthority> {
        val tokens = StringUtils.splitPreserveAllTokens(roles, "[,]")
        val rtn: MutableList<GrantedAuthority> = ArrayList()
        for (token in tokens) {
            if (token != "") {
                val role = token.trim { it <= ' ' }
                try {
                    rtn.add(Roles.valueOf(role).authority)
                } catch (exception: IllegalArgumentException) {
                    log.error { "${"Invalid role. role: {}"} $role" }
                }
            }
        }
        return rtn
    }
}