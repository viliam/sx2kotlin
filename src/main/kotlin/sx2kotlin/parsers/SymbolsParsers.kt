package sx2kotlin.parsers

import sx2kotlin.Position
import sx2kotlin.SxError
import sx2kotlin.SxErrorType
import sx2kotlin.Text
import sx2kotlin.words.Bracket
import sx2kotlin.words.BracketKind
import sx2kotlin.words.CloseBracket
import sx2kotlin.words.Dot
import sx2kotlin.words.DotKind
import sx2kotlin.words.OpenBracket
import sx2kotlin.words.Operator
import sx2kotlin.words.OperatorKind
import sx2kotlin.words.Separator
import sx2kotlin.words.SeparatorKind
import sx2kotlin.words.Symbol
import sx2kotlin.words.SymbolKind

private class SymbolParser<out T : Symbol, K : SymbolKind>(
    symbols: Set<K>,
    private val exceptionType: SxErrorType,
    private val create: (Position, K) -> T
) : SxParser<T> {

    private val symbolsMap : Map<String, K> = symbols.associateBy { it.token }
    private val symbolsToken = symbolsMap.keys.sortedByDescending { it.length }

    override fun read(text: Text): T {
        try {
            val position = text.nextCharPosition() ?: text.position
            val symbol = takeSymbol(text)
            return create(position, symbol)
        } catch (ex: SxError) {
            throw SxError.create(ex.typ, text.position, text.line)
        }
    }

    private fun takeSymbol(text: Text): K {
        val remainingText = text.takeEndOfLine()
        val symbolToken = symbolsToken.firstOrNull { remainingText.startsWith(it) }
            ?: throw SxError.create(exceptionType, text.position, text.line)

        text.position = text.position.addX(symbolToken.length)
        return symbolsMap.getValue(symbolToken)
    }
}

object BracketParser : SxParser<Bracket> by SymbolParser(
    BracketKind.entries.toSet(),
    SxErrorType.EXPECTED_BRACKET,
    { position, kind ->
        when (kind) {
            BracketKind.ROUND_OPEN,
            BracketKind.SQUARE_OPEN,
            BracketKind.CURLY_OPEN -> OpenBracket(position, kind)

            BracketKind.ROUND_CLOSE,
            BracketKind.SQUARE_CLOSE,
            BracketKind.CURLY_CLOSE -> CloseBracket(position, kind)
        }
    }
)

object CommaParser : SxParser<Separator> by SymbolParser(
    setOf(SeparatorKind.COMMA),
    SxErrorType.EXPECTED_COMMA,
    ::Separator
)

object DotParser : SxParser<Dot> by SymbolParser(
    DotKind.entries.toSet(),
    SxErrorType.EXPECTED_DOT,
    ::Dot
)

object OperatorExpressionParser : SxParser<Operator> by SymbolParser(
    OperatorKind.entries.toSet(),
    SxErrorType.EXPECTED_OPERATOR,
    ::Operator
)
