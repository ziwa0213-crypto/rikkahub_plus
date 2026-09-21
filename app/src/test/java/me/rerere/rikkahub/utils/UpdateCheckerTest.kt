package me.rerere.rikkahub.utils

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdateCheckerTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `github release tag becomes a comparable version`() {
        val info = json.decodeFromString<UpdateInfo>(
            """
            {
              "tag_name": "v2.5.3-work.1",
              "body": "Release notes",
              "published_at": "2026-09-21T10:00:00Z",
              "name": "v2.5.3-work.1",
              "assets": [{"name": "rikkahub-release.apk"}]
            }
            """.trimIndent()
        )

        assertEquals("2.5.3-work.1", info.version)
        assertEquals("Release notes", info.changelog)
        assertEquals("2026-09-21T10:00:00Z", info.publishedAt)
    }

    @Test
    fun `optional release fields do not prevent parsing`() {
        val info = json.decodeFromString<UpdateInfo>("""{"tag_name":"2.5.3"}""")

        assertEquals("2.5.3", info.version)
        assertEquals("", info.changelog)
        assertNull(info.publishedAt)
    }
}
