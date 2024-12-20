package event.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [EventConfig.BASE_PACKAGE])
class EventConfig {
    companion object {
        const val BASE_PACKAGE = "event"
        const val BEAN_NAME_PREFIX = "event"
    }
}