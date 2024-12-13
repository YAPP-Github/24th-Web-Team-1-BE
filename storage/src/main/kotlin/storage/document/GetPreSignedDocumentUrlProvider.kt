package storage.document

import storage.GetPreSignedObjectUrlProvider

interface GetPreSignedDocumentUrlProvider : GetPreSignedObjectUrlProvider {
    override fun execute(image: String): String?
}