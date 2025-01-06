package com.few.crm.email.repository

import com.few.crm.email.domain.EmailTemplateHistory
import org.springframework.data.jpa.repository.JpaRepository

interface EmailTemplateHistoryRepository : JpaRepository<EmailTemplateHistory, Long> {
    fun findAllByTemplateId(templateId: Long): List<EmailTemplateHistory>

    fun findAllByTemplateIdInOrderByVersionDesc(templateIds: List<Long>): List<EmailTemplateHistory>

    fun findByTemplateIdAndVersion(
        templateId: Long,
        version: Float,
    ): EmailTemplateHistory?
}