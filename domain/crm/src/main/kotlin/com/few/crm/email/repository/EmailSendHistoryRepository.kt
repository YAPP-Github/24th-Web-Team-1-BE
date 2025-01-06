package com.few.crm.email.repository

import com.few.crm.email.domain.EmailSendHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailSendHistoryRepository : JpaRepository<EmailSendHistory, Long> {
    fun findByUserEmailContainingIgnoreCase(email: String): List<EmailSendHistory>

    fun findByEmailMessageId(emailMessageId: String): EmailSendHistory?
}