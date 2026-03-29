package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestInvalidInput {

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "work in scientist",
            "(work in ()"
    })
    void testInvalidInputThrows(String input) {
        Parse parser = new Parse();
        // assertThrows replaces the @Test(expected = ...) attribute
        assertThrows(ParseException.class, () -> {
            parser.parse(input);
        });
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testNullInputThrows(String input) {
        Parse parser = new Parse();
        assertThrows(ParseException.class, () -> parser.parse(input));
    }

    static Stream<String> nullProvider() {
        return Stream.of((String) null);
    }
}
