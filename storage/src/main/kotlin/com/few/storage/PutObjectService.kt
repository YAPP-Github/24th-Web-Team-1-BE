package com.few.storage

import java.io.File

interface PutObjectService<T> {

    fun execute(name: String, file: File): T?
}