package security

enum class TokenClaim(
    val key: String,
) {
    MEMBER_ID_CLAIM("memberId"),
    MEMBER_EMAIL_CLAIM("memberEmail"),
    MEMBER_ROLE_CLAIM("memberRole"),
}