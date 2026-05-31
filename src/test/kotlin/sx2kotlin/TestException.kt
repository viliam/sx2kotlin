package sx2kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestException {

    @Test
    fun testValidMessageCreation() {
        val position = Position(4, 0)
        val line = "Example line"
        val result = SxError.makeMessage(SxErrorType.UNEXPECTED_PREFIX, position, line)
        val expected = "UNEXPECTED_PREFIX  : Example line    \n char = p"
        assertEquals(expected, result)
    }
}
