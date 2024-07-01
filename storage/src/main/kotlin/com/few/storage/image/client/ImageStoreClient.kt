package com.few.storage.image.client

import com.few.storage.image.client.dto.ImageGetPreSignedObjectUrlArgs
import com.few.storage.image.client.dto.ImagePutObjectArgs
import com.few.storage.image.client.dto.ImageRemoveObjectArgs
import com.few.storage.image.client.dto.ImageWriteResponse

interface ImageStoreClient {

    fun getPreSignedObjectUrl(args: ImageGetPreSignedObjectUrlArgs): String?

    fun removeObject(args: ImageRemoveObjectArgs): Boolean

    fun putObject(args: ImagePutObjectArgs): ImageWriteResponse?
}