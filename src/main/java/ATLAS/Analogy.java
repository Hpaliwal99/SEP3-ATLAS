package ATLAS;

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

        // Step 1: collect all individual mappings with their richness score
        List<Map.Entry<Map<String, String>, Double>> scoredMappings = new ArrayList<>();

        for (Node sNode : sStructures) {
            for (Node tNode : tStructures) {
                if (!kb.shapeHash(sNode).equals(kb.shapeHash(tNode))) continue;

                Parse pS = new Parse();
                Parse pT = new Parse();
                pS.parse(pS.toFlat(sNode));
                pT.parse(pT.toFlat(tNode));

                LinkedList<String[]> pairs = Utility.getKeywordMapping(pS, pT);
                if (pairs.isEmpty()) continue;

                Map<String, String> mapping = new LinkedHashMap<>();
                for (String[] pair : pairs) mapping.put(pair[0], pair[1]);

                double richness = kb.richness(sNode);
                scoredMappings.add(Map.entry(mapping, richness));
            }
        }

        // Step 2: sort by richness descending
        scoredMappings.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Step 3: for each starting point, greedily coalesce
        List<Map<String, String>> composites = new ArrayList<>();

        for (int start = 0; start < scoredMappings.size(); start++) {
            Map<String, String> composite = new LinkedHashMap<>(scoredMappings.get(start).getKey());

            for (int i = 0; i < scoredMappings.size(); i++) {
                if (i == start) continue;
                Map<String, String> candidate = scoredMappings.get(i).getKey();
                if (isConsistent(composite, candidate)) {
                    composite.putAll(candidate);
                }
            }

            composites.add(composite);
        }

        // Step 4: rank composites by number of distinct mappings
        composites.sort((a, b) -> Integer.compare(b.size(), a.size()));

        // Step 5: deduplicate identical composites
        List<Map<String, String>> ranked = new ArrayList<>();
        for (Map<String, String> c : composites) {
            if (!ranked.contains(c)) ranked.add(c);
        }

        System.out.println("Ranked analogies for " + S + " -> " + T + ":");
        for (int i = 0; i < ranked.size(); i++) {
            System.out.println("#" + (i + 1) + " (" + ranked.get(i).size() + " mappings): " + ranked.get(i));
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
