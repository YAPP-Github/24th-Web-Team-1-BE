package com.few.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiMain

fun main(args: Array<String>) {
    runApplication<ApiMain>(*args)
}