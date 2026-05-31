package sx2kotlin.words

import sx2kotlin.ExpType

abstract class ExpressionABC(position: sx2kotlin.Position) : WordABC(position) {
    abstract val expType: ExpType
}

abstract class WordExpression(word: Word) : ExpressionABC(word.position) {
    override val content: String = word.content
}

class Variable(word: Word) : WordExpression(word) {
    private var _expType: ExpType = ExpType.UNKNOWN
    override val expType: ExpType get() = _expType
}

class DataType(word: Word) : WordExpression(word) {
    private var _expType: ExpType = ExpType.VOID
    override val expType: ExpType get() = _expType
}

class Integer(val value: Int, word: Word) : WordExpression(word) {
    override val expType: ExpType get() = ExpType.INT
}

class Expression(val v1: ExpressionABC, val op: Operator, val v2: ExpressionABC) : ExpressionABC(v1.position) {
    override val expType: ExpType
        get() = listOf(v1.expType, v2.expType, op.expType).maxBy { it.metadata.priority }

    override val content: String get() = v1.content + op.content + v2.content
}

class BracketExpression(val z1: Bracket, val expression: ExpressionABC, val z2: Bracket) : ExpressionABC(z1.position) {
    override val expType: ExpType get() = expression.expType
    override val content: String get() = z1.content + expression.content + z2.content
}
