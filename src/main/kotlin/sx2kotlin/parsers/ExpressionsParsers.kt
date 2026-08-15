package sx2kotlin.parsers

import sx2kotlin.*
import sx2kotlin.words.*

class IntegerParser : SxParser<Integer> {
    companion object {
        private val _instance = IntegerParser()
        fun i(): IntegerParser = _instance
    }

    override fun read(text: Text): Integer {
        val intWord = WordExpressionParser.i().read(text)
        val aInt = intWord.content
        if (!aInt.all { it.isDigit() }) {
            throw SxError.create(SxErrorType.EXPECTED_INT, text.position, text.line)
        }
        return Integer(aInt.toInt(), intWord)
    }
}

class VariableParser : SxParser<Variable> {
    companion object {
        private val _instance = VariableParser()
        fun i(): VariableParser = _instance
    }

    override fun read(text: Text): Variable {
        val name = WordExpressionParser.i().read(text)
        return Variable(name)
    }
}

class SimpleExpressionParser : SxParser<ExpressionAbstract> {
    companion object {
        private val _instance = SimpleExpressionParser()
        fun i(): SimpleExpressionParser = _instance
    }

    override fun read(text: Text): ExpressionAbstract {
        return when {
            text.isPrefixInt() -> IntegerParser.i().read(text)
            text.isPrefixVariable() -> VariableParser.i().read(text)
            else -> throw SxError.create(SxErrorType.UNEXPECTED_PREFIX, text.position, text.line)
        }
    }
}

class ExpressionParser : SxParser<ExpressionAbstract> {
    companion object {
        private val _instance = ExpressionParser()
        fun i(): ExpressionParser = _instance
    }

    override fun read(text: Text): ExpressionAbstract {
        var expr = if (text.isPrefixBracketOpen()) {
            BracketExpressionParser.i().read(text)
        } else if (text.isPrefixSquareBracketOpen()) {
            ArrayPostfixParser.i().read(text)
        } else {
            SimpleExpressionParser.i().read(text)
        }

        while (!text.isEndOfFile() && (
                    text.isPrefixCommandPostfix() ||
                    text.isPrefixMemberAccessPostfix() ||
                    text.isPrefixArrayPostfix()
                ))
            expr = when {
                text.isPrefixCommandPostfix() ->
                    Command(expr, CommandPostfixParser.i().read(text))
                text.isPrefixMemberAccessPostfix() ->
                    MemberAccess(expr, MemberAccessPostfixParser.i().read(text))
                else ->
                    Array(expr, ArrayPostfixParser.i().read(text))
            }


        if (text.isEndOfFile()) {
            return expr
        }

        if (text.isPrefixOperator()) {
            val op = OperatorExpressionParser.read(text)
            return Expression(expr, op, i().read(text))
        }

        if (!text.isEndOfFile() &&
            !text.isPrefixOperator() &&
            !text.isPrefixComma() &&
            !text.isPrefixBracketClosed() &&
            !text.isPrefixSquareBracketClose() &&
            !text.isPrefixMemberAccessPostfix()
        ) {
            throw SxError.create(SxErrorType.UNCORRECTED_END_OF_EXPRESSION, text.position, text.line)
        }

        return expr
    }
}

class BracketExpressionParser : SxParser<ExpressionAbstract> {
    companion object {
        private val _instance = BracketExpressionParser()
        fun i(): BracketExpressionParser = _instance
    }

    override fun read(text: Text): ExpressionAbstract {
        val z1 = BracketParser.read(text)
        val ex = ExpressionParser.i().read(text)
        val z2 = BracketParser.read(text)

        return BracketExpression(z1, ex, z2)
    }
}

class ParametersParser : SxParser<Parameters> {
    companion object {
        private val _instance = ParametersParser()
        fun i(): ParametersParser = _instance
    }

    override fun read(text: Text): Parameters {
        val parameters = mutableListOf<ExpressionAbstract>()
        val pos = text.position

        while (true) {
            parameters.add(ExpressionParser.i().read(text))
            if (text.isPrefixBracketClosed())
                return Parameters(pos, parameters)
            if (!text.isPrefixComma())
                throw SxError.createNoMsg(SxErrorType.EXPECTED_PARAMETER, text.position)

            CommaParser.read(text)
        }
    }
}

class CommandPostfixParser : SxParser<CommandPostfix> {
    companion object {
        private val _instance = CommandPostfixParser()
        fun i(): CommandPostfixParser = _instance
    }

    override fun read(text: Text): CommandPostfix {
        val bracketOpen = BracketParser.read(text)
        val parameters = ParametersParser.i().read(text)
        val bracketClose = BracketParser.read(text)
        return CommandPostfix(bracketOpen, parameters, bracketClose)
    }
}


class MemberAccessPostfixParser : SxParser<MemberAccessPostfix> {
    companion object {
        private val _instance = MemberAccessPostfixParser()
        fun i(): MemberAccessPostfixParser = _instance
    }

    override fun read(text: Text): MemberAccessPostfix {
        val dot = DotParser.read(text)
        val name = WordExpressionParser.i().read(text)
        return MemberAccessPostfix(dot, name)
    }
}


class ArrayPostfixParser : SxParser<ArrayPostfix> {
    companion object {
        private val _instance = ArrayPostfixParser()
        fun i(): ArrayPostfixParser = _instance
    }

    override fun read(text: Text): ArrayPostfix {
        val bracketOpen = BracketParser.read(text)

        val elements = mutableListOf<ExpressionAbstract>()
        while  (!text.isPrefixSquareBracketClose()) {
            elements.add(ExpressionParser.i().read(text))
            if (text.isPrefixComma() )
                CommaParser.read(text)
        }

        val bracketClose = BracketParser.read(text)

        return ArrayPostfix(bracketOpen, elements, bracketClose)
    }
}
