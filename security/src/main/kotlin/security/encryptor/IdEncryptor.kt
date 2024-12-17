package security.encryptor

import security.Encryptor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class IdEncryptor(
    private val algorithm: String,
    private val secretKey: String,
    private val transformation: String,
    private val keySize: Int,
    private val iv: String,
) : Encryptor<String, String> {
    private var key: SecretKeySpec =
        KeyGenerator
            .getInstance(algorithm)
            .apply {
                init(keySize)
            }.run {
                SecretKeySpec(secretKey.toByteArray(), this@IdEncryptor.algorithm)
            }
    private var encodeCipher: Cipher =
        Cipher.getInstance(transformation).apply {
            init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(this@IdEncryptor.iv.toByteArray()))
        }
    private var decodeCipher: Cipher =
        Cipher.getInstance(transformation).apply {
            init(Cipher.DECRYPT_MODE, key, IvParameterSpec(this@IdEncryptor.iv.toByteArray()))
        }

    override fun encrypt(plainText: String): String {
        val encrypted: ByteArray = encodeCipher.doFinal(plainText.toByteArray())
        return byteArrayToHex(encrypted)
    }

    private fun byteArrayToHex(ba: ByteArray): String {
        val sb = StringBuffer(ba.size * 2)
        var hexNumber: String
        for (x in ba.indices) {
            hexNumber = "0" + Integer.toHexString(0xff and ba[x].toInt())
            sb.append(hexNumber.substring(hexNumber.length - 2))
        }
        return sb.toString()
    }

    override fun decrypt(encryptedText: String): String {
        val encrypted: ByteArray = hexToByteArray(encryptedText)
        val original = decodeCipher.doFinal(encrypted)
        return String(original)
    }

    private fun hexToByteArray(msg: String): ByteArray {
        val b = ByteArray(msg.length / 2)
        for (i in b.indices) {
            val index = i * 2
            val v = msg.substring(index, index + 2).toInt(16)
            b[i] = v.toByte()
        }
        return b
    }
}