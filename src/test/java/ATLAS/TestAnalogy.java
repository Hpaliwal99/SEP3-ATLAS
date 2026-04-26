package ATLAS;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestAnalogy {

    static Analogy analogy;

    @BeforeAll
    static void setup() throws Exception {
        analogy = new Analogy();

    }

    public static Stream<Arguments> GreedyData() {
        return Stream.of(
                arguments(
                        "priest", "programmer", 4,
                        6,
                        Map.of(
                                "*priest",      "*programmer",
                                "creating",     "creating",
                                "miracle",      "code",
                                "prayer",       "computer",
                                "church",       "worst"
                        )
                ),
                arguments(
                        "priest", "scientist", 4,
                        7,
                        Map.of(
                                "*priest",   "*scientist",
                                "church",    "lab",
                                "sermon",    "experiment"
                        )
                ),
                arguments(
                        "priest", "teacher", 4,
                        6,
                        Map.of(
                                "*priest",  "*teacher",
                                "miracle",  "syllabus",
                                "prayer",   "textbooks",
                                "church",   "school"
                        )
                )
        );
    }

    public static Stream<Arguments> data() {

        return Stream.of(
                arguments("[doctor=6, artist=6, programmer=6, priest=6, leader=4, villain=4]", "teacher"),
                arguments("[scientist=7, doctor=6, teacher=6, artist=6, programmer=6]", "priest"),
                arguments("[slave=5, general=5, villain=5, king=5, politician=5]", "hero"),
                arguments("[teacher=6, artist=6, programmer=6, priest=6, surgeon=5]", "doctor"),
                arguments("[slave=5, general=5, villain=5, politician=5, hero=5]", "king"),
                arguments("[]", "historian")
        );
    }

    @ParameterizedTest
    @MethodSource("GreedyData")
    void greedyMatching(String S, String T, int n,
                        int expectedFirstSize,
                        Map<String, String> expectedEntries) throws Exception {

        List<Map<String, String>> result = analogy.greedyMatching(S, T, n);

        // TC-9 sentinel: historian produces no mappings at all
        if (expectedFirstSize == -1) {
            assertTrue(result.isEmpty(),
                    "Expected empty result for topic with no shared shapes, got: " + result);
            return;
        }

        assertBestMapping(result, expectedFirstSize, expectedEntries);
    }

    private static void assertBestMapping(List<Map<String, String>> result,
                                          int expectedSize,
                                          Map<String, String> expectedEntries) {
        assertFalse(result.isEmpty(), "greedyMatching should return at least one mapping");
        Map<String, String> best = result.get(0);
        assertEquals(expectedSize, best.size(),
                "Best mapping size mismatch. Actual mapping: " + best);
        for (Map.Entry<String, String> e : expectedEntries.entrySet()) {
            assertEquals(e.getValue(), best.get(e.getKey()),
                    "Wrong value for key '" + e.getKey() + "' in mapping: " + best);
        }
    }


    @ParameterizedTest
    @MethodSource("data")
    void topSources(String expected, String input) throws Exception {
        List<Map.Entry<String,Integer>> list;
        list = analogy.topSources(input);
        assertEquals(expected, list.toString(), "top sources are not equal");
    }
}