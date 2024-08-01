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
        private const val MEMBER_EMAIL_CLAIM_KEY = "memberEmail"
        private const val MEMBER_ROLE_CLAIM_KEY = "memberRole"
    }
    fun generateAuthToken(memberId: Long?, memberEmail: String?, memberRoles: List<Roles>): AuthToken {
        return AuthToken(
            generateAccessToken(
                memberId,
                memberEmail,
                memberRoles,
                accessTokenValidTime
            ),
            generateRefreshToken(memberId, memberEmail, memberRoles, refreshTokenValidTime)
        )
    }

    fun generateAuthToken(
        memberId: Long?,
        memberEmail: String?,
        memberRoles: List<Roles>,
        accessTokenValidTime: Long?,
        refreshTokenValidTime: Long?,
    ): AuthToken {
        return AuthToken(
            generateAccessToken(memberId, memberEmail, memberRoles, accessTokenValidTime),
            generateRefreshToken(memberId, memberEmail, memberRoles, refreshTokenValidTime)
        )
    }

    fun generateAccessToken(
        memberId: Long?,
        memberEmail: String?,
        memberRoles: List<Roles>,
        accessTokenValidTime: Long?,
    ): String {
        val now = Date()
        val roles = convertToStringList(memberRoles)
        val acValidTime = accessTokenValidTime ?: this.accessTokenValidTime
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .claim(MEMBER_ID_CLAIM_KEY, memberId)
            .claim(MEMBER_EMAIL_CLAIM_KEY, memberEmail)
            .claim(MEMBER_ROLE_CLAIM_KEY, roles.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + acValidTime))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    fun generateRefreshToken(
        memberId: Long?,
        memberEmail: String?,
        memberRoles: List<Roles>,
        refreshTokenValidTime: Long?,
    ): String {
        val now = Date()
        val roles = convertToStringList(memberRoles)
        val rfValidTime = refreshTokenValidTime ?: this.refreshTokenValidTime
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .claim(MEMBER_ID_CLAIM_KEY, memberId)
            .claim(MEMBER_EMAIL_CLAIM_KEY, memberEmail)
            .claim(MEMBER_ROLE_CLAIM_KEY, roles.toString())
            .setIssuedAt(now)
            .setExpiration(Date(now.time + rfValidTime))
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