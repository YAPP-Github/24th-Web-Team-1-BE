package com.few.api.web.controller

import com.few.api.security.authentication.authority.Roles
import com.few.api.security.authentication.token.TokenUserDetails
import org.springframework.boot.test.context.TestComponent
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

@TestComponent
class TestTokenUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return TokenUserDetails(
            authorities = listOf(
                Roles.ROLE_USER.authority
            ),
            id = "1",
            email = "test@gmail.com"
        )
    }
}