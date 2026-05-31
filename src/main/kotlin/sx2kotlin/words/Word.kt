package sx2kotlin.words

import sx2kotlin.Position

abstract class WordABC(val position: Position) {
    abstract val content: String
}

open class Word(position: Position, override val content: String) : WordABC(position) {
    override fun toString(): String = content

    companion object {
        fun fromWord(word: Word): Word = Word(word.position, word.content)
    }
}
