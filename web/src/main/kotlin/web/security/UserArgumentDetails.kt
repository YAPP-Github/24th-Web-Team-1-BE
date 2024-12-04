package web.security

import org.springframework.security.core.GrantedAuthority
import security.TokenUserDetails

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