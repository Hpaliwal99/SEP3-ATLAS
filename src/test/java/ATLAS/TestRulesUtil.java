package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestRulesUtil {

    public static Stream<Arguments> data() {
        return Stream.of(
                arguments("(are_chronicled_in *hero myth)",  "(by chronicling (communicate historian myth (about *hero )))"),
                arguments("(dislike hero villain)",          "(by disliking (not (respect hero villain (as friend )))"),
                arguments("(anger hero villain)",            "(by angering (imbue hero villain (with anger )))"),
                arguments("(sleep hero)",                    "(sleep hero )")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testRewrite(String input, String expected) throws Exception {
        RulesUtil rulesUtil = new RulesUtil();
        rulesUtil.loadRules("src/main/java/ATLAS/rewrite rules.txt");

        List<Node> out = rulesUtil.rewrite(input);

        Parse parse = new Parse();
        String result = parse.toFlat(out.getFirst());

        assertEquals(expected, result, "Rewrite output not as expected");
    }

    public static Stream<Arguments> data1() {
        return Stream.of(
                arguments("(are_chronicled_by *hero myth (are_enslaved_by vigilante tragedy))",  "(by chronicling (communicate myth history (about *hero (by enslaving (control tragedy vigilante (with slavery ))))))\n" +
                        "(by chronicling (communicate myth history (about *hero (by enslaving (control tragedy freedom (of vigilante ))))))\n" +
                        "(by chronicling (produce myth chronicle (of *hero (by enslaving (control tragedy vigilante (with slavery ))))))\n" +
                        "(by chronicling (produce myth chronicle (of *hero (by enslaving (control tragedy freedom (of vigilante ))))))")
        );
    }

    @ParameterizedTest
    @MethodSource("data1")
    public void testProduct(String input, String expected) throws Exception {
        RulesUtil rulesUtil = new RulesUtil();
        rulesUtil.loadRules("src/main/java/ATLAS/rewrite rules.txt");

        List<Node> out = rulesUtil.rewrite(input);

        Parse parse = new Parse();
        List<String> thingy = new ArrayList<>();
        for (Node n : out) {
            Parse z = new Parse();
            thingy.add(z.toFlat(n));
        }
//        String result = parse.toFlat(out.getFirst().getFirst());
        List<String> exp = List.of(expected.split("\n"));
        assertEquals(exp, thingy, "Rewrite output not as expected");
    }
}