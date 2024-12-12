package com.few.api.domain.common.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiLockFor(
    val identifier: ApiLockIdentifier,
)