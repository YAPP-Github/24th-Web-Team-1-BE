package com.few.api.domain.batch.article.dto

fun List<WorkBookSubscriberItem>.toMemberIds(): Set<Long> {
    return this.map { it.memberId }.toSet()
}

fun List<WorkBookSubscriberItem>.toTargetWorkBookIds(): Set<Long> {
    return this.map { it.targetWorkBookId }.toSet()
}

/** key: 구독자들이 구독한 학습지 ID, value: 구독자들의 학습지 구독 진행률 */
fun List<WorkBookSubscriberItem>.toTargetWorkBookProgress(): Map<Long, List<Long>> {
    return this.stream().collect(
        { mutableMapOf<Long, MutableList<Long>>() },
        { map, dto ->
            if (map.containsKey(dto.targetWorkBookId)) {
                map[dto.targetWorkBookId]?.add(dto.progress)
            } else {
                map[dto.targetWorkBookId] = mutableListOf(dto.progress)
            }
        },
        { map1, map2 ->
            map2.forEach { (key, value) ->
                if (map1.containsKey(key)) {
                    map1[key]?.addAll(value)
                } else {
                    map1[key] = value
                }
            }
        }
    )
}

data class WorkBookSubscriberItem(
    /** 회원 ID */
    val memberId: Long,
    /** 학습지 ID */
    val targetWorkBookId: Long,
    /** 진행률 */
    val progress: Long,
)