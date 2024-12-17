package com.few.api.domain.batch.log

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import java.util.*

@Aspect
@Component
class ApiBatchServiceLogAspect {
    private val log = KotlinLogging.logger {}

    @Pointcut(value = "execution(* com.few.api.domain.batch..*.execute(..))")
    fun batchServiceDao() {}

    @Around("batchServiceDao()")
    @Throws(Throwable::class)
    fun requestLogging(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature
        val splitByDot =
            signature.declaringTypeName
                .split(regex = "\\.".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val serviceName = splitByDot[splitByDot.size - 1]
        val args = joinPoint.args

        log.trace { "$serviceName execute with $args" }
        val startTime = System.currentTimeMillis()
        val proceed = joinPoint.proceed()
        val elapsedTime = System.currentTimeMillis() - startTime
        log.debug { "$serviceName finished in ${elapsedTime}ms" }

        return proceed
    }
}