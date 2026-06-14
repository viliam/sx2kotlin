package sx2kotlin.words

import sx2kotlin.ExpType
import sx2kotlin.Position

abstract class ExpressionABC(position: Position) : WordABC(position) {
    abstract val expType: ExpType
}

abstract class WordExpression(word: Word) : ExpressionABC(word.position) {
    override val content: String = word.content
}

class Variable(word: Word) : WordExpression(word) {
    override val expType: ExpType get() = ExpType.UNKNOWN
}

class DataType(word: Word) : WordExpression(word) {
    override val expType: ExpType get() = ExpType.VOID
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


class Parameters(position: Position, val parameters: List<ExpressionABC>) : WordABC(position) {
    override val content: String get() = parameters.joinToString(",") { it.content }

}

class CommandPostfix(val bracketOpen: Bracket, val parameters: Parameters, val bracketClose: Bracket) : WordABC(bracketOpen.position) {
    override val content: String
        get() =  bracketOpen.content + parameters.content + bracketClose.content

}

class Command(val expression: ExpressionABC, val commandPostfix: CommandPostfix) : ExpressionABC(expression.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String get() = expression.content + commandPostfix.content    
}

class ArrayPostfix(val bracketOpen: Bracket, val elements: List<ExpressionABC>, val bracketClose: Bracket) : ExpressionABC(bracketOpen.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String
        get() = bracketOpen.content + elements.joinToString(",") { it.content } + bracketClose.content
}

class Array(val expression: ExpressionABC, val arrayPostfix: ArrayPostfix) : ExpressionABC(expression.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String get() = expression.content + arrayPostfix.content
}

class MemberAccessPostfix(val dot : Symbol, val name: Word): WordABC(dot.position) {
    override val content: String
        get() = dot.content + name

}

class MemberAccess(val expression: ExpressionABC, val memberAccessPostfix: MemberAccessPostfix): ExpressionABC(expression.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String get() = expression.content + memberAccessPostfix.content
}

