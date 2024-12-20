package event.message

import event.Event

/**
 * Message reverse relay
 *
 * 외부 시스템에서 전달 받은 메시지를 내부에 이벤트로 전달하기 위한 메시지 리버스 릴레이
 *
 * `Bar 메시지를 수신하여 Foo 이벤트로 변환하여 내부로 전달하는 예시`
 *
 * ```kotlin
 * class FooBarLocalMessageReverseRelay(
 *      private val messageSender: MessageSender<BarMessage>,
 *      private val messageMapper: MessageMapper<FooEvent, BarMessage>,
 *      private val eventPublisher: ApplicationEventPublisher,
*   ) : MessageReverseRelay<FooEvent> {
 *
 *    override fun publish(event: FooEvent) {
 *      applicationEventPublisher.publishEvent(event)
 *    }
 *
 *    @EventListener
 *    @LocalSubscribeMessage(topic ="bar")
 *    fun onApplicationEvent(message: BarMessage) {
 *      messageMapper.map(message).ifPresent { publish(it) }
 *    }
 * }
 * ```
 * @param T  Event 클래스를 상속 받은 클래스
 *
 * @see MessageSender 메시지를 전달하기 위한 메시지 발신자
 * @see MessageMapper 메시지를 이벤트로 변환하기 위한 메시지 매퍼
 * @see event.message.local.LocalSubscribeMessage 내부 메시지 수신을 위한 어노테이션
 *
 * @see org.springframework.context.event.EventListener 이벤트 리스너 (기본 설정 메서드 명: onApplicationEvent)
 * @see org.springframework.context.ApplicationEventPublisher
 */
interface MessageReverseRelay<T : Event> {
    /**
     * Publish
     *
     * @param event 내부로 전달할 이벤트
     */
    fun publish(event: T)
}