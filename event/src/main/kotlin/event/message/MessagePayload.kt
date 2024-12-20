package event.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Message payload
 *
 *  메시지 페이로드
 *
 * @property eventId 이벤트 식별자
 * @property eventType 이벤트 행위 타입
 * @property eventTime 이벤트 발행 시간
 * @property data 이벤트 데이터
 *
 * @see event.Event
 */

data class MessagePayload
    @JsonCreator
    constructor(
        @JsonProperty("eventId") val eventId: String?,
        @JsonProperty("eventType") val eventType: String?,
        @JsonProperty("eventTime") val eventTime: Long?,
        @JsonProperty("data") val data: Map<String, Any>?,
    )