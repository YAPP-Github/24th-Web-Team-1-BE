package com.few.api.repo.explain

import org.jooq.Query
import java.io.File

class ResultGenerator {

    companion object {
        fun execute(query: Query, explain: String, fileName: String) {
            File("src/test/resources/explain").let { dir ->
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                File(dir, "$fileName.txt").let { file ->
                    if (file.exists()) {
                        file.delete()
                    }
                    file.createNewFile()
                    file.appendText(query.toString())
                    file.appendText("\n\n")
                    file.appendText(explain)
                }
            }
        }
    }
}