package com.few.crm.email.domain

import com.few.crm.support.jpa.converter.StringListConverter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "email_templates")
@EntityListeners(AuditingEntityListener::class)
data class EmailTemplate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "template_name")
    var templateName: String,
    @Column(name = "subject")
    var subject: String,
    @Lob
    @Column(name = "body", columnDefinition = "BLOB")
    var body: String,
    @Convert(converter = StringListConverter::class)
    @Column(name = "variables")
    var variables: List<String> = listOf(),
    @Column(name = "version")
    var version: Float = 1.0f,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
) {
    protected constructor() : this(
        templateName = "",
        subject = "",
        body = "",
        variables = emptyList(),
    )

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

    fun isNewTemplate(): Boolean = id == null

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