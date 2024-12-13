package security.authentication.token

import security.AuthorityUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import security.exception.SecurityAccessTokenInvalidException
import security.TokenClaim
import security.TokenResolver
import security.TokenUserDetails

class TokenUserDetailsService(
    private val tokenResolver: TokenResolver,
) : UserDetailsService {
    private val log = KotlinLogging.logger {}

    override fun loadUserByUsername(token: String?): UserDetails {
        val claims: Claims = tokenResolver
            .resolve(token)
            ?: throw SecurityAccessTokenInvalidException("Invalid access token. accessToken: $token")

        val id = claims.get(
            TokenClaim.MEMBER_ID_CLAIM.key,
            Integer::class.java
        ).toLong()

        val roles = claims.get(
            TokenClaim.MEMBER_ROLE_CLAIM.key,
            String::class.java
        )

        val email = claims.get(
            TokenClaim.MEMBER_EMAIL_CLAIM.key,
            String::class.java
        )

        val authorities = AuthorityUtils.toAuthorities(roles)

        return TokenUserDetails(authorities, id.toString(), email)
    }
}