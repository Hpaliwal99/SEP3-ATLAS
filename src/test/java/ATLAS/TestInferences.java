package ATLAS;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
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
                arguments("priest", "scientist", 4, 3,
                        List.of("worshipping", "praying", "preaching") // expected predicate roots
                ),
                arguments("priest", "programmer", 4, 3,
                        List.of("worshipping", "praying", "preaching")
                ),
                arguments("hero", "king", 4, 2,
                        List.of("riding", "enslaving")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("candidateInferenceData")
    void getCandidateInferences(String S, String T, int n, int expectedCount,
                                List<String> expectedPredicateRoots) throws Exception {
        Analogy analogy = new Analogy();
        analogy.greedyMatching(S, T, n, new ArrayList<>());
        List<Node> candidates = analogy.getCandidateInferences(n);

        assertFalse(candidates.isEmpty(), "Expected candidate inferences but got none");
        assertTrue(candidates.size() <= expectedCount,
                "Too many candidates returned: " + candidates.size());

        // check candidates are sorted richest first
        for (int i = 0; i < candidates.size() - 1; i++) {
            assertTrue(
                    analogy.kb.richness(candidates.get(i)) >= analogy.kb.richness(candidates.get(i + 1)),
                    "Candidates not sorted by richness descending"
            );
        }

        // check expected predicate roots appear
        Parse p = new Parse();
        List<String> actualPredicates = candidates.stream()
                .map(m -> p.toFlat(m))
                .toList();
        for (String root : expectedPredicateRoots) {
            assertTrue(
                    actualPredicates.stream().anyMatch(s -> s.contains(root)),
                    "Expected predicate root '" + root + "' not found in candidates: " + actualPredicates
            );
        }
    }

    public static Stream<Arguments> coalesceInferenceData() {
        return Stream.of(
                arguments("priest", "scientist", 4, 3, 1),  // expect at least 1 group
                arguments("priest", "programmer", 4, 3, 1),
                arguments("hero", "king", 4, 2, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("coalesceInferenceData")
    void coalesceInferences(String S, String T, int n, int candidateN,
                            int minGroups) throws Exception {
        analogy.greedyMatching(S, T, n, new ArrayList<>());
        List<Node> candidates = analogy.getCandidateInferences(candidateN);
        List<List<Node>> groups = analogy.coalesceInferences(candidates);

        assertFalse(groups.isEmpty(), "Expected at least one coalesced group");
        assertTrue(groups.size() >= minGroups,
                "Expected at least " + minGroups + " group(s), got: " + groups.size());

        // check sorted largest group first
        for (int i = 0; i < groups.size() - 1; i++) {
            assertTrue(
                    groups.get(i).size() >= groups.get(i + 1).size(),
                    "Groups not sorted by size descending"
            );
        }

        // check no node appears in multiple groups
        Set<Node> seen = new HashSet<>();
        for (List<Node> group : groups) {
            for (Node node : group) {
                assertFalse(seen.contains(node),
                        "Node appears in multiple groups: " + node);
                seen.add(node);
            }
        }
    }

    public static Stream<Arguments> rankCoalescedData() {
        return Stream.of(
                arguments("priest", "scientist", 4, 3),
                arguments("priest", "programmer", 4, 3),
                arguments("hero", "king", 4, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("rankCoalescedData")
    void rankCoalescedInferences(String S, String T, int n, int candidateN) throws Exception {
        analogy.greedyMatching(S, T, n, new ArrayList<>());
        List<Node> candidates = analogy.getCandidateInferences(candidateN);
        List<List<Node>> groups = analogy.coalesceInferences(candidates);
        List<Map.Entry<List<Node>, Double>> ranked = analogy.rankCoalescedInferences(groups);

        assertFalse(ranked.isEmpty(), "Expected ranked inferences but got none");

        // check sorted by score descending
        for (int i = 0; i < ranked.size() - 1; i++) {
            assertTrue(
                    ranked.get(i).getValue() >= ranked.get(i + 1).getValue(),
                    "Ranked inferences not sorted by score descending"
            );
        }

        // check scores are positive
        for (Map.Entry<List<Node>, Double> e : ranked) {
            assertTrue(e.getValue() > 0,
                    "Score should be positive, got: " + e.getValue());
        }

        // check adding inferences increases score over base
        List<Map.Entry<List<Node>, Double>> baseRanked =
                analogy.rankCoalescedInferences(List.of(new ArrayList<>()));
        double baseScore = baseRanked.isEmpty() ? 0 : baseRanked.getFirst().getValue();
        assertTrue(ranked.getFirst().getValue() >= baseScore,
                "Inference should not decrease quality score");
    }
}
