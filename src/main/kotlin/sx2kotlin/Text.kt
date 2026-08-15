package sx2kotlin

class Text(private val lines: List<String>) {
    var position: Position = Position(0, 0)

    val line: String
        get() = lines[position.y]

    fun nextCharPosition(): Position? {
        var y = position.y
        var x = position.x

        while (y in lines.indices) {
            val offset = nextCharPositionAtLine(x, y)

            if (offset != -1) {
                position = Position(x + offset, y)
                return position
            }

            y++
            x = 0
        }

        return null
    }

    private fun nextCharPositionAtLine(x: Int, y: Int): Int = lines[y]
        .drop(x)
        .indexOfFirst { !it.isWhitespace() }

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

    fun peekWord(): String {
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

    companion object {
        fun findEndOfWord(line: String, x: Int): Int {
            for (i in x until line.length) {
                if (!line[i].isLetterOrDigit()) return i
            }
            return line.length
        }
    }
}
