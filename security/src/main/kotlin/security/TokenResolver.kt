package security

import io.jsonwebtoken.Claims

interface TokenResolver {
    fun resolve(token: String?): Claims?
    fun resolveId(token: String?): Long?
    fun resolveEmail(token: String?): String?
    fun resolveRole(token: String?): String?
}