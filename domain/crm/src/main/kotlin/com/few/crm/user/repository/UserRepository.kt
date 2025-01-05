package com.few.crm.user.repository

import com.few.crm.user.domain.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    @Query("{ 'user_attributes': { \$regex: '\"email\"' } }")
    fun findAllExistByUserAttributesEmail(): List<User>

    fun findAllByIdIn(ids: List<String>): List<User>
}