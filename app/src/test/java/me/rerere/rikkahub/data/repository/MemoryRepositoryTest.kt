package me.rerere.rikkahub.data.repository

import me.rerere.rikkahub.data.model.Assistant
import me.rerere.rikkahub.utils.JsonInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.uuid.Uuid

class MemoryRepositoryTest {
    @Test
    fun `private memory uses assistant id`() {
        val assistant = Assistant()

        assertEquals(assistant.id.toString(), MemoryRepository.scopeOf(assistant))
    }

    @Test
    fun `group memory uses stable uuid namespace`() {
        val groupId = Uuid.random()
        val assistant = Assistant(memoryGroupId = groupId)

        assertEquals("g:$groupId", MemoryRepository.scopeOf(assistant))
        assertEquals("g:$groupId", MemoryRepository.scopeOf(groupId))
    }

    @Test
    fun `global memory takes priority over group`() {
        val assistant = Assistant(
            useGlobalMemory = true,
            memoryGroupId = Uuid.random(),
        )

        assertEquals(MemoryRepository.GLOBAL_MEMORY_ID, MemoryRepository.scopeOf(assistant))
    }

    @Test
    fun `legacy assistant json defaults to private memory`() {
        val assistant = JsonInstant.decodeFromString<Assistant>("{}")

        assertFalse(assistant.useGlobalMemory)
        assertNull(assistant.memoryGroupId)
        assertEquals(assistant.id.toString(), MemoryRepository.scopeOf(assistant))
    }
}
