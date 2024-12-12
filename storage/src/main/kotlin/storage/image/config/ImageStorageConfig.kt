package storage.image.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import storage.config.StorageClientConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import storage.image.config.properties.CdnProperty

@Configuration
@ComponentScan(basePackages = [ImageStorageConfig.BASE_PACKAGE])
@Import(StorageClientConfig::class)
class ImageStorageConfig {
    companion object {
        const val BASE_PACKAGE = "storage.image"
        const val BEAN_NAME_PREFIX = "imageStore"
        const val CDN_PROPERTY = BEAN_NAME_PREFIX + "CdnProperty"
    }

    @Bean(name = [CDN_PROPERTY])
    fun cdnProperty(
        @Value("\${cdn.url}") url: String,
    ): CdnProperty {
        return CdnProperty(url)
    }
}