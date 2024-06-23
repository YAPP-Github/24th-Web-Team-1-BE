package com.few.batch.log

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Aspect
@Component
class BatchServiceLogAspect {
    private val log = LoggerFactory.getLogger(BatchServiceLogAspect::class.java)

    @Pointcut(value = "execution(* com.few.batch.service..*.execute(..))")
    fun batchServiceDao() {}

    @Around("batchServiceDao()")
    @Throws(Throwable::class)
    fun requestLogging(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature
        val splitByDot =
            signature.declaringTypeName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val serviceName = splitByDot[splitByDot.size - 1]
        val args = joinPoint.args

        log.trace("{} execute with {}", serviceName, args)
        val startTime = System.currentTimeMillis()
        val proceed = joinPoint.proceed()
        val elapsedTime = System.currentTimeMillis() - startTime
        log.debug("{} finished in {}ms", serviceName, elapsedTime)

        return proceed
    }
}