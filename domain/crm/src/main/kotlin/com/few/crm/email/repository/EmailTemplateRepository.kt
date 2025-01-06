package com.few.crm.email.repository

import com.few.crm.email.domain.EmailTemplate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailTemplateRepository : JpaRepository<EmailTemplate, Long> {
    fun findByTemplateNameContainingIgnoreCase(templateName: String): List<EmailTemplate>

    fun findByTemplateName(templateName: String): EmailTemplate?
}