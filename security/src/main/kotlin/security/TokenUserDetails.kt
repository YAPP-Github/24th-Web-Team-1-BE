package security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

open class TokenUserDetails(
    val authorities: List<GrantedAuthority>,
    val id: String,
    val email: String,
) : UserDetails {
    companion object {
        private const val NOT_USE_PASSWORD_VALUE = "0"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities.toMutableList()

    override fun getPassword(): String = NOT_USE_PASSWORD_VALUE

    override fun getUsername(): String = id

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}