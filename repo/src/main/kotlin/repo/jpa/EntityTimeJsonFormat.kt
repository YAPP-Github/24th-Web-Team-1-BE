package repo.jpa

import com.fasterxml.jackson.annotation.JsonFormat

@MustBeDocumented
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EntityTimeJsonFormat.TIME_FORMAT, timezone = EntityTimeJsonFormat.TIME_ZONE)
annotation class EntityTimeJsonFormat {
    companion object {
        const val TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val TIME_ZONE = "Asia/Seoul"
    }
}