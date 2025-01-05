package com.few.crm.email.domain

import com.few.crm.support.jpa.converter.StringListConverter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

// TODO: templateId 와 version  를 기준으로 해당 도큐먼트는 유니크 해야함
@Entity
@Table(name = "email_template_histories")
@EntityListeners(AuditingEntityListener::class)
data class EmailTemplateHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "template_id")
    var templateId: Long,
    @Column(name = "subject")
    var subject: String,
    @Lob
    @Column(name = "body", columnDefinition = "BLOB")
    var body: String,
    @Convert(converter = StringListConverter::class)
    @Column(name = "variables")
    var variables: List<String>,
    @Column(name = "version")
    var version: Float,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
) {
    constructor() : this(
        templateId = 0,
        subject = "",
        body = "",
        variables = listOf(),
        version = 1.0f,
    )
}