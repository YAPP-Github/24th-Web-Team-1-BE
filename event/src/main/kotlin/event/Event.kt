package event

/**
 * Event
 *
 * 이벤트
 *
 * @property eventId 이벤트 식별자
 * @property eventType 이벤트 행위 타입
 * @property eventTime 이벤트 발행 시간 (기본값: 현재 시간)
 */
abstract class Event(
    protected val eventId: String = EventUtils.generateEventId(),
    protected val eventType: String,
    protected val eventTime: Long = System.currentTimeMillis(),
) {
    /**
     * Get data
     *
     * @return 이벤트 데이터
     */
    abstract fun getData(): Map<String, Any>
}