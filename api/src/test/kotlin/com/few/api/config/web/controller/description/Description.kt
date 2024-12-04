package com.few.api.config.web.controller.description

import com.epages.restdocs.apispec.HeaderDescriptorWithType
import com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import org.apache.commons.lang3.ArrayUtils
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

object Description {
    private val messageDescriptor: FieldDescriptor
        private get() = PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
            .description("메시지")

    fun describe(data: Array<FieldDescriptor>?): Array<FieldDescriptor> {
        return ArrayUtils.addAll(
            data,
            messageDescriptor
        )
    }

    fun describe(): Array<FieldDescriptor> {
        return arrayOf(
            messageDescriptor
        )
    }
    fun authHeader(): HeaderDescriptorWithType {
        return headerWithName("Authorization")
            .defaultValue("{{accessToken}}")
            .description("Bearer 어세스 토큰")
    }
}