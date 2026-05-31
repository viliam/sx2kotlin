package sx2kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sx2kotlin.parsers.IntegerParser
import sx2kotlin.parsers.SimpleExpressionParser

class TestExpressionsParsers {

    @Test
    fun testReadValidInteger() {
        val text = Text(listOf("123"))
        val result = SimpleExpressionParser.i().read(text)
        assertEquals(ExpType.INT, result.expType)
    }

    @Test
    fun testReadInvalidIntegerRaisesError() {
        val text = Text(listOf("abc")) // not an integer

        val exception = assertThrows<SxError> {
            IntegerParser.i().read(text)
        }
        assertEquals(SxErrorType.EXPECTED_INT, exception.typ)
    }

    @Test
    fun testReadSimpleExpression() {
        val positiveCases = mapOf(
            "23" to ExpType.INT,
            "ahoj" to ExpType.UNKNOWN
        )
        for ((word, expectedType) in positiveCases) {
            val text = Text(listOf(word))
            val result = SimpleExpressionParser.i().read(text)
            assertEquals(expectedType, result.expType, "Failed for word: $word")
        }
    }
}
