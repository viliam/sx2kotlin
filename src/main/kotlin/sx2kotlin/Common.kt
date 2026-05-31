package sx2kotlin

class SxError(
    val typ: SxErrorType,
    message: String,
    val position: Position?
) : Exception(message) {

    override fun toString(): String {
        val base = super.message ?: ""
        return if (position != null) {
            "$base  \n row = ${position.y} ,   column = ${position.x}"
        } else {
            base
        }
    }

    companion object {
        fun createNoMsg(typ: SxErrorType, position: Position): SxError {
            return SxError(typ, typ.name, position)
        }

        fun create(typ: SxErrorType, position: Position, line: String): SxError {
            return createWithMessage(typ, null, position, line)
        }

        fun createWithMessage(typ: SxErrorType, message: String?, position: Position, line: String): SxError {
            val finalMessage = message ?: makeMessage(typ, position, line)
            return SxError(typ, finalMessage, position)
        }

        fun createWithPosition(typ: SxErrorType, position: Position): SxError {
            return SxError(typ, typ.name, position)
        }

        fun makeMessage(typ: SxErrorType, position: Position, line: String): String {
            val col = position.x
            val aChar = if (line.length > col) line[col] else ' '
            return "${typ.name}  : $line    \n char = $aChar"
        }
    }
}
