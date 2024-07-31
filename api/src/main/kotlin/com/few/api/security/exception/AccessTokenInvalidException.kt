package com.few.api.security.exception

import org.springframework.security.core.AuthenticationException

class AccessTokenInvalidException(msg: String?) : AuthenticationException(msg)