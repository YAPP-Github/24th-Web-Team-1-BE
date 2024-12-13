package storage.image

import storage.GetPreSignedObjectUrlProvider

fun interface GetPreSignedImageUrlProvider : GetPreSignedObjectUrlProvider {
    override fun execute(image: String): String?
}