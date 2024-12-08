package storage.image.config

import storage.config.StorageClientConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = [ImageStorageConfig.BASE_PACKAGE])
@Import(StorageClientConfig::class)
class ImageStorageConfig {
    companion object {
        const val BASE_PACKAGE = "storage.image"
        const val BEAN_NAME_PREFIX = "imageStore"
    }
}