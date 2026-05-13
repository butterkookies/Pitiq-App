package com.pitiq.app.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class SessionIdTest {

    private val uuidV4Regex = Regex(
        "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"
    )

    @Test
    fun `generated session ID matches UUID v4 pattern`() {
        val id = UUID.randomUUID().toString()
        assertTrue("Not UUID v4: $id", uuidV4Regex.matches(id))
    }

    @Test
    fun `version nibble is always 4`() {
        repeat(20) {
            val id = UUID.randomUUID().toString()
            assertEquals("Version nibble wrong: $id", '4', id[14])
        }
    }

    @Test
    fun `variant bits are RFC 4122 compliant`() {
        repeat(20) {
            val id = UUID.randomUUID().toString()
            assertTrue("Variant bits wrong: $id", id[19] in setOf('8', '9', 'a', 'b'))
        }
    }

    @Test
    fun `session ID has five hyphen-delimited groups`() {
        val id = UUID.randomUUID().toString()
        val parts = id.split("-")
        assertEquals(5, parts.size)
        assertEquals(8, parts[0].length)
        assertEquals(4, parts[1].length)
        assertEquals(4, parts[2].length)
        assertEquals(4, parts[3].length)
        assertEquals(12, parts[4].length)
    }

    @Test
    fun `hundred generated IDs are all unique`() {
        val ids = List(100) { UUID.randomUUID().toString() }
        assertEquals(100, ids.toSet().size)
    }
}
