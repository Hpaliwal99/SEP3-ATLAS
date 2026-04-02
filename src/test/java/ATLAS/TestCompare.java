package ATLAS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestCompare {

    public static Stream<Arguments> data() {
        StringBuilder input1 = new StringBuilder("(work in scientist (some lab (that (conduct experiment))))");
        StringBuilder input2 = new StringBuilder("(work in priest (some church (that (conduct sermon))))");
        StringBuilder input3 = new StringBuilder("(null)");
        StringBuilder input4 = new StringBuilder("(Some artist (create art (with paint)))");
        StringBuilder input5 = new StringBuilder("(Some programmer (create code (with computer)))");
        return Stream.of(
                arguments(input1.toString(), input2.toString(), true),
                arguments(input1.insert(8, '*').toString(), input2.toString(), false),
                arguments(input3.toString(), input4.toString(), false),
                arguments(input4.toString(), input5.toString(), true),
                arguments(input4.insert(5, '*').toString(), input5.insert(5, '*').toString(), true)
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCompare(String A, String B, boolean expected) throws ParseException {
        Parse parserA = new Parse();
        Node rootA = parserA.parse(A);
        Parse parserB = new Parse();
        Node rootB = parserB.parse(B);

        assertEquals(expected, Utility.compare(parserA, parserB), "Comparison not as expected");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testStringCompare(String A, String B, Boolean expected) throws ParseException {

        assertEquals(expected, Utility.StringCompare(A, B), "Comparison not as expected");
    }
}
