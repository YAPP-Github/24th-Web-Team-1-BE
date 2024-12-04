package security

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TokenResolver(
    @Value("\${security.jwt.token.secretkey}") private val secretKey: String,
) {
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
                .get(TokenClaim.MEMBER_ID_CLAIM.key, Integer::class.java)
                .toLong()
        } catch (e: Exception) {
            log.warn { "${"Failed to get memberId. token: {}"} $token" }
            return null
        }
    }

    fun resolveEmail(token: String?): String? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.toByteArray())
                .build()
                .parseClaimsJws(token)
                .body
                .get(TokenClaim.MEMBER_EMAIL_CLAIM.key, String::class.java)
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
                .get(TokenClaim.MEMBER_ROLE_CLAIM.key, String::class.java)
        } catch (e: Exception) {
            log.warn { "${"Failed to get memberId. token: {}"} $token" }
            return null
        }
    }
}