package com.few.api.security.authentication.authority

import org.apache.commons.lang3.StringUtils
import org.springframework.security.core.GrantedAuthority

object AuthorityUtils {

    @Throws(IllegalArgumentException::class)
    fun toAuthorities(roles: String): List<GrantedAuthority> {
        val tokens = StringUtils.splitPreserveAllTokens(roles, "[,]")
        val rtn: MutableList<GrantedAuthority> = ArrayList()
        for (token in tokens) {
            if (token != "") {
                val role = token.trim { it <= ' ' }
                rtn.add(Roles.valueOf(role).authority)
            }
        }
        return rtn
    }
}