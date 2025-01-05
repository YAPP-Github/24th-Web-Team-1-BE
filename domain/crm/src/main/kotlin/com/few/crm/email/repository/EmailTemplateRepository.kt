package com.few.crm.email.repository

import com.few.crm.email.domain.EmailTemplate
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailTemplateRepository : MongoRepository<EmailTemplate, String> {
    fun findByTemplateName(templateName: String): EmailTemplate?
}