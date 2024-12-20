package event

/**
 * Event RePlayer
 *
 * 이벤트 재생기
 */
abstract class EventRePlayer {
    /**
     * Replay
     *
     *  정상 처리 되지 않은 이벤트를 재생합니다.
     */
    abstract fun replay()
}