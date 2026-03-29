package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestRootPredicate {


    static Stream<Arguments> data() {
        return Stream.of(
                arguments("(work in scientist (some lab (that (conduct experiment))))", "work in"),
                arguments("(run fast (some track))",                                    "run fast"),
                arguments("(single)",                                                   "single"),
                arguments("(jump high (over fence))",                                   "jump high")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void testRootPredicate(String input, String expectedPredicate) throws ParseException {
        Parse parser = new Parse();
        Node root = parser.parse(input);

        assertNotNull(root, "Root node should not be null");
        assertEquals(expectedPredicate, root.Predicate, "Predicate should match expected value");
    }
}
