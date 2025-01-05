package com.few.crm.email.repository

import com.few.crm.email.domain.EmailTemplateHistory
import org.springframework.data.mongodb.repository.MongoRepository

interface EmailTemplateHistoryRepository : MongoRepository<EmailTemplateHistory, String> {
    fun findAllByTemplateIdInOrderByVersionDesc(templateIds: List<String>): List<EmailTemplateHistory>

    fun findByTemplateIdAndVersion(
        templateId: String,
        version: Float,
    ): EmailTemplateHistory?
}