package event.message

/**
 * Message relay
 *
 * 내부 이벤트를 외부에 메시지로 전달하기 위한 메시지 릴레이
 *
 * @param T Message 클래스를 상속 받은 클래스
 * @see MessageSender 메시지를 전달하기 위한 메시지 발신자
 */
interface MessageRelay<T : Message> {
    /**
     * Publish
     *
     * @param message 외부로 전달할 메시지
     */
    fun publish(message: T)
}