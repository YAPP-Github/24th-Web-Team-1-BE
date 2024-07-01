package com.few.api.repo.dao.document

import com.few.api.repo.dao.document.command.InsertDocumentIfoCommand
import jooq.jooq_dsl.tables.DocumentIfo
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class DocumentDao(
    private val dslContext: DSLContext
) {

    fun insertDocumentIfo(command: InsertDocumentIfoCommand): Long? {
        return dslContext.insertInto(DocumentIfo.DOCUMENT_IFO)
            .set(DocumentIfo.DOCUMENT_IFO.PATH, command.path)
            .set(DocumentIfo.DOCUMENT_IFO.URL, command.url.toString())
            .set(DocumentIfo.DOCUMENT_IFO.ALIAS, command.alias)
            .returning(DocumentIfo.DOCUMENT_IFO.ID)
            .fetchOne()?.id
    }
}