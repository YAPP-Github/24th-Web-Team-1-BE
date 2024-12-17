package web

enum class MessageCode(
    val code: String,
    val value: String,
) {
    SUCCESS("success", "성공"),
    RESOURCE_DELETED("resource.deleted", "삭제되었습니다."),
    RESOURCE_UPDATED("resource.updated", "수정되었습니다."),
    RESOURCE_CREATED("resource.created", "새로 생성되었습니다."),
}