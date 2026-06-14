package sx2kotlin.parsers

import sx2kotlin.Position
import sx2kotlin.SxError
import sx2kotlin.SxErrorType
import sx2kotlin.SymbolEnum
import sx2kotlin.SymbolGroupEnum
import sx2kotlin.Text
import sx2kotlin.words.Bracket
import sx2kotlin.words.Comma
import sx2kotlin.words.Dot
import sx2kotlin.words.Operator
import sx2kotlin.words.Symbol

private class SymbolParser<out T : Symbol>(
    symbols: Set<SymbolEnum>,
    private val exceptionType: SxErrorType,
    private val create: (Position, SymbolEnum) -> T
) : SxParser<T> {

    private val symbols = symbols.sortedByDescending { it.value.length }

    override fun read(text: Text): T {
        try {
            val position = text.nextCharPosition() ?: text.position
            val symbol = takeSymbol(text)
            return create(position, symbol)
        } catch (ex: SxError) {
            throw SxError.create(ex.typ, text.position, text.line)
        }
    }

    private fun takeSymbol(text: Text): SymbolEnum {
        val remainingText = text.takeEndOfLine()
        val symbol = symbols.firstOrNull { remainingText.startsWith(it.value) }
            ?: throw SxError.create(exceptionType, text.position, text.line)

        text.position = text.position.addX(symbol.value.length)
        return symbol
    }
}

object BracketParser : SxParser<Bracket> by SymbolParser(
    SymbolGroupEnum.BRACKET.members,
    SxErrorType.EXPECTED_BRACKET,
    ::Bracket
)

object CommaParser : SxParser<Comma> by SymbolParser(
    SymbolGroupEnum.COMMAS.members,
    SxErrorType.EXPECTED_COMMA,
    ::Comma
)

object DotParser : SxParser<Dot> by SymbolParser(
    SymbolGroupEnum.DOTS.members,
    SxErrorType.EXPECTED_DOT,
    ::Dot
)

object OperatorExpressionParser : SxParser<Operator> by SymbolParser(
    SymbolGroupEnum.OP_EXP.members,
    SxErrorType.EXPECTED_OPERATOR,
    ::Operator
)
