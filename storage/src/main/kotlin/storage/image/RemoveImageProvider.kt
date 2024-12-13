package storage.image

import storage.RemoveObjectProvider

fun interface RemoveImageProvider : RemoveObjectProvider {
    override fun execute(image: String): Boolean
}