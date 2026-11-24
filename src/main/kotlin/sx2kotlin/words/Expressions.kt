package sx2kotlin.words

import sx2kotlin.ExpType
import sx2kotlin.Position

abstract class AbstractExpression(position: Position) : AbstractWord(position) {
    abstract val expType: ExpType
}

abstract class WordExpression(word: Word) : AbstractExpression(word.position) {
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

class Expression(val v1: AbstractExpression, val op: Operator, val v2: AbstractExpression) : AbstractExpression(v1.position) {
    override val expType: ExpType
        get() = listOf(v1.expType, v2.expType, op.expType).maxBy { it.metadata.priority }

    override val content: String get() = v1.content + op.content + v2.content
}

class BracketExpression(val z1: Bracket, val expression: AbstractExpression, val z2: Bracket) : AbstractExpression(z1.position) {
    override val expType: ExpType get() = expression.expType
    override val content: String get() = z1.content + expression.content + z2.content
}


class Parameters(position: Position, val parameters: List<AbstractExpression>) : AbstractWord(position) {
    override val content: String get() = parameters.joinToString(",") { it.content }

}

class CommandPostfix(val bracketOpen: Bracket, val parameters: Parameters, val bracketClose: Bracket) : AbstractWord(bracketOpen.position) {
    override val content: String
        get() =  bracketOpen.content + parameters.content + bracketClose.content

}

class Command(val expression: AbstractExpression, val commandPostfix: CommandPostfix) : AbstractExpression(expression.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String get() = expression.content + commandPostfix.content    
}

class ArrayPostfix(val bracketOpen: Bracket, val elements: List<AbstractExpression>, val bracketClose: Bracket) : AbstractExpression(bracketOpen.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String
        get() = bracketOpen.content + elements.joinToString(",") { it.content } + bracketClose.content
}

class Array(val expression: AbstractExpression, val arrayPostfix: ArrayPostfix) : AbstractExpression(expression.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String get() = expression.content + arrayPostfix.content
}

class MemberAccessPostfix(val dot : Symbol, val name: Word): AbstractWord(dot.position) {
    override val content: String
        get() = dot.content + name

}

class MemberAccess(val expression: AbstractExpression, val memberAccessPostfix: MemberAccessPostfix): AbstractExpression(expression.position) {
    override val expType: ExpType get() = ExpType.UNKNOWN
    override val content: String get() = expression.content + memberAccessPostfix.content
}

