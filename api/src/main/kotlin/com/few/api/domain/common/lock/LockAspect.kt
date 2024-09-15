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
                    getSubscriptionMemberIdAndWorkBookIdLockCase(joinPoint)
                }
            }
        }
    }

    private fun getSubscriptionMemberIdAndWorkBookIdLockCase(joinPoint: JoinPoint) {
        if (joinPoint.args[0] is SubscribeWorkbookUseCaseIn) {
            val useCaseIn = joinPoint.args[0] as SubscribeWorkbookUseCaseIn
            getSubscriptionMemberIdAndWorkBookIdLock(useCaseIn)
        } else {
            val memberId = joinPoint.args[0] as Long
            val workbookId = joinPoint.args[1] as Long
            getSubscriptionMemberIdAndWorkBookIdLock(memberId, workbookId)
        }
    }

    private fun getSubscriptionMemberIdAndWorkBookIdLock(useCaseIn: SubscribeWorkbookUseCaseIn) {
        getSubscriptionMemberIdAndWorkBookIdLock(useCaseIn.memberId, useCaseIn.workbookId)
    }

    private fun getSubscriptionMemberIdAndWorkBookIdLock(memberId: Long, workbookId: Long) {
        subscriptionDao.getLock(memberId, workbookId).run {
            if (!this) {
                throw IllegalStateException("Already in progress for $memberId's subscription to $workbookId")
            }
            log.debug { "Lock acquired for $memberId's subscription to $workbookId" }
        }
    }

    @AfterReturning("lockPointcut()")
    fun afterReturning(joinPoint: JoinPoint) {
        getLockFor(joinPoint).run {
            when (this.identifier) {
                LockIdentifier.SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID -> {
                    releaseSubscriptionMemberIdAndWorkBookIdLockCase(joinPoint)
                }
            }
        }
    }

    @AfterThrowing("lockPointcut()")
    fun afterThrowing(joinPoint: JoinPoint) {
        getLockFor(joinPoint).run {
            when (this.identifier) {
                LockIdentifier.SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID -> {
                    releaseSubscriptionMemberIdAndWorkBookIdLockCase(joinPoint)
                }
            }
        }
    }

    private fun getLockFor(joinPoint: JoinPoint) =
        (joinPoint.signature as MethodSignature).method.getAnnotation(LockFor::class.java)

    private fun releaseSubscriptionMemberIdAndWorkBookIdLockCase(joinPoint: JoinPoint) {
        if (joinPoint.args[0] is SubscribeWorkbookUseCaseIn) {
            val useCaseIn = joinPoint.args[0] as SubscribeWorkbookUseCaseIn
            releaseSubscriptionMemberIdAndWorkBookIdLock(useCaseIn)
        } else {
            val memberId = joinPoint.args[0] as Long
            val workbookId = joinPoint.args[1] as Long
            releaseSubscriptionMemberIdAndWorkBookIdLock(memberId, workbookId)
        }
    }

    private fun releaseSubscriptionMemberIdAndWorkBookIdLock(useCaseIn: SubscribeWorkbookUseCaseIn) {
        releaseSubscriptionMemberIdAndWorkBookIdLock(useCaseIn.memberId, useCaseIn.workbookId)
    }

    private fun releaseSubscriptionMemberIdAndWorkBookIdLock(memberId: Long, workbookId: Long) {
        subscriptionDao.releaseLock(memberId, workbookId)
        log.debug { "Lock released for $memberId's subscription to $workbookId" }
    }
}