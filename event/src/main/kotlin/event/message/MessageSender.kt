package event.message

/**
 * Message sender
 *
 * 외부 시스템으로 메시지를 전달하기 위한 메시지 발신자
 *
 * @param T Message 클래스를 상속 받은 클래스
 */
interface MessageSender<T : Message> {
    /**
     * Send
     *
     * @param message 전달할 메시지
     */
    fun send(message: T)
}