package web.config

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

class ClonedTaskDecorator : TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        val contextMap = MDC.getCopyOfContextMap()
        return Runnable {
            if (contextMap != null) {
                MDC.setContextMap(contextMap)
            }
            runnable.run()
        }
    }
}