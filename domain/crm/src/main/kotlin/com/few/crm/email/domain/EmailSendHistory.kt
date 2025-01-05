package com.few.crm.email.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "email_send_history")
class EmailSendHistory(
    @Id
    var id: String? = null,
    @Field("user_external_id")
    var userExternalId: String? = "",
    @Field("user_email")
    var userEmail: String? = "",
    @Field("email_message_id")
    var emailMessageId: String? = "",
    @Field("email_body")
    var emailBody: String? = "",
    @Field("send_status")
    var sendStatus: String? = "",
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
)