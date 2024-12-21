package event

/**
 * Event handler
 *
 * 이벤트 핸들러
 *
 * @param T Event 클래스를 상속 받은 클래스
 * @see Event
 */
interface EventHandler<T : Event> {
    /**
     * Handle event
     *
     * @param event 이벤트
     */
    fun handle(event: T)
}