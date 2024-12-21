package event.message

import event.Event
import java.util.Optional

/**
 * Message mapper
 *
 * 이벤트를 메시지로 변환하기 위한 매퍼
 *
 * @param T Event 클래스를 상속 받은 클래스
 * @param R Message 클래스를 상속 받은 클래스
 *
 * @see Event
 * @see Message
 */
abstract class MessageMapper<T : Event, R : Message> {
    /**
     * Map
     *
     * 이벤트를 메시지로 변환
     *
     * @param event 이벤트
     * @return Optional<Message> 메시지
     */
    abstract fun map(event: T): Optional<R>
}