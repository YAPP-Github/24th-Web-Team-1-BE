package security.exception

import org.springframework.security.core.AuthenticationException

class SecurityAccessTokenInvalidException(
    msg: String?,
) : AuthenticationException(msg)