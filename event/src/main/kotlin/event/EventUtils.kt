package event

import java.util.*

/**
 * Is out box extension for Event Class
 *
 * 이벤트 외부 발행 여부를 확인합니다.
 *
 * @return 이벤트 외부 발행 여부
 * @see Event
 */
fun Event.isOutBox(): Boolean = this::class.annotations.any { annotation -> annotation is EventDetails && annotation.outBox }

class EventUtils {
    companion object {
        /**
         * Generate event id
         *
         * 이벤트 ID를 생성합니다.
         *
         * @return 이벤트 ID
         */
        fun generateEventId(): String = UUID.randomUUID().toString()
    }
}