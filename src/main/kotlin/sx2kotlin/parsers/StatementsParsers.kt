package sx2kotlin.parsers

import sx2kotlin.Text
import sx2kotlin.startsWithArrayPostfix
import sx2kotlin.startsWithCommandPostfix
import sx2kotlin.startsWithMemberAccessPostfix
import sx2kotlin.words.Array
import sx2kotlin.words.Assignment
import sx2kotlin.words.Command
import sx2kotlin.words.AbstractExpression
import sx2kotlin.words.MemberAccess
import sx2kotlin.words.Statement

class StatementParser : SxParser<Statement> {
    companion object {
        private val _instance = StatementParser()
        fun i(): StatementParser = _instance
    }

    override fun read(text: Text): Statement {
        return AssignmentParser.i().read(text)
    }
}

class AssignmentParser : SxParser<Assignment> {
    companion object {
        private val _instance = AssignmentParser()
        fun i(): AssignmentParser = _instance
    }

    override fun read(text: Text): Assignment {
        var lvalue : AbstractExpression = VariableParser.i().read(text)
        lvalue = readPostfixes(text, lvalue)

        val assignSymbol = AssigmentSymbolParser.read(text)
        val expression = ExpressionParser.i().read(text)
        return Assignment(lvalue, assignSymbol, expression)
    }

    private fun readPostfixes(text: Text, baseExpression: AbstractExpression): AbstractExpression {
        var expr = baseExpression

        while (!text.isEndOfFile() && text.startsWithPostfix()) {
            expr = when {
                text.startsWithMemberAccessPostfix() ->
                    MemberAccess(expr, MemberAccessPostfixParser.i().read(text))
                else ->
                    Array(expr, ArrayPostfixParser.i().read(text))
            }
        }

        return expr
    }

    private fun Text.startsWithPostfix(): Boolean {
        return  startsWithMemberAccessPostfix() ||
                startsWithArrayPostfix()
    }
}