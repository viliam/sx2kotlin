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

class SimpleExpressionParser : SxParser<ExpressionABC> {
    companion object {
        private val _instance = SimpleExpressionParser()
        fun i(): SimpleExpressionParser = _instance
    }

    override fun read(text: Text): ExpressionABC {
        return when {
            text.isPrefixInt() -> IntegerParser.i().read(text)
            text.isPrefixVariable() -> VariableParser.i().read(text)
            else -> throw SxError.create(SxErrorType.UNEXPECTED_PREFIX, text.position, text.line)
        }
    }
}

class ExpressionParser : SxParser<ExpressionABC> {
    companion object {
        private val _instance = ExpressionParser()
        fun i(): ExpressionParser = _instance
    }

    override fun read(text: Text): ExpressionABC {
        val expr = if (text.isPrefixBracketOpen()) {
            BracketExpressionParser.i().read(text)
        } else {
            SimpleExpressionParser.i().read(text)
        }

        if (text.isEndOfFile()) {
            return expr
        }

        if (text.isPrefixOperator()) {
            val op = OperatorExpressionParser.i().read(text)
            return Expression(expr, op, i().read(text))
        }

        if (!text.isEndOfFile() &&
            !text.isPrefixOperator() &&
            !text.isPrefixComma() &&
            !text.isPrefixBracketClosed()
        ) {
            throw SxError.create(SxErrorType.UNCORRECTED_END_OF_EXPRESSION, text.position, text.line)
        }

        return expr
    }
}

class BracketExpressionParser : SxParser<ExpressionABC> {
    companion object {
        private val _instance = BracketExpressionParser()
        fun i(): BracketExpressionParser = _instance
    }

    override fun read(text: Text): ExpressionABC {
        val z1 = BracketParser.i().read(text)
        val ex = ExpressionParser.i().read(text)
        val z2 = BracketParser.i().read(text)

        return BracketExpression(z1, ex, z2)
    }
}
