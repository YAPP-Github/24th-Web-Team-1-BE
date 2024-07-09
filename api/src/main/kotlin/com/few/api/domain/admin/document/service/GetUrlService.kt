package com.few.api.domain.admin.document.service

import com.few.api.domain.admin.document.service.dto.GetUrlInDto
import com.few.api.domain.admin.document.service.dto.getPreSignedUrlServiceKey
import com.few.api.exception.common.ExternalIntegrationException
import com.few.storage.GetPreSignedObjectUrlService
import com.few.storage.image.service.support.CdnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URL
import java.util.*

interface GetUrlService {
    fun execute(query: GetUrlInDto): URL
}

@Profile("local")
@Service
class GetLocalUrlService(
    private val services: Map<String, GetPreSignedObjectUrlService>
) : GetUrlService {
    override fun execute(query: GetUrlInDto): URL {
        val service = services.keys.stream().filter { key ->
            key.lowercase(Locale.getDefault())
                .contains(query.getPreSignedUrlServiceKey())
        }.findAny().let {
            if (it.isEmpty) throw IllegalArgumentException("Cannot find service for ${query.getPreSignedUrlServiceKey()}")
            services[it.get()]!!
        }

        return service.execute(query.`object`)?.let {
            URL(it)
        } ?: throw ExternalIntegrationException("external.presignedfail.url")
    }
}

@Profile("!local")
@Service
class GetCdnUrlService(
    private val cdnProperty: CdnProperty
) : GetUrlService {
    override fun execute(query: GetUrlInDto): URL {
        return URL(cdnProperty.url + "/" + query.`object`)
    }
}