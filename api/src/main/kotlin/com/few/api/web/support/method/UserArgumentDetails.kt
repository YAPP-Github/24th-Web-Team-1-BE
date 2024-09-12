package com.few.api.web.support.method

import com.few.api.security.authentication.token.TokenUserDetails
import org.springframework.security.core.GrantedAuthority

class UserArgumentDetails(
    val isAuth: Boolean,
    authorities: List<GrantedAuthority>,
    id: String,
    email: String,
) : TokenUserDetails(
    authorities = authorities,
    id = id,
    email = email
)