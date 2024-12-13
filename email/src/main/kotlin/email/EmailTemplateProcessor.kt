package email

import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine

@Component
class EmailTemplateProcessor(
    private val templateEngine: TemplateEngine,
) {

    fun process(template: String, context: EmailContext): String {
        return templateEngine.process(template, context.getContext())
    }
}