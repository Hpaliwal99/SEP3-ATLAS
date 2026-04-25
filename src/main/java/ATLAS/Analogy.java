package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Analogy {

    private KnowledgeBase kb;
    private Map<String, Map<String, String>> rankedAnalogies;

    public Analogy() throws Exception {
        this.kb = new KnowledgeBase();
        kb.load("src/main/java/ATLAS/knowledge.txt");

    }
    public boolean isConsistent(Map<String, String> a, Map<String, String> b) {
        for (Map.Entry<String, String> e : b.entrySet()) {
            if (a.containsKey(e.getKey()) && !a.get(e.getKey()).equals(e.getValue()))
                return false;
            if (a.containsValue(e.getValue()) && !a.containsKey(e.getKey()))
                return false;
        }
        return true;
    }
    public Map<String, String> bestAnalogy(String S, String T) throws Exception {

        List<Node> sStructures = kb.getStructures(S);
        List<Node> tStructures = kb.getStructures(T);

        System.out.println("S structures for " + S + ": " + sStructures.size());
        System.out.println("T structures for " + T + ": " + tStructures.size());

        List<Map<String, String>> allMappings = new ArrayList<>();

        for (Node sNode : sStructures) {
            String sHash = kb.shapeHash(sNode);

            for (Node tNode : tStructures) {
                String tHash = kb.shapeHash(tNode);

                // compare hashes first
                if (!sHash.equals(tHash)) continue;

                System.out.println("Hash match: " + sHash);

                // hashes match, now compare keywords
                Parse pS = new Parse();
                Parse pT = new Parse();
                pS.parse(pS.toFlat(sNode));
                pT.parse(pT.toFlat(tNode));

                LinkedList<String[]> pairs = Utility.getKeywordMapping(pS, pT);
                System.out.println("Pairs: " + Utility.toStringLL(pairs));
                if (pairs.isEmpty()) continue;

                Map<String, String> mapping = new LinkedHashMap<>();
                for (String[] pair : pairs) {
                    mapping.put(pair[0], pair[1]);
                }
                allMappings.add(mapping);
            }
        }

        System.out.println("Total mappings found: " + allMappings.size());

        Map<String, String> best = new HashMap<>();
        for (Map<String, String> candidate : allMappings) {
            if (isConsistent(best, candidate)) {
                best.putAll(candidate);
            }
        }

        return best;
    }

    public List<Map<String, String>> rankedAnalogies(String S, String T) throws Exception {
        List<Node> sStructures = kb.getStructures(S);
        List<Node> tStructures = kb.getStructures(T);

        // Step 1: sort S structures by richness descending
        sStructures.sort((a, b) -> Double.compare(kb.richness(b), kb.richness(a)));

        // Step 2: for each S structure as seed, find all matching T structures and coalesce
        List<Map.Entry<Map<String, String>, Double>> scoredComposites = new ArrayList<>();

        for (Node seedNode : sStructures) {
            Map<String, String> composite = new LinkedHashMap<>();
            double score = 0.0;

            // try to map this S structure against every T structure
            for (Node tNode : tStructures) {
                if (!kb.shapeHash(seedNode).equals(kb.shapeHash(tNode))) continue;

                Parse pS = new Parse();
                Parse pT = new Parse();
                pS.parse(pS.toFlat(seedNode));
                pT.parse(pT.toFlat(tNode));

                LinkedList<String[]> pairs = Utility.getKeywordMapping(pS, pT);
                if (pairs.isEmpty()) continue;

                Map<String, String> mapping = new LinkedHashMap<>();
                for (String[] pair : pairs) mapping.put(pair[0], pair[1]);

                if (isConsistent(composite, mapping)) {
                    composite.putAll(mapping);
                    score += kb.richness(seedNode);
                }
            }

            // now try to coalesce other S structures into this composite
            for (Node otherS : sStructures) {
                if (otherS == seedNode) continue;

                for (Node tNode : tStructures) {
                    if (!kb.shapeHash(otherS).equals(kb.shapeHash(tNode))) continue;

                    Parse pS = new Parse();
                    Parse pT = new Parse();
                    pS.parse(pS.toFlat(otherS));
                    pT.parse(pT.toFlat(tNode));

                    LinkedList<String[]> pairs = Utility.getKeywordMapping(pS, pT);
                    if (pairs.isEmpty()) continue;

                    Map<String, String> mapping = new LinkedHashMap<>();
                    for (String[] pair : pairs) mapping.put(pair[0], pair[1]);

                    if (isConsistent(composite, mapping)) {
                        composite.putAll(mapping);
                        score += kb.richness(otherS);
                    }
                }
            }

            if (!composite.isEmpty()) {
                scoredComposites.add(Map.entry(composite, score));
            }
        }

        // Step 3: rank by score descending
        scoredComposites.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Step 4: deduplicate
        List<Map<String, String>> ranked = new ArrayList<>();
        for (Map.Entry<Map<String, String>, Double> e : scoredComposites) {
            if (!ranked.contains(e.getKey())) ranked.add(e.getKey());
            System.out.println("Score: " + String.format("%.4f", e.getValue()) + " -> " + e.getKey());
        }

        return ranked;
    }

    public List<Map.Entry<String, Integer>> topSources(String S) throws Exception {
        Map<String, Integer> topics = new  HashMap<>();
        for (Map.Entry<String, List<Node>> entry : kb.getIndex().entrySet()) {
            String key = entry.getKey();
            List<Map<String, String>> count = rankedAnalogies(S, key);
            if (!count.isEmpty()) {
                topics.put(key, count.getFirst().size());
            }
        }

        List<Map.Entry<String, Integer>> SortedTopics = new ArrayList<>(topics.entrySet());
        SortedTopics.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        return SortedTopics;
    }
}
