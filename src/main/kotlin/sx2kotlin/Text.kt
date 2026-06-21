package sx2kotlin

import sx2kotlin.words.BracketKind
import sx2kotlin.words.DotKind
import sx2kotlin.words.OperatorGroup
import sx2kotlin.words.SeparatorKind

class Text(private val lines: List<String>) {
    var position: Position = Position(0, 0)

    val line: String
        get() = lines[position.y]

    fun lineAt(row: Int): String {
        return if (row in lines.indices) lines[row] else ""
    }

    fun nextCharPosition(): Position? {
        var x = position.x
        var y = position.y

        while (y < lines.size) {
            val currentLine = lines[y]
            val tail = currentLine.substring(x)
            val lstripCount = tail.length - tail.trimStart().length
            
            if (tail.trimStart().isNotEmpty()) {
                x += lstripCount
                break
            } else {
                x = 0
                y++
            }
        }

        if (!isValidPosition(x, y)) {
            return null
        }

        position = Position(x, y)
        return position
    }

    fun isValidPosition(x: Int, y: Int): Boolean {
        if (y !in lines.indices) return false
        if (x !in lines[y].indices) return false
        return true
    }

    private fun nextChar(lambdaNextChar: () -> Position?): Char {
        val actual = Position.create(position)
        lambdaNextChar()
        ensureNotEof()

        val char = lines[position.y][position.x]
        position = actual
        return char
    }

    fun nextChar(): Char {
        return nextChar { nextCharPosition() }
    }

    fun lookAhead(): String {
        val currentLine = line
        val x = position.x
        val endX = findEndOfWord(currentLine, x)
        return if (x < endX) currentLine.substring(x, endX) else ""
    }

    fun takeEndOfLine(): String {
        ensureNotEof()
        return lines[position.y].substring(position.x)
    }

    fun isEndOfFile(): Boolean {
        val currentPos = position
        val res = nextCharPosition() == null
        position = currentPos
        return res
    }

    fun ensureNotEof() {
        if (isEndOfFile()) {
            throw SxError.createNoMsg(SxErrorType.END_OF_FILE, position)
        }
    }

    fun isPrefixInt(): Boolean = nextChar().isDigit()

    fun isPrefixLetter(): Boolean {
        val c = nextChar()
        return c.isLetter() || c == '_'
    }

    fun isPrefixOperator(): Boolean {
        val a = nextChar().toString()
        return OperatorGroup.COMPARISON.isPrefix(a) ||
                OperatorGroup.ARITHMETIC.isPrefix(a) ||
                OperatorGroup.ASSIGNMENT.isPrefix(a) ||
                OperatorGroup.BOOLEAN.isPrefix(a)
    }

    fun isPrefixBracketOpen(): Boolean {
        val a = nextChar().toString()
        return BracketKind.ROUND_OPEN.isPrefix(a)
    }

    fun isPrefixBracketClosed(): Boolean {
        val a = nextChar().toString()
        return BracketKind.ROUND_CLOSE.isPrefix(a)
    }

    fun isPrefixSquareBracketOpen(): Boolean {
        val a = nextChar().toString()
        return BracketKind.SQUARE_OPEN.isPrefix(a)
    }

    fun isPrefixSquareBracketClose(): Boolean {
        val a = nextChar().toString()
        return BracketKind.SQUARE_CLOSE.isPrefix(a)
    }

    fun isPrefixComma(): Boolean {
        val a = nextChar().toString()
        return SeparatorKind.COMMA.isPrefix(a)
    }

    fun isPrefixDot(): Boolean {
        val a = nextChar().toString()
        return DotKind.DOT.isPrefix(a)
    }

    fun isPrefixVariable(): Boolean {
        return IsPrefix(this).check { word ->
            !ReservedWordEnum.isWord(word) && (isValidPosition(position.x, position.y) || !isPrefixBracketOpen())
        }
    }

    fun isPrefixCommandPostfix() = isPrefixBracketOpen()

    fun isPrefixArrayPostfix() = isPrefixSquareBracketOpen()


    fun isPrefixMemberAccessPostfix() = isPrefixDot()

//    fun isPrefixDataType(): Boolean {
//        return IsPrefix(this).check { word ->
//            ReservedWordGroupEnum.DATA_TYPE.contains(ReservedWordEnum.makeSymbol(word) ?: ReservedWordEnum.VOID)
//        }
//    }

    companion object {
        fun findEndOfWord(line: String, x: Int): Int {
            for (i in x until line.length) {
                if (!line[i].isLetterOrDigit()) return i
            }
            return line.length
        }
    }
}

class IsPrefix(private val text: Text) {
    fun check(isPrefix: (String) -> Boolean): Boolean {
        val pos = text.position
        return try {
            if (!text.isPrefixLetter()) {
                false
            } else {
                val word = text.lookAhead()
                isPrefix(word)
            }
        } finally {
            text.position = pos
        }
    }
}
