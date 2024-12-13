package security

import org.springframework.security.core.GrantedAuthority

class UserAuthority : GrantedAuthority {
    override fun getAuthority(): String {
        return Roles.ROLE_USER.role
    }
}