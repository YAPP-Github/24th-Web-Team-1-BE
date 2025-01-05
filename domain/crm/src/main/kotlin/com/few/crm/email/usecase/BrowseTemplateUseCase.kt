package com.few.crm.email.usecase

import com.few.crm.email.repository.EmailTemplateHistoryRepository
import com.few.crm.email.repository.EmailTemplateRepository
import com.few.crm.email.usecase.dto.*
import org.springframework.stereotype.Service

@Service
class BrowseTemplateUseCase(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailTemplateHistoryRepository: EmailTemplateHistoryRepository,
) {
    fun execute(useCaseIn: BrowseTemplateUseCaseIn): BrowseTemplateUseCaseOut {
        val withHistory = useCaseIn.withHistory

        val templates = emailTemplateRepository.findAll()
        val histories =
            if (withHistory) {
                templates
                    .map { it.id!! }
                    .let { emailTemplateHistoryRepository.findAllByTemplateIdInOrderByVersionDesc(it) }
                    .groupBy { it.templateId }
            } else {
                null
            }

        templates
            .map { template ->
                val history = histories?.get(template.id)
                template to history
            }.map {
                val template = it.first
                val templateHistories = it.second
                TemplateResult(
                    template =
                        TemplateCurrent(
                            id = template.id!!,
                            templateName = template.templateName,
                            subject = template.subject,
                            body = template.body,
                            variables = template.variables,
                            version = template.version,
                            createdAt = template.createdAt.toString(),
                        ),
                    histories =
                        templateHistories?.map { history ->
                            TemplateHistory(
                                id = history.id!!,
                                templateId = history.templateId,
                                subject = history.subject,
                                body = history.body,
                                variables = history.variables,
                                version = history.version,
                                createdAt = history.createdAt.toString(),
                            )
                        } ?: emptyList(),
                )
            }.let {
                return BrowseTemplateUseCaseOut(it)
            }
    }
}