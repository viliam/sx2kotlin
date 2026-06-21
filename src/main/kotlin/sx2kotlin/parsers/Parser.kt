package sx2kotlin.parsers

import sx2kotlin.Text
import sx2kotlin.words.AbstractWord

interface SxParser<out T : AbstractWord> {
    fun read(text: Text): T
}
