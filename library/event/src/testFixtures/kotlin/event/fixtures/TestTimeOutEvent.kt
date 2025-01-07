package event.fixtures

import event.EventDetails
import event.EventUtils
import event.TimeExpiredEvent
import event.TimeOutEvent
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime

@EventDetails(outBox = false)
class TestTimeOutEvent(
    eventId: String = EventUtils.generateEventId(),
    eventType: String,
    eventTime: LocalDateTime = LocalDateTime.now(),
    expiredTime: LocalDateTime = LocalDateTime.now(),
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
    override fun timeExpiredEvent(): TimeExpiredEvent =
        TestTimeExpiredEvent(
            timeOutEventId = eventId,
            eventType = "ExpiredEvent",
        )
}

@EventDetails(outBox = false)
class TestTimeExpiredEvent(
    timeOutEventId: String,
    eventId: String = EventUtils.generateEventId(),
    eventType: String,
    eventTime: LocalDateTime = LocalDateTime.now(),
) : TimeExpiredEvent(
        timeOutEventId,
        eventId,
        eventType,
        eventTime,
    )