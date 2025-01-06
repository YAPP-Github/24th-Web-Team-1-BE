package com.few.crm.user.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity(name = "users")
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "external_id")
    var externalId: String? = "",
    @Lob
    @Column(name = "user_attributes", columnDefinition = "BLOB")
    var userAttributes: String,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) {
    constructor() : this(
        externalId = "",
        userAttributes = "",
    )

    fun updateAttributes(attributes: String) {
        userAttributes = attributes
    }
}