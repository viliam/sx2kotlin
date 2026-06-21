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
    UNKNOWN_OPERATOR,
    EXPECTED_PARAMETER,
    EXPECTED_COMMA,
    EXPECTED_DOT
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
