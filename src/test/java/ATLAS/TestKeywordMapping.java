package ATLAS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestKeywordMapping {

    public static Stream<Arguments> data() {
        StringBuilder input1 = new StringBuilder("(work in scientist (some lab (that (conduct experiment))))");
        StringBuilder input2 = new StringBuilder("(work in priest (some church (that (conduct sermon))))");
        String input3 = "(null)";
        StringBuilder input4 = new StringBuilder("(Some artist (create art (with paint)))");
        StringBuilder input5 = new StringBuilder("(Some programmer (create code (with computer)))");
        String output1 = "[[*scientist, *priest], [lab, church], [experiment, sermon]]";
        String output2 = "[[artist, programmer], [art, code], [paint, computer]]";
        return Stream.of(
                arguments(input1.insert(9, '*').toString(), input2.insert(9, '*').toString(), output1),
                arguments(input4.toString(), input5.toString(), output2)
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testKeywordMapping(String A, String B, String output) throws ParseException {

        Parse parserA = new Parse();
        Node rootA = parserA.parse(A);
        Parse parserB = new Parse();
        Node rootB = parserB.parse(B);

        String StringOut1 = Utility.toStringLL(Utility.getKeywordMapping(parserA, parserB));
        String StringOut2 = Utility.toStringLL(Utility.getStringKeywordMapping(A, B));

        assertEquals(output, StringOut1);
        assertEquals(output, StringOut2);

    }


}
