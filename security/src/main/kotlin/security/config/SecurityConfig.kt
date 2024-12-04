package security.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [SecurityConfig.BASE_PACKAGE])
class SecurityConfig {
    companion object {
        const val BASE_PACKAGE = " security"
        const val BEAN_NAME_PREFIX = "security"
    }
}