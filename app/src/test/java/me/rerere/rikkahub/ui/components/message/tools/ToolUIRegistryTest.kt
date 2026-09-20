package me.rerere.rikkahub.ui.components.message.tools

import org.junit.Assert.assertSame
import org.junit.Test

class ToolUIRegistryTest {

    @Test
    fun calendarDeleteUsesDedicatedRenderer() {
        assertSame(
            CalendarDeleteToolUI,
            ToolUIRegistry.resolve("calendar_delete"),
        )
    }
}
