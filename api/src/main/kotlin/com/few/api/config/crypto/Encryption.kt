package com.few.api.config.crypto

interface Encryption<T, R> {

    fun encrypt(plainText: T): R

    fun decrypt(encryptedText: R): T
}