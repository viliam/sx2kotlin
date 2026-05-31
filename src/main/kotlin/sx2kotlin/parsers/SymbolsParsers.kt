package sx2kotlin.parsers

import sx2kotlin.*
import sx2kotlin.words.*

abstract class SymbolAbstractParser<out T : Symbol> : SxParser<T> {

    protected open fun nextCharPosition(text: Text): Position? {
        return text.nextCharPosition()
    }

    override fun read(text: Text): T {
        try {
            val poz = nextCharPosition(text) ?: text.position
            val sSymbol = takeSymbol(text)
            val eSymbol = SymbolEnum.makeSymbol(sSymbol)
            return create(poz, eSymbol)
        } catch (ex: SxError) {
            throw SxError.create(ex.typ, text.position, text.line)
        }
    }

    abstract fun getSymbols(): Set<SymbolEnum>

    abstract fun create(position: Position, enum: SymbolEnum?): T

    abstract fun getExceptionType(): SxErrorType

    private fun takeSymbol(text: Text): String {
        val s = text.takeEndOfLine()
        val symbols = getSymbols().map { it.value }.sortedByDescending { it.length }

        for (sym in symbols) {
            if (s.startsWith(sym)) {
                text.position = text.position.addX(sym.length)
                return sym
            }
        }

        throw SxError.create(getExceptionType(), text.position, text.line)
    }
}

class BracketParser : SymbolAbstractParser<Bracket>() {
    companion object {
        private val _instance = BracketParser()
        fun i(): BracketParser = _instance
    }

    override fun getSymbols(): Set<SymbolEnum> = SymbolGroupEnum.BRACKET.members

    override fun create(position: Position, enum: SymbolEnum?): Bracket {
        if (enum == null) throw SxError.create(getExceptionType(), position, "")
        return Bracket(position, enum)
    }

    override fun getExceptionType(): SxErrorType = SxErrorType.EXPECTED_BRACKET
}

class OperatorExpressionParser : SymbolAbstractParser<Operator>() {
    companion object {
        private val _instance = OperatorExpressionParser()
        fun i(): OperatorExpressionParser = _instance
    }

    override fun nextCharPosition(text: Text): Position? {
        return text.nextCharPosition()
    }

    override fun getSymbols(): Set<SymbolEnum> = SymbolGroupEnum.OP_EXP.members

    override fun create(position: Position, enum: SymbolEnum?): Operator {
        if (enum == null) throw SxError.create(getExceptionType(), position, "")
        return Operator(position, enum)
    }

    override fun getExceptionType(): SxErrorType = SxErrorType.EXPECTED_OPERATOR
}
