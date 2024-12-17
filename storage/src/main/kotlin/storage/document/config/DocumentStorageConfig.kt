package storage.document.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import storage.config.StorageClientConfig

@Configuration
@ComponentScan(basePackages = [DocumentStorageConfig.BASE_PACKAGE])
@Import(StorageClientConfig::class)
class DocumentStorageConfig {
    companion object {
        const val BASE_PACKAGE = "storage.document"
        const val BEAN_NAME_PREFIX = "documentStore"
    }
}