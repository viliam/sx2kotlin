package sx2kotlin.words

import sx2kotlin.*

open class Symbol(position: Position, val symbolEnum: SymbolEnum) : WordABC(position) {
    override val content: String get() = symbolEnum.symbol
}

class Bracket(position: Position, symbolEnum: SymbolEnum) : Symbol(position, symbolEnum)

class Operator(position: Position, symbolEnum: SymbolEnum) : Symbol(position, symbolEnum) {
    val expType: ExpType
        get() = when (symbolEnum) {
            SymbolEnum.PLUS, SymbolEnum.MINUS, SymbolEnum.TIMES, SymbolEnum.MODULO,
            SymbolEnum.DIVIDE, SymbolEnum.FLOOR_DIVISION, SymbolEnum.EXPONENT -> ExpType.INT

            SymbolEnum.AND, SymbolEnum.OR, SymbolEnum.AND_STRONG, SymbolEnum.OR_STRONG -> ExpType.BOOL

            SymbolEnum.SMALLER, SymbolEnum.GREATER, SymbolEnum.SMALLER_EQUAL,
            SymbolEnum.GRATER_EQUAL, SymbolEnum.EQUAL, SymbolEnum.UNEQUAL -> ExpType.COMPARISON

            SymbolEnum.ASSIGN -> ExpType.UNKNOWN
            else -> throw SxError.createWithMessage(SxErrorType.UNKNOWN_OPERATOR, symbolEnum.toString(), position, "")
        }
}
