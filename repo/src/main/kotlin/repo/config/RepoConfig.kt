package repo.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [RepoConfig.BASE_PACKAGE])
class RepoConfig {
    companion object {
        const val BASE_PACKAGE = "repo"
        const val BEAN_NAME_PREFIX = "repo"
    }
}