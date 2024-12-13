package security

interface TokenGenerator {
    fun generateAuthToken(memberId: Long?, memberEmail: String?, memberRoles: List<Roles>): AuthToken
    fun generateAuthToken(
        memberId: Long?,
        memberEmail: String?,
        memberRoles: List<Roles>,
        accessTokenValidTime: Long?,
        refreshTokenValidTime: Long?,
    ): AuthToken

    fun generateAccessToken(
        memberId: Long?,
        memberEmail: String?,
        memberRoles: List<Roles>,
        accessTokenValidTime: Long?,
    ): String

    fun generateRefreshToken(
        memberId: Long?,
        memberEmail: String?,
        memberRoles: List<Roles>,
        refreshTokenValidTime: Long?,
    ): String

    fun convertToStringList(memberRoles: List<Roles>): List<String>
}