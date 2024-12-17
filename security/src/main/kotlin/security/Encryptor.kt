package security

interface Encryptor<T, R> {
    fun encrypt(plainText: T): R

    fun decrypt(encryptedText: R): T
}