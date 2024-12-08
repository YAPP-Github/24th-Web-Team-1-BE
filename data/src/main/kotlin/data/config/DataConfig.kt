package data.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [DataConfig.BASE_PACKAGE])
class DataConfig {
    companion object {
        const val BASE_PACKAGE = "data"
        const val BEAN_NAME_PREFIX = "data"
    }
}