package ATLAS;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestInferences {

    static Analogy analogy;

    @BeforeAll
    static void setup() throws Exception {
        analogy = new Analogy();
    }

    public static Stream<Arguments> candidateInferenceData() {
        return Stream.of(
                arguments("priest",     "doctor",     7, 10,
                        Set.of("(if  (can  (by following (respect  *doctor (as leader (some disease (when unwanted (by lacking (not (hold  desirability (as asset (can  (succeed_at *doctor (by lacking (not (hold  desirability (as asset )))))))))))))))",
                                "(if  (can  (by following (respect  *doctor (as leader (some disease (when undesirable (by lacking (not (hold  desirability (as asset (can  (succeed_at *doctor (by lacking (not (hold  desirability (as asset )))))))))))))))")
                ),
                arguments("consultant", "counselor", 7, 10,
                        Set.of("(if  (can  (by appointing (appoint *consultant  (as appointee (some pope (when dogmatic (by lacking (not (hold  flexibility (as asset (can  (succeed_at *counselor (by lacking (not (hold  flexibility (as asset )))))))))))))))")
                ),
                arguments("hero",       "king",       10, 20,
                        Set.of())
        );
    }

    @ParameterizedTest
    @MethodSource("candidateInferenceData")
    void getCandidateInferences(String S, String T, int n, int candidateN, Set<String> expected) throws Exception {
        analogy.greedyMatching(S, T, n);
        List<Node> candidates = analogy.getCandidateInferences(candidateN);

        // sorted richest first
        for (int i = 0; i < candidates.size() - 1; i++) {
            assertTrue(
                    analogy.kb.richness(candidates.get(i)) >= analogy.kb.richness(candidates.get(i + 1)),
                    "Candidates not sorted by richness descending"
            );
        }
        Parse parse = new Parse();
        Set<String> actual = candidates.isEmpty() ? new HashSet<>() :
                candidates.stream()
                        .map(parse::toFlat)
                        .collect(Collectors.toSet());
        assertEquals(expected, actual, "Output not equal");
    }


    public static Stream<Arguments> coalesceInferenceData() {
        return Stream.of(
                arguments("priest",     "doctor",     7, 10,
                        Set.of("(if  (can  (by following (respect  *doctor (as leader (some disease (when unwanted (by lacking (not (hold  desirability (as asset (can  (succeed_at *doctor (by lacking (not (hold  desirability (as asset )))))))))))))))",
                                "(if  (can  (by following (respect  *doctor (as leader (some disease (when undesirable (by lacking (not (hold  desirability (as asset (can  (succeed_at *doctor (by lacking (not (hold  desirability (as asset )))))))))))))))")
                ),
                arguments("consultant", "counselor", 7, 10,
                        Set.of("(if  (can  (by appointing (appoint *consultant  (as appointee (some pope (when dogmatic (by lacking (not (hold  flexibility (as asset (can  (succeed_at *counselor (by lacking (not (hold  flexibility (as asset )))))))))))))))")
                ),
                arguments("hero",       "king",       10, 20,
                        Set.of()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("coalesceInferenceData")
    void coalesceInferences(String S, String T, int n, int candidateN, Set<String> expected) throws Exception {
        analogy.greedyMatching(S, T, n);
        List<Node> candidates = analogy.getCandidateInferences(candidateN);
        List<List<Node>> groups = analogy.coalesceInferences(candidates);

        // sorted largest group first
        for (int i = 0; i < groups.size() - 1; i++) {
            assertTrue(
                    groups.get(i).size() >= groups.get(i + 1).size(),
                    "Groups not sorted by size descending"
            );
        }

        // no node in multiple groups
        Set<Node> seen = new HashSet<>();
        for (List<Node> group : groups) {
            for (Node node : group) {
                assertFalse(seen.contains(node), "Node appears in multiple groups: " + node);
                seen.add(node);
            }
        }
        Parse parse = new Parse();
        Set<String> actual = groups.isEmpty() ? new HashSet<>() :
                groups.getFirst().stream()
                        .map(parse::toFlat)
                        .collect(Collectors.toSet());
        assertEquals(expected, actual, "Output not equal");
    }


    public static Stream<Arguments> rankCoalescedData() {
        return Stream.of(
                arguments("priest",     "doctor",     7, 10),
                arguments("priest",     "programmer", 7, 10),
                arguments("hero",       "king",       10, 20)
        );
    }

    @ParameterizedTest
    @MethodSource("rankCoalescedData")
    void rankCoalescedInferences(String S, String T, int n, int candidateN) throws Exception {
        analogy.greedyMatching(S, T, n);
        List<Node> candidates = analogy.getCandidateInferences(candidateN);
        List<List<Node>> groups = analogy.coalesceInferences(candidates);
        List<Map.Entry<List<Node>, Double>> ranked = analogy.rankCoalescedInferences(groups);

        if (ranked.isEmpty()) return; // no inferences is valid (e.g. hero/king)

        // sorted by score descending
        for (int i = 0; i < ranked.size() - 1; i++) {
            assertTrue(
                    ranked.get(i).getValue() >= ranked.get(i + 1).getValue(),
                    "Ranked inferences not sorted by score descending"
            );
        }

        // scores are positive
        for (var e : ranked) {
            assertTrue(e.getValue() > 0, "Score should be positive, got: " + e.getValue());
        }

        // inferences never decrease the base score
        double baseScore = analogy.CombinedQuality(analogy.getMatchedStruct(), candidates);
        assertTrue(ranked.getFirst().getValue() >= baseScore,
                "Inferences should not decrease quality score");
    }
}