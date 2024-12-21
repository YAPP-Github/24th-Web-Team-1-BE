package event.fixtures

import event.Event
import event.EventDetails
import event.EventUtils

@EventDetails(outBox = true)
class TestEvent(
    eventId: String = EventUtils.generateEventId(),
    eventType: String,
    eventTime: Long = System.currentTimeMillis(),
) : Event(
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