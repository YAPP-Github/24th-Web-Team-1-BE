package storage

import java.io.File

interface PutObjectProvider<T> {
    fun execute(
        name: String,
        file: File,
    ): T?
}