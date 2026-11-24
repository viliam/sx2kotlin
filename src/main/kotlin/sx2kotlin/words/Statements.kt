package sx2kotlin.words

import sx2kotlin.ExpType
import sx2kotlin.Position

abstract class Statement(position: Position) : AbstractWord(position) {
    abstract val expType: ExpType
}

class Assignment(
    val lvalue: AbstractExpression,
    val assignmentSymbol: AssignmentSymbol,
    val expression: AbstractExpression
)  : Statement(lvalue.position) {
    override val content: String
        get() = lvalue.content + assignmentSymbol.content + expression.content
    override val expType: ExpType
        get() = ExpType.VOID
}


class DekVariable(word: Word) {



}