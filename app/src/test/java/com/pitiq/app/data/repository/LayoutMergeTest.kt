package com.pitiq.app.data.repository

import com.pitiq.app.domain.model.Layout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutMergeTest {

    // Mirrors the merge logic from LayoutRepository.getLayouts()
    private fun merge(defaults: List<Layout>, remote: List<Layout>): List<Layout> {
        val remoteIds = remote.map { it.id }.toSet()
        val mergedDefaults = defaults.filter { it.id !in remoteIds }
        return (mergedDefaults + remote).sortedBy { it.sortOrder }
    }

    private fun remoteLayout(id: String, sortOrder: Int = 0) = Layout(
        id = id,
        name = "Remote $id",
        slotCount = 4,
        frameAssetPath = "https://cdn.example.com/$id.png",
        previewImagePath = "https://cdn.example.com/$id-preview.png",
        version = 2,
        isDefault = false,
        sortOrder = sortOrder,
    )

    @Test
    fun `no remote returns all three defaults`() {
        val result = merge(LayoutRepository.defaults, emptyList())
        assertEquals(3, result.size)
        assertTrue(result.all { it.isDefault })
    }

    @Test
    fun `remote layout with matching ID replaces default`() {
        val remote = remoteLayout("default-2slot", sortOrder = 0)
        val result = merge(LayoutRepository.defaults, listOf(remote))
        assertEquals(3, result.size)
        val replaced = result.first { it.id == "default-2slot" }
        assertFalse(replaced.isDefault)
        assertEquals("Remote default-2slot", replaced.name)
    }

    @Test
    fun `remote layout with new ID appended alongside defaults`() {
        val remote = remoteLayout("custom-polaroid", sortOrder = 99)
        val result = merge(LayoutRepository.defaults, listOf(remote))
        assertEquals(4, result.size)
        assertTrue(result.any { it.id == "custom-polaroid" })
    }

    @Test
    fun `result is sorted by sortOrder ascending`() {
        val remote = listOf(
            remoteLayout("remote-b", sortOrder = 5),
            remoteLayout("remote-a", sortOrder = 2),
        )
        val result = merge(LayoutRepository.defaults, remote)
        val orders = result.map { it.sortOrder }
        for (i in 0 until orders.size - 1) {
            assertTrue("Not sorted at index $i: $orders", orders[i] <= orders[i + 1])
        }
    }

    @Test
    fun `all three defaults overridden by remote`() {
        val remote = listOf(
            remoteLayout("default-2slot", sortOrder = 0),
            remoteLayout("default-4slot", sortOrder = 1),
            remoteLayout("default-6slot", sortOrder = 2),
        )
        val result = merge(LayoutRepository.defaults, remote)
        assertEquals(3, result.size)
        assertTrue(result.none { it.isDefault })
    }

    @Test
    fun `multiple remote layouts all appear in result`() {
        val remote = listOf(
            remoteLayout("style-vintage", sortOrder = 10),
            remoteLayout("style-modern", sortOrder = 11),
            remoteLayout("style-minimal", sortOrder = 12),
        )
        val result = merge(LayoutRepository.defaults, remote)
        assertEquals(6, result.size)
    }

    @Test
    fun `empty defaults with remote returns remote only`() {
        val remote = listOf(remoteLayout("only-remote", sortOrder = 0))
        val result = merge(emptyList(), remote)
        assertEquals(1, result.size)
        assertEquals("only-remote", result[0].id)
    }
}
