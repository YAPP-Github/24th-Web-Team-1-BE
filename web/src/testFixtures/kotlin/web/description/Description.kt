package web.description

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

    fun fields(vararg fields: FieldDescriptor): Array<FieldDescriptor> {
        return describe(fields.toList().toTypedArray())
    }

    fun describe(): Array<FieldDescriptor> {
        return arrayOf(
            messageDescriptor
        )
    }

    fun authHeader(optional: Boolean = false): HeaderDescriptorWithType {
        if (optional) {
            return headerWithName("Authorization")
                .optional()
                .defaultValue("{{accessToken}}")
                .description("Bearer 어세스 토큰")
        }
        return headerWithName("Authorization")
            .defaultValue("{{accessToken}}")
            .description("Bearer 어세스 토큰")
    }
}