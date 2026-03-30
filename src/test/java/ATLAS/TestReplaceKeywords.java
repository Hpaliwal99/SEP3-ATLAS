package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestReplaceKeywords {

    static Stream<Arguments> data() {
        String input = "(work in scientist (some lab (that (conduct experiment))))";
        return Stream.of(
                arguments(input, 0, "0"),
                arguments(input, 1, "1"),
                arguments(input, 2, "2")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void testReplaceKeywords(String input, int keyIndex, String expectedKeyword) throws ParseException {
        Parse parser = new Parse();
        Node root = parser.parse(input);
        parser.replaceKeywords(root);

        Node node = getNodeAtIndex(root, keyIndex);

        assertNotNull(node, () -> "Node at index " + keyIndex + " should not be null");
        assertEquals(expectedKeyword, node.keyword, "Keyword mismatch at index " + keyIndex);
    }

    private Node getNodeAtIndex(Node root, int index) {
        Node current = root;
        for (int i = 0; i < index; i++) {
            assertNotNull(current, () -> "Chain ended before index " + index);
            current = current.children;
            if (current.keyword.isEmpty() && current != null) {
                current = current.children;
            }
        }
        return current;
    }
}
