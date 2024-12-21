package event

import org.springframework.context.ApplicationEventPublisher

/**
 * Time out event
 *
 * 시간이 설정된 이벤트
 *
 * @param expiredTime 만료 시간
 * @param completed 완료 여부(기본값: false)
 * @param eventPublisher 이벤트 발행자
 *
 * @see TimeExpiredEvent 시간 만료 이벤트
 */
abstract class TimeOutEvent(
    eventId: String = EventUtils.generateEventId(),
    eventType: String,
    eventTime: Long = System.currentTimeMillis(),
    protected val expiredTime: Long,
    protected var completed: Boolean = false,
    protected val eventPublisher: ApplicationEventPublisher,
) : Event(
        eventId,
        eventType,
        eventTime,
    ),
    Runnable {
    /**
     * Complete event
     *
     * 이벤트 완료 처리
     */
    fun complete() {
        completed = true
    }

    /**
     * Is expired
     *
     * @param time  시간 (기본값: 현재 시간)
     * @return 이벤트 만료 여부
     */
    fun isExpired(time: Long = System.currentTimeMillis()): Boolean = !completed && time > expiredTime

    /**
     * Run
     *
     *  이벤트 만료시 실행
     */
    override fun run() {
        publishExpiredTimeEvent()
    }

    /**
     * Publish expired time event
     *
     * 시간 만료 이벤트 발행
     */
    private fun publishExpiredTimeEvent() {
        eventPublisher.publishEvent(timeExpiredEvent())
    }

    /**
     * Expired time event
     *
     * 시간 만료 이벤트
     */
    abstract fun timeExpiredEvent(): TimeExpiredEvent
}

/**
 * Expired time event
 */
abstract class TimeExpiredEvent(
    eventId: String,
    eventType: String,
    eventTime: Long,
) : Event(
        eventId,
        eventType,
        eventTime,
    )