package com.few.api.domain.admin.repo.image

import com.few.api.domain.admin.repo.image.command.InsertImageIfoCommand
import jooq.jooq_dsl.tables.ImageIfo
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ImageDao(
    private val dslContext: DSLContext,
) {
    // todo test
    fun insertImageIfo(command: InsertImageIfoCommand): Long? =
        dslContext
            .insertInto(ImageIfo.IMAGE_IFO)
            .set(ImageIfo.IMAGE_IFO.PATH, command.imagePath)
            .set(ImageIfo.IMAGE_IFO.URL, command.url.toString())
            .set(ImageIfo.IMAGE_IFO.ALIAS, command.alias)
            .returning(ImageIfo.IMAGE_IFO.ID)
            .fetchOne()
            ?.id
}