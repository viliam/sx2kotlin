package sx2kotlin.parsers

import sx2kotlin.*
import sx2kotlin.words.Word

open class WordParser : SxParser<Word> {
    
    companion object {
        private val _instance = WordParser()
        fun i(): WordParser = _instance
    }

    protected open fun nextCharPosition(text: Text) {
        text.nextCharPosition()
    }

    override fun read(text: Text): Word {
        nextCharPosition(text)
        val pos = text.position
        val wordContent = text.peekWord()

        if (wordContent.isEmpty()) {
            throw SxError.create(SxErrorType.EXPECTED_TOKEN, text.position, text.line)
        }

        val endX = pos.x + wordContent.length
        text.position = Position(endX, pos.y)
        return Word(pos, wordContent)
    }
}

class WordExpressionParser : WordParser() {
    
    companion object {
        private val _instance = WordExpressionParser()
        fun i(): WordExpressionParser = _instance
    }

    override fun nextCharPosition(text: Text) {
        text.nextCharPosition()
    }
}
