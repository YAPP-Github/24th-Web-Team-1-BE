package email

import org.thymeleaf.context.AbstractContext
import org.thymeleaf.context.Context

class EmailContext {
    private val context = Context()

    fun setVariable(name: String, value: Any) {
        context.setVariable(name, value)
    }

    fun getContext(): AbstractContext {
        return context
    }
}