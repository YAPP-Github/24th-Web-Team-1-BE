package com.few.crm.email.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "email_templates")
class EmailTemplate(
    @Id
    var id: String? = null,
    @Field("template_name")
    var templateName: String,
    @Field("subject")
    var subject: String,
    @Field("body")
    var body: String,
    @Field("variables")
    var variables: List<String> = emptyList(),
    @Field("version")
    var version: Float = 1.0f,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
) {
    companion object {
        fun new(
            templateName: String,
            subject: String,
            body: String,
            variables: List<String>,
        ): EmailTemplate =
            EmailTemplate(
                templateName = templateName,
                subject = subject,
                body = body,
                variables = variables,
            )
    }

    fun isNewTemplate(): Boolean = version == 1.0f

    fun modifySubject(subject: String?): EmailTemplate {
        subject?.let {
            this.subject = it
        }
        return this
    }

    fun modifyBody(
        body: String,
        variables: List<String>,
    ): EmailTemplate {
        this.body = body
        this.variables = variables
        return this
    }

    fun updateVersion(version: Float?): EmailTemplate {
        version?.let {
            if (it <= this.version) {
                throw IllegalArgumentException("Invalid version: $it")
            }
            this.version = it
        } ?: kotlin.run {
            this.version += 0.1f
        }
        return this
    }
}