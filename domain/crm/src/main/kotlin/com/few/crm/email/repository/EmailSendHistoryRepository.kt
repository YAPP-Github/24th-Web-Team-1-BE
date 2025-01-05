package com.few.crm.email.repository

import com.few.crm.email.domain.EmailSendHistory
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailSendHistoryRepository : MongoRepository<EmailSendHistory, String>