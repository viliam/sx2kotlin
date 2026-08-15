package sx2kotlin

import sx2kotlin.words.BracketKind
import sx2kotlin.words.DotKind
import sx2kotlin.words.OperatorGroup
import sx2kotlin.words.SeparatorKind

fun Text.isPrefixInt(): Boolean = nextChar().isDigit()

fun Text.isPrefixLetter(): Boolean {
    val c = nextChar()
    return c.isLetter() || c == '_'
}

fun Text.isPrefixOperator(): Boolean {
    val a = nextChar().toString()
    return OperatorGroup.COMPARISON.isPrefix(a) ||
            OperatorGroup.ARITHMETIC.isPrefix(a) ||
            OperatorGroup.ASSIGNMENT.isPrefix(a) ||
            OperatorGroup.BOOLEAN.isPrefix(a)
}

fun Text.isPrefixBracketOpen(): Boolean {
    val a = nextChar().toString()
    return BracketKind.ROUND_OPEN.isPrefix(a)
}

fun Text.isPrefixBracketClosed(): Boolean {
    val a = nextChar().toString()
    return BracketKind.ROUND_CLOSE.isPrefix(a)
}

fun Text.isPrefixSquareBracketOpen(): Boolean {
    val a = nextChar().toString()
    return BracketKind.SQUARE_OPEN.isPrefix(a)
}

fun Text.isPrefixSquareBracketClose(): Boolean {
    val a = nextChar().toString()
    return BracketKind.SQUARE_CLOSE.isPrefix(a)
}

fun Text.isPrefixComma(): Boolean {
    val a = nextChar().toString()
    return SeparatorKind.COMMA.isPrefix(a)
}

fun Text.isPrefixDot(): Boolean {
    val a = nextChar().toString()
    return DotKind.DOT.isPrefix(a)
}

fun Text.isPrefixVariable(): Boolean {
    return IsPrefix(this).check { word ->
        !ReservedWordEnum.isWord(word) && (isValidPosition(position.x, position.y) || !isPrefixBracketOpen())
    }
}

fun Text.isPrefixCommandPostfix() = isPrefixBracketOpen()

fun Text.isPrefixArrayPostfix() = isPrefixSquareBracketOpen()

fun Text.isPrefixMemberAccessPostfix() = isPrefixDot()

//fun Text.isPrefixDataType(): Boolean {
//    return IsPrefix(this).check { word ->
//        ReservedWordGroupEnum.DATA_TYPE.contains(ReservedWordEnum.makeSymbol(word) ?: ReservedWordEnum.VOID)
//    }
//}

private class IsPrefix(private val text: Text) {
    fun check(isPrefix: (String) -> Boolean): Boolean {
        val pos = text.position
        return try {
            if (!text.isPrefixLetter()) {
                false
            } else {
                val word = text.peekWord()
                isPrefix(word)
            }
        } finally {
            text.position = pos
        }
    }
}
