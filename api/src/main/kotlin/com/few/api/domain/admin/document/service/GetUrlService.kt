package com.few.api.domain.admin.document.service

import com.few.api.domain.admin.document.service.dto.GetUrlInDto
import com.few.api.domain.admin.document.service.dto.GetUrlOutDto
import com.few.api.domain.admin.document.service.dto.getPreSignedUrlServiceKey
import com.few.api.exception.common.ExternalIntegrationException
import com.few.storage.GetPreSignedObjectUrlService
import com.few.storage.image.service.support.CdnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URL
import java.util.*

interface GetUrlService {
    fun execute(query: GetUrlInDto): GetUrlOutDto
}

@Profile("local")
@Service
class GetLocalUrlService(
    private val services: Map<String, GetPreSignedObjectUrlService>,
) : GetUrlService {
    override fun execute(query: GetUrlInDto): GetUrlOutDto {
        val service = services.keys.firstOrNull { key ->
            key.lowercase(Locale.getDefault()).contains(query.getPreSignedUrlServiceKey())
        }?.let { services[it] } ?: throw IllegalArgumentException("Cannot find service for ${query.getPreSignedUrlServiceKey()}")

        return service.execute(query.`object`)?.let {
            GetUrlOutDto(URL(it))
        } ?: throw ExternalIntegrationException("external.presignedfail.url")
    }
}

@Profile("!local")
@Service
class GetCdnUrlService(
    private val cdnProperty: CdnProperty,
) : GetUrlService {
    override fun execute(query: GetUrlInDto): GetUrlOutDto {
        return GetUrlOutDto(URL(cdnProperty.url + "/" + query.`object`))
    }
}