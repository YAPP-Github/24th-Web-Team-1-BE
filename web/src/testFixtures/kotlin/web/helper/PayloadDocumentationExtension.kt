package web.helper

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

fun FieldDescriptor.fieldWithObject(description: String): FieldDescriptor {
    return this.type(JsonFieldType.OBJECT).description(description)
}

fun FieldDescriptor.fieldWithArray(description: String): FieldDescriptor {
    return this.type(JsonFieldType.ARRAY).description(description)
}

fun FieldDescriptor.fieldWithString(description: String): FieldDescriptor {
    return this.type(JsonFieldType.STRING).description(description)
}

fun FieldDescriptor.fieldWithNumber(description: String): FieldDescriptor {
    return this.type(JsonFieldType.NUMBER).description(description)
}

fun FieldDescriptor.fieldWithBoolean(description: String): FieldDescriptor {
    return this.type(JsonFieldType.BOOLEAN).description(description)
}

fun FieldDescriptor.fieldWithNull(description: String): FieldDescriptor {
    return this.type(JsonFieldType.NULL).description(description)
}

fun FieldDescription.asObject(): FieldDescriptor {
    return PayloadDocumentation.fieldWithPath(this.path).fieldWithObject(this.description)
}

fun FieldDescription.asArray(): FieldDescriptor {
    return PayloadDocumentation.fieldWithPath(this.path).fieldWithArray(this.description)
}

fun FieldDescription.asString(): FieldDescriptor {
    return PayloadDocumentation.fieldWithPath(this.path).fieldWithString(this.description)
}

fun FieldDescription.asNumber(): FieldDescriptor {
    return PayloadDocumentation.fieldWithPath(this.path).fieldWithNumber(this.description)
}

fun FieldDescription.asBoolean(): FieldDescriptor {
    return PayloadDocumentation.fieldWithPath(this.path).fieldWithBoolean(this.description)
}

fun FieldDescription.asNull(): FieldDescriptor {
    return PayloadDocumentation.fieldWithPath(this.path).fieldWithNull(this.description)
}
class PayloadDocumentationExtension