package security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object TokenAuditHolder {
    private const val NOT_USE_ID_VALUE = "0"
    private const val NOT_USE_EMAIL_VALUE = ""
    private val NOT_USE_AUTHORITY: GrantedAuthority = SimpleGrantedAuthority("NOT_USE")

    fun get(): UserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            TokenUserDetails(listOf(NOT_USE_AUTHORITY), NOT_USE_ID_VALUE, NOT_USE_EMAIL_VALUE)
        } else {
            authentication.principal as TokenUserDetails
        }
    }
}