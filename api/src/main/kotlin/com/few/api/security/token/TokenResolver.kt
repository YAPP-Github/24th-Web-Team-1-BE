package com.few.api.security.token

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenResolver(
    @Value("\${security.jwt.token.secretkey}") private val secretKey: String,
) {

    companion object {
        private const val MEMBER_ID_CLAIM_KEY = "memberId"
        private const val MEMBER_ROLE_CLAIM_KEY = "memberRole"
    }

    private val log = KotlinLogging.logger {}

    fun resolve(token: String?): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.toByteArray())
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            log.warn { "${"Failed to get memberId. token: {}"} $token" }
            null
        }
    }

    fun resolveId(token: String?): Long? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.toByteArray())
                .build()
                .parseClaimsJws(token)
                .body
                .get(MEMBER_ID_CLAIM_KEY, Long::class.java)
        } catch (e: Exception) {
            log.warn { "${"Failed to get memberId. token: {}"} $token" }
            return null
        }
    }

    fun resolveRole(token: String?): String? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.toByteArray())
                .build()
                .parseClaimsJws(token)
                .body
                .get(MEMBER_ROLE_CLAIM_KEY, String::class.java)
        } catch (e: Exception) {
            log.warn { "${"Failed to get memberId. token: {}"} $token" }
            return null
        }
    }
}