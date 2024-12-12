package web.helper

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType

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
class PayloadDocumentationExtension