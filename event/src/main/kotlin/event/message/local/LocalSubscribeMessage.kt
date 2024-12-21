package event.message.local

/**
 * Local subscribe message
 *
 * 로컬 메시지 구독 정보
 *
 * @property topic 메시지 토픽
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalSubscribeMessage(
    val topic: String = "",
)