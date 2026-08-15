package sx2kotlin.cover

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sx2kotlin.ExpType
import sx2kotlin.SxError
import sx2kotlin.SxErrorType
import sx2kotlin.Text
import sx2kotlin.parsers.ExpressionParser

class TestExpressionsParsers {

    @Test
    fun testReadSimplePositive() {
        doTheTest(listOf(
            listOf("23") to ExpType.INT,
            listOf("ahoj") to ExpType.UNKNOWN
        ))
    }

    @Test
    fun testReadSimpleNegative() {
        doTheTestNegative(listOf(
            listOf("23dd") to SxErrorType.EXPECTED_INT,
            listOf("ahoj+") to SxErrorType.END_OF_FILE,
        ))
    }

    @Test
    fun testReadBinaryPositive() {
        doTheTest(listOf(
            listOf("23+3") to ExpType.INT,
            listOf("23+a") to ExpType.INT,
            listOf("23*a") to ExpType.INT,
            listOf("23/a") to ExpType.INT,
            listOf("23**a") to ExpType.INT,
            listOf("(23+a)") to ExpType.INT,
            listOf("2>3") to ExpType.COMPARISON,
            listOf("2<3") to ExpType.COMPARISON,
            listOf("2>=df") to ExpType.COMPARISON,
            listOf("2<=df") to ExpType.COMPARISON
        ))
    }

    @Test
    fun testReadComplexPositive() {
        doTheTest(listOf(
            listOf("(23+a)-4") to ExpType.INT,
            listOf("(3+a)-(4)") to ExpType.INT,
            listOf("3+(a-(4))") to ExpType.INT,
            listOf("((3+a)-(4))") to ExpType.INT,
            listOf("(3<a) and (4 == 4)") to ExpType.BOOL,
            listOf("3+1<a or 4>=ahoj") to ExpType.BOOL
        ))
    }

    @Test
    fun testReadComplexNegative() {
        doTheTestNegative(listOf(
            listOf("(23+a-4") to SxErrorType.END_OF_FILE,
            listOf("(3+3a)-(4)") to SxErrorType.EXPECTED_INT,
            listOf(")3+(a-(4))") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("((3+>a)-(4))") to SxErrorType.UNEXPECTED_PREFIX,
            listOf("(3<a) ad (4 == 4)") to SxErrorType.EXPECTED_OPERATOR,
            listOf("3+1<a( or 4>=ahoj") to SxErrorType.UNCORRECTED_END_OF_EXPRESSION
        ))
    }


    @Test
    fun testReadMultiLine() {
        doTheTest(listOf(
            listOf("23+3+", "4") to ExpType.INT,
            listOf("(23+3", "+4)") to ExpType.INT,
            listOf("23+3", "+4") to ExpType.INT
        ))
    }

    @Test
    fun testReadMultiLineNegative() {
        doTheTestNegative(listOf(
            listOf("23+3+", "+4") to SxErrorType.UNEXPECTED_PREFIX
        ))
    }


    @Test
    fun testReadCommands() {
        doTheTest(listOf(
            listOf("func(1, 2)" ) to ExpType.UNKNOWN,
            listOf("lolo(a+1,b,c)") to ExpType.UNKNOWN,
            listOf("func(1)(2)") to ExpType.UNKNOWN,
            listOf("arr[0](1)" ) to ExpType.UNKNOWN
        ))
    }

    @Test
    fun testReadArray() {
        doTheTest(listOf(
            listOf("[1, 2]") to ExpType.UNKNOWN,
            listOf("[]") to ExpType.UNKNOWN,
            listOf("arr[0]") to ExpType.UNKNOWN,
            listOf("arr[i+1]") to ExpType.UNKNOWN,
            listOf("arr[0][1]") to ExpType.UNKNOWN,
            listOf("func(1, 2)[0]") to ExpType.UNKNOWN,
            listOf("arr[0]+1") to ExpType.INT,
            listOf("[1, 2][0]") to ExpType.UNKNOWN
        ))
    }

    @Test
    fun testReadArrayNegative() {
        doTheTestNegative(listOf(
            listOf("arr[0") to SxErrorType.END_OF_FILE,
            listOf("[1, 2") to SxErrorType.END_OF_FILE,
            listOf("arr[0 1]") to SxErrorType.UNCORRECTED_END_OF_EXPRESSION
        ))
    }

    @Test
    fun testReadMemberAccessPositive() {
        doTheTest(listOf(
            listOf("obj.name") to ExpType.UNKNOWN,
            listOf("a.b.c") to ExpType.UNKNOWN,
            listOf("a.b.c.d") to ExpType.UNKNOWN,
            listOf("obj.method(1)") to ExpType.UNKNOWN,
            listOf("obj.method(1, 2)") to ExpType.UNKNOWN,
            listOf("obj.arr[0]") to ExpType.UNKNOWN,
            listOf("arr[0].name") to ExpType.UNKNOWN,
            listOf("func(1).name") to ExpType.UNKNOWN,
            listOf("obj.a.b(1).c[0].d") to ExpType.UNKNOWN,
            listOf("obj.value+1") to ExpType.INT,
            listOf("obj.value+other.value") to ExpType.INT,
            listOf("obj.x>obj.y") to ExpType.COMPARISON,
            listOf("(obj.name)") to ExpType.UNKNOWN,
            listOf("obj.a and obj.b") to ExpType.BOOL,
        ))
    }

    @Test
    fun testReadMemberAccessNegative() {
        doTheTestNegative(listOf(
            listOf("obj.") to SxErrorType.EXPECTED_TOKEN,
            listOf("obj.name.") to SxErrorType.EXPECTED_TOKEN,
            listOf("obj.+name") to SxErrorType.EXPECTED_TOKEN,
        ))
    }


    private fun doTheTest(cases: List<Pair<List<String>, ExpType>>) {
        for ((lines, expectedType) in cases) {
            val text = Text(lines)
            try {
                val result = ExpressionParser.i().read(text)
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