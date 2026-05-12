package com.pitiq.app.hardware.bluetooth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HmacVerifierTest {

    private val secret = "test-shared-secret-pitiq"
    private val challenge = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

    @Test
    fun `correct HMAC is accepted`() {
        val hmac = computeHmac(challenge, secret)
        assertTrue(HmacVerifier.verify(challenge, secret, hmac))
    }

    @Test
    fun `tampered challenge is rejected`() {
        val tamperedChallenge = challenge.copyOf().also { it[0] = (it[0] + 1).toByte() }
        val hmac = computeHmac(tamperedChallenge, secret)
        assertFalse(HmacVerifier.verify(challenge, secret, hmac))
    }

    @Test
    fun `wrong secret is rejected`() {
        val hmac = computeHmac(challenge, "wrong-secret")
        assertFalse(HmacVerifier.verify(challenge, secret, hmac))
    }

    @Test
    fun `tampered HMAC is rejected`() {
        val hmac = computeHmac(challenge, secret)
        val tampered = "00" + hmac.drop(2)
        assertFalse(HmacVerifier.verify(challenge, secret, tampered))
    }

    @Test
    fun `uppercase hex is accepted`() {
        val hmac = computeHmac(challenge, secret).uppercase()
        assertTrue(HmacVerifier.verify(challenge, secret, hmac))
    }

    @Test
    fun `mixed case hex is accepted`() {
        val hmac = computeHmac(challenge, secret)
        val mixed = hmac.mapIndexed { i, c -> if (i % 2 == 0) c.uppercaseChar() else c }.joinToString("")
        assertTrue(HmacVerifier.verify(challenge, secret, mixed))
    }

    @Test
    fun `all-zeros HMAC is rejected`() {
        assertFalse(HmacVerifier.verify(challenge, secret, "0".repeat(64)))
    }

    @Test
    fun `computeHmacSha256 round-trips with verify`() {
        val hmac = HmacVerifier.computeHmacSha256(challenge, secret)
        assertTrue(HmacVerifier.verify(challenge, secret, hmac))
    }

    private fun computeHmac(data: ByteArray, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(data).joinToString("") { "%02x".format(it) }
    }
}
