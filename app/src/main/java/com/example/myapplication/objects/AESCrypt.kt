package com.example.myapplication

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AESCrypt {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val SECRET_KEY = "Aplikacja123"

    fun encrypt(password: String?): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, generateKey())
        val encryptedBytes = cipher.doFinal(password?.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(password: String?): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, generateKey())
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(password))
        return String(decryptedBytes)
    }

    private fun generateKey(): SecretKeySpec {
        val key = ByteArray(16)
        val keySpec = SecretKeySpec(key, ALGORITHM)
        return keySpec
    }
}