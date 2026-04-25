package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Analogy {

    private final int DEFAULT_N = 3;
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
                Parse p = new Parse();
                String pS = p.toFlat(sNode);
                String pT = p.toFlat(tNode);

                LinkedList<String[]> pairs = Utility.getStringKeywordMapping(pS, pT);
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

    public List<Map<String, String>> rankedAnalogies(String S, String T, int n) throws Exception {
        List<Node> sStructures = kb.getStructures(S);
        List<Node> tStructures = kb.getStructures(T);

        // Step 1: sort S structures by richness descending
        sStructures.sort((a, b) -> Double.compare(kb.richness(b), kb.richness(a)));

        // Step 2: for each S structure as seed, find all matching T structures and coalesce
        List<Map.Entry<Map<String, String>, Integer>> scoredComposites = new ArrayList<>();

        int flag = 0;
        for (Node seedNode : sStructures) {
            if (flag == n) break;
            Map<String, String> composite = new LinkedHashMap<>();


            // try to map this S structure against every T structure
            Combiner(tStructures, seedNode, composite, seedNode);

            // now try to coalesce other S structures into this composite
            for (Node otherS : sStructures) {
                if (otherS == seedNode) continue;

                Combiner(tStructures, seedNode, composite, otherS);
            }

            if (!composite.isEmpty()) {
                scoredComposites.add(Map.entry(composite, composite.size()));
            }
            flag++;
        }

        // Step 3: rank by score descending
        scoredComposites.sort((a, b) -> Double.compare(b.getKey().size(), a.getKey().size()));

        // Step 4: deduplicate
        List<Map<String, String>> ranked = new ArrayList<>();
        for (Map.Entry<Map<String, String>, Integer> e : scoredComposites) {
            if (!ranked.contains(e.getKey())) ranked.add(e.getKey());
            System.out.println("Score: " +  e.getValue() + " -> " + e.getKey());
        }

        return ranked;
    }

    private void Combiner(List<Node> tStructures, Node seedNode, Map<String, String> composite, Node otherS) throws ParseException {
        for (Node tNode : tStructures) {
            if (!kb.shapeHash(otherS).equals(kb.shapeHash(tNode))) continue;

            Parse p = new Parse();
            String pS = p.toFlat(seedNode);
            String pT = p.toFlat(tNode);

            LinkedList<String[]> pairs = Utility.getStringKeywordMapping(pS, pT);
            if (pairs.isEmpty()) continue;

            Map<String, String> mapping = new LinkedHashMap<>();
            for (String[] pair : pairs) mapping.put(pair[0], pair[1]);

            if (isConsistent(composite, mapping)) {
                composite.putAll(mapping);

            }
        }
    }

    public List<Map.Entry<String, Integer>> topSources(String S) throws Exception {
        Map<String, Integer> topics = new  HashMap<>();
        for (Map.Entry<String, List<Node>> entry : kb.getIndex().entrySet()) {
            String key = entry.getKey();
            List<Map<String, String>> count = rankedAnalogies(S, key, DEFAULT_N);
            if (!count.isEmpty()) {
                topics.put(key, count.getFirst().size());
            }
        }

        List<Map.Entry<String, Integer>> SortedTopics = new ArrayList<>(topics.entrySet());
        SortedTopics.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        return SortedTopics;
    }
}
