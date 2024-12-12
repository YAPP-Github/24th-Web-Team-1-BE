package storage.image.client

import storage.image.client.dto.ImageGetPreSignedObjectUrlArgs
import storage.image.client.dto.ImagePutObjectArgs
import storage.image.client.dto.ImageRemoveObjectArgs
import storage.image.client.dto.ImageWriteResponse

interface ImageStoreClient {

    fun getPreSignedObjectUrl(args: ImageGetPreSignedObjectUrlArgs): String?

    fun removeObject(args: ImageRemoveObjectArgs): Boolean

    fun putObject(args: ImagePutObjectArgs): ImageWriteResponse?
}