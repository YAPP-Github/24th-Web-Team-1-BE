package security

import org.springframework.security.core.GrantedAuthority

enum class Roles(
    val role: String,
) {
    ROLE_USER("ROLE_USER") {
        override val authority: GrantedAuthority
            get() = UserAuthority()
    }, ;

    abstract val authority: GrantedAuthority
}