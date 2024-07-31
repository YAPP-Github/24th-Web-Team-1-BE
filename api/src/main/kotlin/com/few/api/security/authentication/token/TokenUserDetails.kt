package com.few.api.security.authentication.token

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class TokenUserDetails(
    private val authorities: List<GrantedAuthority>,
    private val id: String,
) : UserDetails {

    companion object {
        private const val NOT_USE_PASSWORD_VALUE = "0"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.toMutableList()
    }

    override fun getPassword(): String {
        return NOT_USE_PASSWORD_VALUE
    }

    override fun getUsername(): String {
        return id
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}