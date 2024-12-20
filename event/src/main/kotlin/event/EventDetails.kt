package event

/**
 * Event details
 *
 * 이벤트 상세 정보
 *
 * @property outBox 이벤트 외부 발행 여부
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventDetails(
    val outBox: Boolean = false,
)