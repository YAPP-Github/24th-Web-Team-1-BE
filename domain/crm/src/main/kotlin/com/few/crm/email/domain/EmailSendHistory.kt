package com.few.crm.email.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "email_send_histories")
@EntityListeners(AuditingEntityListener::class)
data class EmailSendHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "user_external_id")
    var userExternalId: String? = "",
    @Column(name = "user_email")
    var userEmail: String? = "",
    @Column(name = "email_message_id")
    var emailMessageId: String? = "",
    @Column(name = "email_body")
    var emailBody: String? = "",
    @Column(name = "send_status")
    var sendStatus: String? = "",
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
)