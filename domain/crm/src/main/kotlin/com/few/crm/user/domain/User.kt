package com.few.crm.user.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "users")
class User(
    @Id
    var id: String? = null,
    @Field("external_id")
    var externalId: String? = "",
    @Field("user_attributes")
    var userAttributes: String,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
)