package event.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Message
 *
 * 외부 시스템과의 통신을 위한 메시지
 *
 * @property payload 메시지 페이로드
 */
abstract class Message
    @JsonCreator
    constructor(
        @JsonProperty("payload") var payload: MessagePayload?,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Message

            if (payload != other.payload) return false

            return true
        }

        override fun hashCode(): Int = payload?.hashCode() ?: 0
    }