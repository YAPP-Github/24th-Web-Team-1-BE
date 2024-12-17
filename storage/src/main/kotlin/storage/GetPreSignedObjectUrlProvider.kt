package storage

interface GetPreSignedObjectUrlProvider {
    fun execute(image: String): String?
}