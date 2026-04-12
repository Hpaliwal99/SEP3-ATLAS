package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
}