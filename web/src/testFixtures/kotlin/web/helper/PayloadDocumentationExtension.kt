package web.helper

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

fun FieldDescriptor.fieldWithObject(description: String): FieldDescriptor = this.type(JsonFieldType.OBJECT).description(description)

fun FieldDescriptor.fieldWithArray(description: String): FieldDescriptor = this.type(JsonFieldType.ARRAY).description(description)

fun FieldDescriptor.fieldWithString(description: String): FieldDescriptor = this.type(JsonFieldType.STRING).description(description)

fun FieldDescriptor.fieldWithNumber(description: String): FieldDescriptor = this.type(JsonFieldType.NUMBER).description(description)

fun FieldDescriptor.fieldWithBoolean(description: String): FieldDescriptor = this.type(JsonFieldType.BOOLEAN).description(description)

fun FieldDescriptor.fieldWithNull(description: String): FieldDescriptor = this.type(JsonFieldType.NULL).description(description)

fun FieldDescription.asObject(): FieldDescriptor {
    val descriptor = PayloadDocumentation.fieldWithPath(this.path).fieldWithObject(this.description)
    if (this.optional) {
        return descriptor.optional()
    }
    return descriptor
}

fun FieldDescription.asArray(): FieldDescriptor {
    val descriptor = PayloadDocumentation.fieldWithPath(this.path).fieldWithArray(this.description)
    if (this.optional) {
        return descriptor.optional()
    }
    return descriptor
}

fun FieldDescription.asString(): FieldDescriptor {
    val descriptor = PayloadDocumentation.fieldWithPath(this.path).fieldWithString(this.description)
    if (this.optional) {
        return descriptor.optional()
    }
    return descriptor
}

fun FieldDescription.asNumber(): FieldDescriptor {
    val descriptor = PayloadDocumentation.fieldWithPath(this.path).fieldWithNumber(this.description)
    if (this.optional) {
        return descriptor.optional()
    }
    return descriptor
}

fun FieldDescription.asBoolean(): FieldDescriptor {
    val descriptor = PayloadDocumentation.fieldWithPath(this.path).fieldWithBoolean(this.description)
    if (this.optional) {
        return descriptor.optional()
    }
    return descriptor
}

fun FieldDescription.asNull(): FieldDescriptor {
    val descriptor = PayloadDocumentation.fieldWithPath(this.path).fieldWithNull(this.description)
    if (this.optional) {
        return descriptor.optional()
    }
    return descriptor
}

class PayloadDocumentationExtension