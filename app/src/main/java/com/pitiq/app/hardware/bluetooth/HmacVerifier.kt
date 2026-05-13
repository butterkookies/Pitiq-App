package com.pitiq.app.hardware.bluetooth

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacVerifier {

    fun computeHmacSha256(challenge: ByteArray, sharedSecret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(sharedSecret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(challenge).toHex()
    }

    fun verify(challenge: ByteArray, sharedSecret: String, hmacHex: String): Boolean {
        return try {
            val expected = computeHmacSha256(challenge, sharedSecret).lowercase()
            val actual = hmacHex.lowercase()
            // Constant-time comparison prevents timing side-channel attacks.
            MessageDigest.isEqual(
                expected.toByteArray(Charsets.UTF_8),
                actual.toByteArray(Charsets.UTF_8),
            )
        } catch (_: Exception) {
            false
        }
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
