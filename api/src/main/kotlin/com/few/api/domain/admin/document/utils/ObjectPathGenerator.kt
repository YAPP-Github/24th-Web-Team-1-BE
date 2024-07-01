package com.few.api.domain.admin.document.utils

import java.time.LocalDate
import kotlin.random.Random

object ObjectPathGenerator {

    fun imagePath(suffix: String): String {
        val dateDir = LocalDate.now().toString()
        return "images/$dateDir/${generateImageName()}" + ".$suffix"
    }

    fun documentPath(suffix: String): String {
        val dateDir = LocalDate.now().toString()
        return "documents/$dateDir/${generateImageName()}" + ".$suffix"
    }

    private fun generateImageName(): String {
        return randomString()
    }

    private fun randomString(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..16)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }
}