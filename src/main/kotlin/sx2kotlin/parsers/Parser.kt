package sx2kotlin.parsers

import sx2kotlin.Text
import sx2kotlin.words.WordABC

interface SxParser<out T : WordABC> {
    fun read(text: Text): T
}
