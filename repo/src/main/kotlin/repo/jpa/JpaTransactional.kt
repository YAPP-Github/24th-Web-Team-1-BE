package repo.jpa

import org.springframework.core.annotation.AliasFor
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import repo.config.JpaConfig.Companion.JPA_TX
import kotlin.reflect.KClass

/**
 * JPA 트랜잭션을 처리하는 어노테이션
 * transactionManager는 JpaConfig에서 설정한 값을 기본으로 사용한다.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Transactional(transactionManager = JPA_TX)
annotation class JpaTransactional(
    @get:AliasFor(annotation = Transactional::class, attribute = "label")
    val label: Array<String> = [],
    @get:AliasFor(annotation = Transactional::class, attribute = "propagation")
    val propagation: Propagation = Propagation.REQUIRED,
    @get:AliasFor(annotation = Transactional::class, attribute = "isolation")
    val isolation: Isolation = Isolation.DEFAULT,
    @get:AliasFor(annotation = Transactional::class, attribute = "timeout")
    val timeout: Int = -1,
    @get:AliasFor(annotation = Transactional::class, attribute = "timeoutString")
    val timeoutString: String = "",
    @get:AliasFor(annotation = Transactional::class, attribute = "readOnly")
    val readOnly: Boolean = false,
    @get:AliasFor(annotation = Transactional::class, attribute = "rollbackFor")
    val rollbackFor: Array<KClass<out Throwable>> = [],
    @get:AliasFor(annotation = Transactional::class, attribute = "rollbackForClassName")
    val rollbackForClassName: Array<String> = [],
    @get:AliasFor(annotation = Transactional::class, attribute = "noRollbackFor")
    val noRollbackFor: Array<KClass<out Throwable>> = [],
    @get:AliasFor(annotation = Transactional::class, attribute = "noRollbackForClassName")
    val noRollbackForClassName: Array<String> = [],
)