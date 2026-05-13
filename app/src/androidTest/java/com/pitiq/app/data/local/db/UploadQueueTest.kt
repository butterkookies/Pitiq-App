package com.pitiq.app.data.local.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pitiq.app.data.local.db.entity.SessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UploadQueueTest {

    private lateinit var db: PitiqDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PitiqDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() = db.close()

    private fun entity(id: String, status: String = "pending") = SessionEntity(
        sessionId = id,
        locationId = "cafe-01",
        coinsInserted = 40,
        thermalImagePath = null,
        colorImagePath = null,
        gifPath = null,
        uploadStatus = status,
    )

    @Test
    fun `enqueued session appears in pending list`() = runBlocking {
        db.uploadQueueDao().enqueue(entity("s-001"))
        val pending = db.uploadQueueDao().getPending()
        assertEquals(1, pending.size)
        assertEquals("s-001", pending[0].sessionId)
    }

    @Test
    fun `session leaves pending after status updated to uploaded`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("s-002"))
        dao.updateStatus("s-002", "uploaded", System.currentTimeMillis())
        assertTrue(dao.getPending().isEmpty())
    }

    @Test
    fun `multiple offline sessions all appear pending`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("s-003"))
        dao.enqueue(entity("s-004"))
        dao.enqueue(entity("s-005"))
        assertEquals(3, dao.getPending().size)
    }

    @Test
    fun `delete removes session from queue`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("s-006"))
        dao.delete("s-006")
        assertTrue(dao.getPending().isEmpty())
    }

    @Test
    fun `observePending flow emits empty when all sessions uploaded`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("s-007"))
        dao.updateStatus("s-007", "uploaded", System.currentTimeMillis())
        assertTrue(dao.observePending().first().isEmpty())
    }

    @Test
    fun `enqueue with duplicate ID replaces existing entry`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("s-008", status = "failed"))
        dao.enqueue(entity("s-008", status = "pending"))
        val pending = dao.getPending()
        assertEquals(1, pending.size)
        assertEquals("pending", pending[0].uploadStatus)
    }

    @Test
    fun `failed status session does not appear in pending`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("s-009"))
        dao.updateStatus("s-009", "failed", System.currentTimeMillis())
        assertTrue(dao.getPending().isEmpty())
    }

    @Test
    fun `pending sessions ordered by createdAt ascending`() = runBlocking {
        val dao = db.uploadQueueDao()
        dao.enqueue(entity("early").copy(createdAt = 1000L))
        dao.enqueue(entity("late").copy(createdAt = 2000L))
        val pending = dao.getPending()
        assertEquals("early", pending[0].sessionId)
        assertEquals("late", pending[1].sessionId)
    }
}
