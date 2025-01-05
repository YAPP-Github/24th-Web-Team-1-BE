package com.few.crm.email.controller

import com.few.crm.email.controller.request.PostTemplateRequest
import com.few.crm.email.controller.request.SendNotificationEmailRequest
import com.few.crm.email.usecase.BrowseTemplateUseCase
import com.few.crm.email.usecase.PostTemplateUseCase
import com.few.crm.email.usecase.SendNotificationEmailUseCase
import com.few.crm.email.usecase.dto.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import web.ApiResponse
import web.ApiResponseGenerator

@Validated
@RestController
@RequestMapping(value = ["/api/v2/crm/email"])
class CrmEmailController(
    @Value("\${crm.auth}") private val auth: String,
    private val browseTemplateUseCase: BrowseTemplateUseCase,
    private val postTemplateUseCase: PostTemplateUseCase,
    private val sendNotificationEmailUseCase: SendNotificationEmailUseCase,
) {
    @GetMapping(value = ["/templates"])
    fun browseEmailTemplates(
        @RequestParam(required = false) history: Boolean?,
        @RequestParam(value = "auth") auth: String,
    ): ApiResponse<ApiResponse.SuccessBody<BrowseTemplateUseCaseOut>> {
        if (this.auth != auth) {
            throw IllegalAccessException("Invalid Permission")
        }
        browseTemplateUseCase
            .execute(
                BrowseTemplateUseCaseIn(
                    withHistory = history ?: true,
                ),
            ).let {
                return ApiResponseGenerator.success(it, HttpStatus.OK)
            }
    }

    @PostMapping(value = ["/templates"])
    fun postEmailTemplate(
        @RequestBody request: PostTemplateRequest,
        @RequestParam(value = "auth") auth: String,
    ): ApiResponse<ApiResponse.SuccessBody<PostTemplateUseCaseOut>> {
        if (this.auth != auth) {
            throw IllegalAccessException("Invalid Permission")
        }
        postTemplateUseCase
            .execute(
                PostTemplateUseCaseIn(
                    id = request.id,
                    templateName = request.templateName,
                    subject = request.subject,
                    version = request.version,
                    body = request.body,
                    variables = request.variables ?: emptyList(),
                ),
            ).let {
                return ApiResponseGenerator.success(it, HttpStatus.OK)
            }
    }

    @PostMapping(value = ["/send/notification"])
    fun sendNotificationEmail(
        @RequestBody request: SendNotificationEmailRequest,
        @RequestParam(value = "auth") auth: String,
    ): ApiResponse<ApiResponse.SuccessBody<SendNotificationEmailUseCaseOut>> {
        if (this.auth != auth) {
            throw IllegalAccessException("Invalid Permission")
        }
        sendNotificationEmailUseCase
            .execute(
                SendNotificationEmailUseCaseIn(
                    templateId = request.templateId,
                    templateVersion = request.templateVersion,
                    userIds = request.userIds ?: emptyList(),
                ),
            ).let {
                return ApiResponseGenerator.success(it, HttpStatus.OK)
            }
    }
}