package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestRootKeyword {

    static Stream<Arguments> data() {
        return Stream.of(
                arguments("(work in scientist (some lab (that (conduct experiment))))", "scientist"),
                arguments("(run fast (some track))",                                    "fast"),
                arguments("(jump high (over fence))",                                   "high")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void testRootKeyword(String input, String expectedKeyword) throws ParseException {
        Parse parser = new Parse();
        Node root = parser.parse(input);

        assertNotNull(root, "Root node should not be null");
        assertEquals(expectedKeyword, root.keyword, "Keyword should match expected value");
    }
}
