package com.few.api.security.token

import com.few.api.security.authentication.authority.Roles
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenGenerator(
    @Value("\${security.jwt.token.secretkey}") private val secretKey: String,
    @Value("\${security.jwt.token.validtime.access}") private val accessTokenValidTime: Long,
    @Value("\${security.jwt.token.validtime.refresh}") private val refreshTokenValidTime: Long,
) {

    companion object {
        private const val MEMBER_ID_CLAIM_KEY = "memberId"
        private const val MEMBER_ROLE_CLAIM_KEY = "memberRole"
    }

    fun generateAuthToken(memberId: Long?, memberRoles: List<Roles>): AuthToken {
        return AuthToken(generateAccessToken(memberId, memberRoles), generateRefreshToken(memberId, memberRoles))
    }

    fun generateAccessToken(memberId: Long?, memberRoles: List<Roles>): String {
        val now = Date()
        val roles = convertToStringList(memberRoles)
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .claim(MEMBER_ID_CLAIM_KEY, memberId)
            .claim(MEMBER_ROLE_CLAIM_KEY, roles.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + accessTokenValidTime))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    fun generateRefreshToken(memberId: Long?, memberRoles: List<Roles>): String {
        val now = Date()
        val roles = convertToStringList(memberRoles)
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .claim(MEMBER_ID_CLAIM_KEY, memberId)
            .claim(MEMBER_ROLE_CLAIM_KEY, roles.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshTokenValidTime))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    private fun convertToStringList(memberRoles: List<Roles>): List<String> {
        val stringRoles: MutableList<String> = ArrayList()
        for (role in memberRoles) {
            stringRoles.add(role.role)
        }
        return stringRoles
    }
}