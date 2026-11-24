package sx2kotlin.cover

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sx2kotlin.ExpType
import sx2kotlin.SxError
import sx2kotlin.SxErrorType
import sx2kotlin.Text
import sx2kotlin.parsers.ExpressionParser
import sx2kotlin.parsers.StatementParser

class TestStatementsParsers {

    @Test
    fun testReadAssignment() {
        doTheTest(listOf(
            listOf("ahoj = 3") to ExpType.VOID,
            listOf("ahoj += 3") to ExpType.VOID,
            listOf("ahoj -= 3") to ExpType.VOID,
            listOf("ahoj *= 3") to ExpType.VOID,
            listOf("ahoj /= 3") to ExpType.VOID,
            listOf("ahoj %= 3") to ExpType.VOID,
            listOf("ahoj[1] %= 3") to ExpType.VOID,
            listOf("ahoj.name %= 3") to ExpType.VOID,
            listOf("ahoj[1].name %= 3") to ExpType.VOID,
            listOf("ahoj.name[2] %= 3") to ExpType.VOID,
        ))
    }

    @Test
    fun testReadAssignmentNegative() {
        doTheTestNegative(listOf(
            listOf("= 3") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("+= 3") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj =") to SxErrorType.END_OF_FILE,
            listOf("ahoj +=") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj = +") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj = 23dd") to SxErrorType.EXPECTED_INT,
            listOf("ahoj = (3+4") to SxErrorType.END_OF_FILE,
            listOf("ahoj = )3+4(") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj = (3+>4)") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj = 3+1<a( or 4>=b") to SxErrorType.UNCORRECTED_END_OF_EXPRESSION,
            listOf("ahoj[1 %= 3") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj..name %= 3") to SxErrorType.EXPECTED_TOKEN,
            listOf("ahoj[.1].name %= 3") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("ahoj().name[2] %= 3") to SxErrorType.UNEXPECTED_PREFIX,
        ))
    }




    private fun doTheTest(cases: List<Pair<List<String>, ExpType>>) {
        for ((lines, expectedType) in cases) {
            val text = Text(lines)
            try {
                val result = StatementParser.i().read(text)
                Assertions.assertEquals(expectedType, result.expType, "Failed for lines: $lines")
            } catch (e: SxError) {
                Assertions.fail("Unexpected exception: $e\nText: ${text.line}")
            }
        }
    }

    private fun doTheTestNegative(cases: List<Pair<List<String>, SxErrorType>>) {
        for ((lines, expectedType) in cases) {
            val text = Text(lines)
            val exception = assertThrows<SxError>("Expected SxError for lines: $lines") {
                ExpressionParser.i().read(text)
            }
            Assertions.assertEquals(expectedType, exception.typ, "Wrong error type for lines: $lines")
        }
    }
}