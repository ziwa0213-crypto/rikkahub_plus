package me.rerere.rikkahub.data.ai.tools.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalendarToolTest {

    @Test
    fun acceptsPositiveInteger() {
        assertEquals(42L, parseCalendarEventId(" 42 "))
    }

    @Test
    fun rejectsMissingZeroNegativeAndNonNumericValues() {
        assertNull(parseCalendarEventId(null))
        assertNull(parseCalendarEventId("0"))
        assertNull(parseCalendarEventId("-1"))
        assertNull(parseCalendarEventId("not-a-number"))
    }
}
