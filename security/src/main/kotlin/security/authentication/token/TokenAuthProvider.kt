package security.authentication.token

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

class TokenAuthProvider(
    private val tokenUserDetailsService: UserDetailsService,
) : AuthenticationProvider {

    @Throws(AuthenticationException::class, AccessDeniedException::class)
    override fun authenticate(authentication: Authentication): Authentication? {
        val token = Optional.ofNullable(authentication.principal)
            .map { obj: Any? ->
                String::class.java.cast(
                    obj
                )
            }
            .orElseThrow {
                IllegalArgumentException(
                    "token is missing"
                )
            }
        val userDetails: UserDetails = tokenUserDetailsService.loadUserByUsername(token)
        SecurityContextHolder.getContext().authentication = authentication
        return if (authentication is PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken(
                userDetails,
                userDetails.password,
                userDetails.authorities
            )
        } else {
            null
        }
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return PreAuthenticatedAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}