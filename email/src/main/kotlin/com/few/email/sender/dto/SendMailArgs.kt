package com.few.email.sender.dto

abstract class SendMailArgs<C, P>(
    val to: String,
    val subject: String,
    val template: String,
    private val content: C,
    private val properties: P
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SendMailArgs<*, *>

        if (to != other.to) return false
        if (subject != other.subject) return false
        if (template != other.template) return false
        if (content != other.content) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = to.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + template.hashCode()
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (properties?.hashCode() ?: 0)
        return result
    }
}