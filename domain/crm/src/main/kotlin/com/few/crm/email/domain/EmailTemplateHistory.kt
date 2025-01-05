package com.few.crm.email.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

// TODO: templateId 와 version  를 기준으로 해당 도큐먼트는 유니크 해야함
@Document(collection = "email_template_histories")
class EmailTemplateHistory(
    @Id
    var id: String? = null,
    @Field("template_id")
    var templateId: String,
    @Field("subject")
    var subject: String,
    @Field("body")
    var body: String,
    @Field("variables")
    var variables: List<String>,
    @Field("version")
    var version: Float,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
)