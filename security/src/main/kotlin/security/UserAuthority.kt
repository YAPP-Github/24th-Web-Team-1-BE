package security

import org.springframework.security.core.GrantedAuthority

class UserAuthority : GrantedAuthority {
    override fun getAuthority(): String = Roles.ROLE_USER.role
}