package sx2kotlin

enum class SxErrorType {
    UNCORRECTED_END_OF_EXPRESSION,
    END_OF_FILE,
    EXPECTED_INT,
    UNEXPECTED_PREFIX,
    EXPECTED_DATA_TYPE,
    EXPECTED_BRACKET,
    EXPECTED_OPERATOR,
    EXPECTED_TOKEN,
    UNKNOWN_OPERATOR
}

class ExpTypeWithPriority(val priority: Int)

enum class ExpType(val metadata: ExpTypeWithPriority) {
    VOID(ExpTypeWithPriority(0)),
    INT(ExpTypeWithPriority(1)),
    BOOL(ExpTypeWithPriority(3)),
    COMPARISON(ExpTypeWithPriority(2)),
    UNKNOWN(ExpTypeWithPriority(-1))
}

enum class ReservedWordEnum(val value: String) {
    INT("int"),
    BOOL("bool"),
    RETURN("return"),
    IF("if"),
    VOID("void"),
    TRUE("true"),
    FALSE("false"),
    AND("and"),
    OR("or");

    override fun toString(): String = value

    companion object {
        fun makeSymbol(reservedWord: String): ReservedWordEnum? {
            return entries.find { it.value == reservedWord }
        }

        fun isWord(value: String): Boolean {
            return entries.any { it.value == value }
        }
    }
}

enum class ReservedWordGroupEnum(val members: Set<ReservedWordEnum>) {
    DATA_TYPE(setOf(ReservedWordEnum.INT, ReservedWordEnum.BOOL)),
    INSTRUCTION_WORD(setOf(ReservedWordEnum.RETURN, ReservedWordEnum.IF)),
    DATA_VALUE(setOf(ReservedWordEnum.VOID, ReservedWordEnum.TRUE, ReservedWordEnum.FALSE));

    fun contains(word: ReservedWordEnum): Boolean = members.contains(word)
}

enum class SymbolEnum(val value: String) {
    // ARITM
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDE("/"),
    MODULO("%"),
    FLOOR_DIVISION("//"),
    EXPONENT("**"),

    // BOOL
    AND("and"),
    OR("or"),
    AND_STRONG("&&"),
    OR_STRONG("||"),

    // COMPARISON
    SMALLER("<"),
    GREATER(">"),
    SMALLER_EQUAL("<="),
    GRATER_EQUAL(">="),
    EQUAL("=="),
    UNEQUAL("!="),

    ASSIGN("="),

    BRACKET_NORM_OPEN("("),
    BRACKET_NORM_CLOSE(")"),
    PARENTHESIS_BLOCK_OPEN("{"),
    PARENTHESIS_BLOCK_CLOSE("}"),

    COMMA(","),
    SEMICOLON(";"),
    DOT(","); // Note: in Python it was DOT = "," which seems weird but I'll keep it. 

    val symbol: String get() = value

    override fun toString(): String = value

    fun isPrefix(a: String): Boolean {
        if (a.isEmpty()) return false
        return value[0] == a[0]
    }

    companion object {
        fun makeSymbol(s: String): SymbolEnum? {
            return entries.find { it.value == s }
        }
    }
}

enum class SymbolGroupEnum(val members: Set<SymbolEnum>) {
    OP_ARITH(setOf(
        SymbolEnum.PLUS, SymbolEnum.MINUS, SymbolEnum.TIMES,
        SymbolEnum.MODULO, SymbolEnum.DIVIDE, SymbolEnum.FLOOR_DIVISION,
        SymbolEnum.EXPONENT
    )),
    OP_BOOL(setOf(SymbolEnum.AND, SymbolEnum.AND_STRONG, SymbolEnum.OR, SymbolEnum.OR_STRONG)),
    OP_COMPARISON(setOf(
        SymbolEnum.SMALLER, SymbolEnum.SMALLER_EQUAL, SymbolEnum.GREATER,
        SymbolEnum.GRATER_EQUAL, SymbolEnum.EQUAL, SymbolEnum.UNEQUAL
    )),
    OP_EXP(setOf(
        SymbolEnum.PLUS, SymbolEnum.MINUS, SymbolEnum.TIMES,
        SymbolEnum.MODULO, SymbolEnum.DIVIDE, SymbolEnum.FLOOR_DIVISION,
        SymbolEnum.EXPONENT, SymbolEnum.AND,
        SymbolEnum.AND_STRONG, SymbolEnum.OR, SymbolEnum.OR_STRONG,
        SymbolEnum.SMALLER, SymbolEnum.SMALLER_EQUAL, SymbolEnum.GREATER,
        SymbolEnum.GRATER_EQUAL, SymbolEnum.EQUAL, SymbolEnum.UNEQUAL
    )),
    OP_ASSIGNMENT(setOf(SymbolEnum.ASSIGN)),
    COMMAS(setOf(SymbolEnum.COMMA, SymbolEnum.SEMICOLON, SymbolEnum.DOT)),
    BRACKET(setOf(
        SymbolEnum.BRACKET_NORM_OPEN, SymbolEnum.BRACKET_NORM_CLOSE,
        SymbolEnum.PARENTHESIS_BLOCK_OPEN, SymbolEnum.PARENTHESIS_BLOCK_CLOSE
    ));

    fun contains(word: SymbolEnum): Boolean = members.contains(word)

    fun isPrefix(a: String): Boolean {
        return members.any { it.isPrefix(a) }
    }
}
