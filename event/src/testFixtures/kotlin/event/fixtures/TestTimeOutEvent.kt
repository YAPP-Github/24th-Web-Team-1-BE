package event.fixtures

import event.EventDetails
import event.EventUtils
import event.TimeExpiredEvent
import event.TimeOutEvent
import org.springframework.context.ApplicationEventPublisher

@EventDetails(outBox = false)
class TestTimeOutEvent(
    eventId: String = EventUtils.generateEventId(),
    eventType: String,
    eventTime: Long = System.currentTimeMillis(),
    expiredTime: Long,
    completed: Boolean = false,
    eventPublisher: ApplicationEventPublisher,
) : TimeOutEvent(
        eventId,
        eventType,
        eventTime,
        expiredTime,
        completed,
        eventPublisher,
    ) {
    override fun timeExpiredEvent(): TimeExpiredEvent = TestTimeExpiredEvent(eventId, eventType, eventTime)

    override fun getData(): Map<String, Any> =
        mapOf(
            "eventId" to eventId,
            "eventType" to eventType,
            "eventTime" to eventTime,
            "expiredTime" to expiredTime,
            "completed" to completed,
        )
}

@EventDetails(outBox = false)
class TestTimeExpiredEvent(
    eventId: String,
    eventType: String,
    eventTime: Long,
) : TimeExpiredEvent(
        eventId,
        eventType,
        eventTime,
    ) {
    override fun getData(): Map<String, Any> =
        mapOf(
            "eventId" to eventId,
            "eventType" to eventType,
            "eventTime" to eventTime,
        )
}