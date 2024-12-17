package com.few.api.config.web.controller

import org.springframework.boot.test.context.TestComponent
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import security.Roles
import security.TokenUserDetails

@TestComponent
class ApiTestTokenUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        TokenUserDetails(
            authorities =
                listOf(
                    Roles.ROLE_USER.authority,
                ),
            id = "1",
            email = "test@gmail.com",
        )
}