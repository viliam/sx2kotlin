package sx2kotlin.words

import sx2kotlin.*

sealed class Symbol(
    position: Position,
    final override val content: String
) : AbstractWord(position)

interface SymbolKind {
    val token: String

    fun isPrefix(value: String): Boolean =
        value.isNotEmpty() && token.startsWith(value)
}   

enum class DotKind(
    override val token: String
) : SymbolKind {
    DOT(".")
}

class Dot(
    position: Position,
    val kind: DotKind
) : Symbol(position, kind.token)


enum class SeparatorKind(
    override val token: String
) : SymbolKind {
    COMMA(","),
    SEMICOLON(";")
}

open class Separator(
    position: Position,
    val kind: SeparatorKind
) : Symbol(position, kind.token)


enum class OperatorGroup(val expType: ExpType) {
    ARITHMETIC(ExpType.INT),
    BOOLEAN(ExpType.BOOL),
    COMPARISON(ExpType.COMPARISON),
    ASSIGNMENT(ExpType.UNKNOWN);

    fun isPrefix(token: String): Boolean =
        OperatorKind.entries.any {
            it.group == this && it.isPrefix(token)
        }
}

enum class OperatorKind(
    override val token: String,
    val group: OperatorGroup
) : SymbolKind {
    PLUS("+", OperatorGroup.ARITHMETIC),
    MINUS("-", OperatorGroup.ARITHMETIC),
    TIMES("*", OperatorGroup.ARITHMETIC),
    DIVIDE("/", OperatorGroup.ARITHMETIC),
    MODULO("%", OperatorGroup.ARITHMETIC),
    FLOOR_DIVISION("//", OperatorGroup.ARITHMETIC),
    EXPONENT("**", OperatorGroup.ARITHMETIC),

    AND("and", OperatorGroup.BOOLEAN),
    OR("or", OperatorGroup.BOOLEAN),
    AND_WEAK("&", OperatorGroup.BOOLEAN),
    OR_WEAK("|", OperatorGroup.BOOLEAN),
    AND_STRONG("&&", OperatorGroup.BOOLEAN),
    OR_STRONG("||", OperatorGroup.BOOLEAN),

    EQUAL("==", OperatorGroup.COMPARISON),
    NOT_EQUAL("!=", OperatorGroup.COMPARISON),
    SMALLER("<", OperatorGroup.COMPARISON),
    GREATER(">", OperatorGroup.COMPARISON),
    SMALLER_EQUAL("<=", OperatorGroup.COMPARISON),
    GRATER_EQUAL(">=", OperatorGroup.COMPARISON),

    ASSIGN("=", OperatorGroup.ASSIGNMENT)
}

class Operator(
    position: Position,
    val kind: OperatorKind
) : Symbol(position, kind.token) {
    val expType: ExpType get() = kind.group.expType
}



sealed class Bracket(
    position: Position,
    val kind: BracketKind
) : Symbol(position, kind.token)

class OpenBracket(
    position: Position,
    kind: BracketKind
) : Bracket(position, kind)

class CloseBracket(
    position: Position,
    kind: BracketKind
) : Bracket(position, kind)

enum class BracketKind(
    override val token: String
) : SymbolKind {
    ROUND_OPEN("("),
    ROUND_CLOSE(")"),
    SQUARE_OPEN("["),
    SQUARE_CLOSE("]"),
    CURLY_OPEN("{"),
    CURLY_CLOSE("}")
}

