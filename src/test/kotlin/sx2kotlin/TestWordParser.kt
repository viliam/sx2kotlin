package sx2kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sx2kotlin.parsers.SxParser
import sx2kotlin.parsers.WordParser

class PositiveWordTest(
    val lines: List<String>,
    val position: Position = Position(0, 0),
    val expectedPosition: Position,
    val expectedWordPosition: Position = Position(0, 0),
    val expectedWordContent: String,
    val parserProvider: () -> SxParser<*>
) {
    fun doTheTest() {
        val text = Text(lines)
        text.position = position
        val word = parserProvider().read(text)

        assertEquals(expectedPosition, text.position)
        assertEquals(expectedWordContent, word.content)
        assertEquals(expectedWordPosition, word.position)
    }
}

class TestWordParser {

    @Test
    fun testReadSingleWord() {
        PositiveWordTest(
            lines = listOf("Hello"),
            expectedPosition = Position(5, 0),
            expectedWordContent = "Hello",
            parserProvider = { WordParser.i() }
        ).doTheTest()
    }

    @Test
    fun testReadMultipleWords() {
        PositiveWordTest(
            lines = listOf("Hello World"),
            expectedPosition = Position(5, 0),
            expectedWordContent = "Hello",
            parserProvider = { WordParser.i() }
        ).doTheTest()
    }

    @Test
    fun testReadSecondWord() {
        PositiveWordTest(
            lines = listOf("Hello World"),
            position = Position(5, 0),
            expectedPosition = Position(11, 0),
            expectedWordPosition = Position(6, 0),
            expectedWordContent = "World",
            parserProvider = { WordParser.i() }
        ).doTheTest()
    }

    @Test
    fun testReadHandlesLeadingWhitespace() {
        PositiveWordTest(
            lines = listOf("   Hello"),
            expectedPosition = Position(8, 0),
            expectedWordPosition = Position(3, 0),
            expectedWordContent = "Hello",
            parserProvider = { WordParser.i() }
        ).doTheTest()
    }

    @Test
    fun testReadEmptyText() {
        val text = Text(listOf(""))
        val exception = assertThrows<SxError> {
            WordParser.i().read(text)
        }
        assertEquals(SxErrorType.EXPECTED_TOKEN, exception.typ)
    }
}
