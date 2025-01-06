package com.few.crm.user.repository

import com.few.crm.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @Query("SELECT u FROM users u WHERE u.userAttributes LIKE %:key%")
    fun findAllExistByUserAttributesKey(
        @Param("key") key: String? = "email",
    ): List<User>

    fun findAllByIdIn(ids: List<Long>): List<User>

    fun findByExternalId(externalId: String): User?
}