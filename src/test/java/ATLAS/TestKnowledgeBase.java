package ATLAS;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestKnowledgeBase {

    static KnowledgeBase kb;

    @BeforeAll
    static void loadKnowledgeBase() throws Exception {

        kb = new KnowledgeBase();
        kb.load("src/main/java/ATLAS/knowledge.txt");
    }


    @ParameterizedTest
    @ValueSource(strings = {"hero", "villain", "scientist", "priest"})
    void knownTopicsHaveStructures(String topic) {
        assertFalse(kb.getStructures(topic).isEmpty(),
                "Expected structures for topic: " + topic);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dragon", "wizard", "unknown", ""})
    void unknownTopicsHaveNoStructures(String topic) {
        assertTrue(kb.getStructures(topic).isEmpty(),
                "Expected no structures for unknown topic: " + topic);
    }

    public static Stream<Arguments> topic_count() {
        return Stream.of(
                arguments("hero",      8),
                arguments("villain",   2),   // get_stronger_by, live_in
                arguments("scientist", 6),   // are_chronicled_by, work in
                arguments("priest",    2)    // work in only)
        );
    }

    @ParameterizedTest
    @MethodSource("topic_count")
    public void structureCountForTopic(String topic, int expectedCount) {
        assertEquals(expectedCount, kb.getStructures(topic.trim()).size(),
                "Structure count mismatch for: " + topic.trim());
    }

    public static Stream<Arguments> topic_analogy() {
        return Stream.of(
          arguments("hero",      "scientist"),
                arguments("hero",      "villain"),
                arguments("hero",      "priest"),
                arguments("scientist", "hero"),
                arguments("scientist", "priest"),
                arguments("villain",   "hero"   )
        );
    }

    @ParameterizedTest
    @MethodSource("topic_analogy")
    void sourcesContainsExpectedAnalogy(String target, String expectedSource) {
        assertTrue(kb.getSources(target.trim()).contains(expectedSource.trim()),
                "getSources(" + target.trim() + ") should contain: " + expectedSource.trim());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hero", "villain", "scientist", "priest"})
    void sourcesDoesNotContainSelf(String topic) {
        assertFalse(kb.getSources(topic).contains(topic),
                "getSources() should not include the target itself: " + topic);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hero", "villain", "scientist", "priest"})
    void rankSourcesNonEmpty(String topic) {
        assertFalse(kb.rankSources(topic).isEmpty(),
                "Expected a non-empty ranking for: " + topic);
    }

    public static Stream<Arguments> rankedsources() {
        List<String> l1 = List.of("hero");
        List<String> l2 = List.of("hero", "priest");
        List<String> l3 = List.of("scientist", "hero");
        List<String> l4 = List.of("scientist", "priest", "villain");

        return Stream.of(
                arguments("villain", l1),
                arguments("scientist", l2),
                arguments("priest", l3),
                arguments("hero", l4)
        );
    }

    @ParameterizedTest
    @MethodSource("rankedsources")
    void rankSourcesResult(String target, List<String> expectedTop) {
        List<String> ranked = kb.rankSources(target.trim());
        assertFalse(ranked.isEmpty(), "Ranking should not be empty for: " + target.trim());
        assertEquals(expectedTop, ranked,
                "Expected Ordering for" + target.trim() + " to be " + expectedTop);
    }

}