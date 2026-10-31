package sx2kotlin

import sx2kotlin.words.BracketKind
import sx2kotlin.words.DotKind
import sx2kotlin.words.OperatorGroup
import sx2kotlin.words.SeparatorKind

fun Text.startsWithInt(): Boolean = nextChar().isDigit()

fun Text.startsWithLetter(): Boolean {
    val c = nextChar()
    return c.isLetter() || c == '_'
}

fun Text.startsWithOperator(): Boolean {
    val a = nextChar().toString()
    return OperatorGroup.COMPARISON.isPrefix(a) ||
            OperatorGroup.ARITHMETIC.isPrefix(a) ||
            OperatorGroup.ASSIGNMENT.isPrefix(a) ||
            OperatorGroup.BOOLEAN.isPrefix(a)
}

fun Text.startsWithBracketOpen(): Boolean {
    val a = nextChar().toString()
    return BracketKind.ROUND_OPEN.isPrefix(a)
}

fun Text.startsWithBracketClosed(): Boolean {
    val a = nextChar().toString()
    return BracketKind.ROUND_CLOSE.isPrefix(a)
}

fun Text.startsWithSquareBracketOpen(): Boolean {
    val a = nextChar().toString()
    return BracketKind.SQUARE_OPEN.isPrefix(a)
}

fun Text.startsWithSquareBracketClose(): Boolean {
    val a = nextChar().toString()
    return BracketKind.SQUARE_CLOSE.isPrefix(a)
}

fun Text.startsWithComma(): Boolean {
    val a = nextChar().toString()
    return SeparatorKind.COMMA.isPrefix(a)
}

fun Text.startsWithDot(): Boolean {
    val a = nextChar().toString()
    return DotKind.DOT.isPrefix(a)
}

fun Text.startsWithVariable(): Boolean {
    return StartsWith(this).check { word ->
        !ReservedWordEnum.isWord(word) && (isValidPosition(position.x, position.y) || !startsWithBracketOpen())
    }
}

fun Text.startsWithCommandPostfix() = startsWithBracketOpen()

fun Text.startsWithArrayPostfix() = startsWithSquareBracketOpen()

fun Text.startsWithMemberAccessPostfix() = startsWithDot()

//fun Text.startsWithDataType(): Boolean {
//    return StartsWith(this).check { word ->
//        ReservedWordGroupEnum.DATA_TYPE.contains(ReservedWordEnum.makeSymbol(word) ?: ReservedWordEnum.VOID)
//    }
//}

private class StartsWith(private val text: Text) {
    fun check(startsWith: (String) -> Boolean): Boolean {
        val pos = text.position
        return try {
            if (!text.startsWithLetter()) {
                false
            } else {
                val word = text.peekWord()
                startsWith(word)
            }
        } finally {
            text.position = pos
        }
    }
}
