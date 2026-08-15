package sx2kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestText {

    @Test
    fun testNextCharPositionSimpleText() {
        val text = Text(listOf("Hello", "World"))
        val result = text.nextCharPosition()
        assertNotNull(result)
        assertEquals(0, result?.x)
        assertEquals(0, result?.y)
    }

    @Test
    fun testNextCharPositionEmptyText() {
        val text = Text(emptyList())
        val result = text.nextCharPosition()
        assertNull(result)
    }

    @Test
    fun testNextCharPositionWhitespaceText() {
        val text = Text(listOf("   ", "  ", "Hello"))
        val result = text.nextCharPosition()
        assertNotNull(result)
        assertEquals(0, result?.x)
        assertEquals(2, result?.y)
    }

    @Test
    fun testIsValidPositionIncorrect() {
        val text = Text(listOf("la"))
        assertFalse(text.isValidPosition(0, -1))
        assertFalse(text.isValidPosition(-1, 0))
        assertFalse(text.isValidPosition(0, 2))
        assertFalse(text.isValidPosition(2, 0))
    }

    @Test
    fun testIsValidPosition() {
        val text1 = Text(listOf("la"))
        assertTrue(text1.isValidPosition(0, 0))
        val text2 = Text(listOf("la", "lo"))
        assertTrue(text2.isValidPosition(1, 1))
    }

    @Test
    fun testNextCharSimpleText() {
        val text = Text(listOf("    Hello", "World"))
        val result = text.nextChar()
        assertEquals('H', result)
        assertEquals(0, text.position.x)
        assertEquals(0, text.position.y)
    }

    @Test
    fun testNextCharEmptyText() {
        val text = Text(emptyList())
        val exception = assertThrows<SxError> {
            text.nextChar()
        }
        assertEquals(SxErrorType.END_OF_FILE, exception.typ)
    }

    @Test
    fun testPeekWordFindsWord() {
        val text = Text(listOf("TestWord example"))
        val result = text.peekWord()
        assertEquals("TestWord", result)
    }

    @Test
    fun testPeekWordEmptyString() {
        val text = Text(listOf(""))
        val result = text.peekWord()
        assertEquals("", result)
    }

    @Test
    fun testPeekWordStartIndexBeyondLength() {
        val text = Text(listOf("ShortWord"))
        text.position = Position("ShortWord".length, 0)
        val result = text.peekWord()
        assertEquals("", result)
    }

    @Test
    fun testPeekWordNonWordCharacter() {
        val text = Text(listOf("Test1@Word"))
        text.position = Position(5, 0)
        val result = text.peekWord()
        assertEquals("", result)
    }

    @Test
    fun testFindEndOfWordCorrectIndex() {
        val line = "OpenAI Language Model"
        val result = Text.findEndOfWord(line, 7)
        assertEquals(15, result)
    }

    @Test
    fun testFindEndOfWordNoWordCharacters() {
        val line = "!@#$%^&*"
        val result = Text.findEndOfWord(line, 0)
        assertEquals(0, result)
    }

    @Test
    fun testFindEndOfWordIndexOutOfBounds() {
        val line = "TextExample"
        val result = Text.findEndOfWord(line, line.length)
        assertEquals(line.length, result)
    }
}
