package event

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.elements.*
import event.message.Message
import event.message.MessageRelay
import event.message.MessageReverseRelay
import event.message.MessageSender
import event.message.local.LocalSubscribeMessage
import org.springframework.context.event.EventListener

fun ClassesThat<GivenClassesConjunction>.areEvent(): GivenClassesConjunction = this.areAssignableTo(Event::class.java)

fun ClassesThat<GivenClassesConjunction>.areNotAbstractClasses(): GivenClassesConjunction = this.doNotHaveModifier(JavaModifier.ABSTRACT)

/**
 * Event rule
 *
 * 이벤트 모듈의 규칙을 정의 하고 검증 할 수 있도록 지원 한다.
 *
 * 해당 모듈을 사용하는 프로젝트에서 EventRule을 상속 받아서 이벤트 관련 규칙을 검증 한다.
 *
 * `검증 예시`:
 * ```kotlin
 * class ApiEventRule : EventRule() {
 *     companion object {
 *         var classes: JavaClasses? = null
 *
 *         @JvmStatic
 *         @BeforeAll
 *         fun setUp() {
 *             classes = ClassFileImporter().importPackages("com.few", "event")
 *             for (clazz in classes!!) {
 *                 println(clazz.name)
 *             }
 *         }
 *     }
 *
 *     @Test
 *     fun `TimeOutEvent 클래스와 TimeExpiredEvent는  @EventDetails의 outBox 속성이 false여야 한다`() {
 *         // 패키지를 지정하여 검증
 *         assertDoesNotThrow {
 *             `규칙 - TimeOutEvent 클래스와 TimeExpiredEvent는  @EventDetails의 outBox 속성이 false여야 한다`("com.few", "event")
 *         }
 *     }
 *
 *     @Test
 *     fun `MessageRelay는 MessageSender를 가지고 있어야 한다`() {
 *          // check 메서드를 사용하여 검증
 *         `규칙 - MessageRelay는 MessageSender를 가지고 있어야 한다`().check(classes)
 *     }
 * }
 *
 */
@Suppress("ktlint:standard:function-naming", "ktlint:standard:max-line-length")
abstract class EventRule {
    /**
     * 이벤트 클래스는 @event details 어노테이션이 붙어있어야 한다
     */
    fun `규칙 - 이벤트 클래스는 @EventDetails 어노테이션이 붙어있어야 한다`(): ClassesShouldConjunction =
        classes()
            .that()
            .areEvent()
            .and()
            .areNotAbstractClasses()
            .should()
            .beAnnotatedWith(EventDetails::class.java)

    /**
     * time out event 클래스를 상속하면 time expired event도 상속해야 한다
     *
     * @param packages 패키지 목록
     */
    fun `규칙 - TimeOutEvent 클래스를 상속하면 TimeExpiredEvent도 상속해야 한다`(vararg packages: String) {
        val timeOutEvents =
            ClassFileImporter()
                .withImportOption { clazz ->
                    clazz.contains("TimeOutEvent")
                }.importPackages(*packages)

        val timeExpiredEvents =
            ClassFileImporter()
                .withImportOption { clazz ->
                    clazz.contains("TimeExpiredEvent")
                }.importPackages(*packages)

        if (timeOutEvents.size != timeExpiredEvents.size) {
            throw AssertionError("TimeOutEvent 클래스의 수와 TimeExpiredEvent 클래스의 수가 다릅니다.")
        }
    }

    /**
     * TimeOutEvent 클래스와 TimeExpiredEvent는  @EventDetails의 outBox 속성이 false여야 한다
     *
     * @param packages 패키지 목록
     */
    fun `규칙 - TimeOutEvent 클래스와 TimeExpiredEvent는  @EventDetails의 outBox 속성이 false여야 한다`(vararg packages: String) {
        val timeOutEvents =
            ClassFileImporter()
                .withImportOption { clazz ->
                    clazz.contains("TimeOutEvent")
                }.importPackages(*packages)

        val timeExpiredEvents =
            ClassFileImporter()
                .withImportOption { clazz ->
                    clazz.contains("TimeExpiredEvent")
                }.importPackages(*packages)

        (timeOutEvents + timeExpiredEvents)
            .filterNot {
                it.name.contains("$")
            }.filterNot {
                it.name.split(".").last() == "TimeOutEvent" || it.name.split(".").last() == ("TimeExpiredEvent")
            }.forEach { event ->
                if (!event.isAnnotatedWith(EventDetails::class.java)) {
                    throw AssertionError("EventDetails 어노테이션이 없습니다.")
                }

                if (event.getAnnotationOfType(EventDetails::class.java).outBox) {
                    throw AssertionError("outBox 속성이 true입니다.")
                }
            }
    }

    /**
     * MessageRelay는 MessageSender를 가지고 있어야 한다
     */
    fun `규칙 - MessageRelay는 MessageSender를 가지고 있어야 한다`(): FieldsShouldConjunction {
        val isSubclassOfMessageSender =
            object : DescribedPredicate<JavaClass>("is a subclass of MessageSender") {
                override fun test(t: JavaClass?): Boolean = t?.isAssignableTo(MessageSender::class.java) ?: false
            }

        return fields()
            .that()
            .areDeclaredInClassesThat()
            .areAssignableTo(MessageRelay::class.java)
            .should()
            .haveRawType(isSubclassOfMessageSender)
    }

    /**
     *  local  message reverse relay는 on application event를 가지고 있어야 한다
     *
     * @param packages 패키지 목록
     */
    fun `규칙 - Local  MessageReverseRelay는 onApplicationEvent를 가지고 있어야 한다`(vararg packages: String): Unit =
        ClassFileImporter()
            .withImportOption { clazz ->
                clazz.contains("Local") && clazz.contains("MessageReverseRelay") && !clazz.contains("$")
            }.importPackages(*packages)
            .groupBy { it }
            .mapValues { (_, clazz) ->
                clazz.first().methods.map { method -> method.name }
            }.forEach {
                if (!it.value.contains("onApplicationEvent")) {
                    throw AssertionError("onApplicationEvent 메서드가 없습니다.")
                }
            }

    /**
     *  message reverse relay는 @event listener이 붙어 있는 on application event를 가지고 있어야 한다
     */
    fun `규칙 - MessageReverseRelay는 @EventListener이 붙어 있는 onApplicationEvent를 가지고 있어야 한다`(): MethodsShouldConjunction =
        methods()
            .that()
            .haveName("onApplicationEvent")
            .and()
            .areDeclaredInClassesThat()
            .areAssignableTo(MessageReverseRelay::class.java)
            .should()
            .beAnnotatedWith(EventListener::class.java)

    /**
     *  message reverse relay는 @event listener이 붙어 있는 on application event는 message 클래스를 첫 번째 파라미터로 가지고 있어야 한다
     */
    fun `규칙 - MessageReverseRelay는 @EventListener이 붙어 있는 onApplicationEvent는 Message 클래스를 첫 번째 파라미터로 가지고 있어야 한다`(): MethodsShouldConjunction {
        val isSubclassOfMessage =
            object : DescribedPredicate<List<JavaClass>>("is a subclass of Message") {
                override fun test(t: List<JavaClass>?): Boolean = t?.first()?.isAssignableTo(Message::class.java) ?: false
            }
        return methods()
            .that()
            .haveName("onApplicationEvent")
            .and()
            .areDeclaredInClassesThat()
            .areAssignableTo(MessageReverseRelay::class.java)
            .should()
            .beAnnotatedWith(EventListener::class.java)
            .andShould()
            .haveRawParameterTypes(isSubclassOfMessage)
    }

    /**
     *  local message reverse relay의 on application event 파라미터는 @local subscribe message 어노테이션에서 설정한 토픽의 이름을 포함해야 한다
     *
     * @param packages 패키지 목록
     */
    fun `규칙 - Local MessageReverseRelay의 onApplicationEvent 파라미터는 @LocalSubscribeMessage 어노테이션에서 설정한 토픽의 이름을 포함해야 한다`(
        vararg packages: String,
    ): Unit =
        ClassFileImporter()
            .withImportOption { clazz ->
                clazz.contains("Local") && clazz.contains("MessageReverseRelay") && !clazz.contains("$")
            }.importPackages(*packages)
            .filter { it.methods.any { method -> method.name == "onApplicationEvent" } }
            .forEach {
                val method = it.methods.first { method -> method.name == "onApplicationEvent" }
                val topic = method.getAnnotationOfType(LocalSubscribeMessage::class.java).topic
                val message = method.parameterTypes[0]
                if (!message.name.contains(topic, ignoreCase = true)) {
                    throw AssertionError("onApplicationEvent 메서드의 이름에 토픽이 포함되어 있지 않습니다.")
                }
            }
}