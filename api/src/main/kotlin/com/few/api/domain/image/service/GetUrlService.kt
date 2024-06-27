package com.few.api.domain.image.service

import com.few.api.domain.image.service.dto.GetUrlQuery
import com.few.image.service.GetPreSignedImageUrlService
import com.few.storage.image.service.support.CdnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URL

interface GetUrlService {
    fun execute(query: GetUrlQuery): URL
}

@Profile("local")
@Service
class GetLocalUrlService(
    private val getPreSignedImageUrlService: GetPreSignedImageUrlService
) : GetUrlService {
    override fun execute(query: GetUrlQuery): URL {
        return getPreSignedImageUrlService.execute(query.`object`)?.let {
            URL(it)
        } ?: throw IllegalStateException("Failed to get image url")
    }
}

@Profile("!local")
@Service
class GetCdnUrlService(
    private val cdnProperty: CdnProperty
) : GetUrlService {
    override fun execute(query: GetUrlQuery): URL {
        return URL(cdnProperty.url + "/" + query.`object`)
    }
}