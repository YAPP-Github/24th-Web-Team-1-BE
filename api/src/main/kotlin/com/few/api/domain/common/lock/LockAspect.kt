package com.few.api.domain.common.lock

import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class LockAspect(
    private val subscriptionDao: SubscriptionDao,
) {
    private val log = KotlinLogging.logger {}

    @Pointcut("@annotation(com.few.api.domain.common.lock.LockFor)")
    fun lockPointcut() {}

    @Before("lockPointcut()")
    fun before(joinPoint: JoinPoint) {
        getLockFor(joinPoint).run {
            when (this.identifier) {
                LockIdentifier.SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID -> {
                    val useCaseIn = joinPoint.args[0] as SubscribeWorkbookUseCaseIn
                    getSubscriptionMemberIdAndWorkBookIdLock(useCaseIn)
                }
            }
        }
    }

    private fun getSubscriptionMemberIdAndWorkBookIdLock(useCaseIn: SubscribeWorkbookUseCaseIn) {
        subscriptionDao.getLock(useCaseIn.memberId, useCaseIn.workbookId).run {
            if (!this) {
                throw IllegalStateException("Already in progress for ${useCaseIn.memberId}'s subscription to ${useCaseIn.workbookId}")
            }
            log.debug { "Lock acquired for ${useCaseIn.memberId}'s subscription to ${useCaseIn.workbookId}" }
        }
    }

    @AfterReturning("lockPointcut()")
    fun afterReturning(joinPoint: JoinPoint) {
        getLockFor(joinPoint).run {
            when (this.identifier) {
                LockIdentifier.SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID -> {
                    val useCaseIn = joinPoint.args[0] as SubscribeWorkbookUseCaseIn
                    releaseSubscriptionMemberIdAndWorkBookIdLock(useCaseIn)
                }
            }
        }
    }

    @AfterThrowing("lockPointcut()")
    fun afterThrowing(joinPoint: JoinPoint) {
        getLockFor(joinPoint).run {
            when (this.identifier) {
                LockIdentifier.SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID -> {
                    val useCaseIn = joinPoint.args[0] as SubscribeWorkbookUseCaseIn
                    releaseSubscriptionMemberIdAndWorkBookIdLock(useCaseIn)
                }
            }
        }
    }

    private fun getLockFor(joinPoint: JoinPoint) =
        (joinPoint.signature as MethodSignature).method.getAnnotation(LockFor::class.java)

    private fun releaseSubscriptionMemberIdAndWorkBookIdLock(useCaseIn: SubscribeWorkbookUseCaseIn) {
        subscriptionDao.releaseLock(useCaseIn.memberId, useCaseIn.workbookId)
        log.debug { "Lock released for ${useCaseIn.memberId}'s subscription to ${useCaseIn.workbookId}" }
    }
}