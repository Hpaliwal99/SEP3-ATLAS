package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Analogy {

    private final int DEFAULT_N = 3;
    private final String KB_PATH = "src/main/java/ATLAS/knowledge.txt";
    private KnowledgeBase kb;
    private Map<String, Map<String, String>> rankedAnalogies;

    public Analogy() throws Exception {
        this.kb = new KnowledgeBase();
        kb.load(KB_PATH);
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

    public List<Map<String, String>> greedyMatching(String S, String T, int n) throws Exception {
        List<Node> sStructures = kb.getStructures(S);
        List<Node> tStructures = kb.getStructures(T);

        // Sort by richness
        sStructures.sort((a, b) -> Double.compare(kb.richness(b), kb.richness(a)));

        List<Map.Entry<Map<String, String>, Integer>> scoredComposites = new ArrayList<>();

        // run matching n times
        int flag = 0;
        for (Node seedNode : sStructures) {
            if (flag == n) break;
            Map<String, String> composite = new LinkedHashMap<>();

            Combiner(tStructures, seedNode, composite, seedNode);

            for (Node otherS : sStructures) {
                if (otherS == seedNode) continue;

                Combiner(tStructures, seedNode, composite, otherS);
            }

            if (!composite.isEmpty()) {
                scoredComposites.add(Map.entry(composite, composite.size()));
            }
            flag++;
        }

        // Sort by number of mappings
        scoredComposites.sort((a, b) -> Double.compare(b.getKey().size(), a.getKey().size()));

        // Remove duplicates
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
            if (S.equals(key)) continue;
            List<Map<String, String>> count = greedyMatching(S, key, DEFAULT_N);
            if (!count.isEmpty()) {
                topics.put(key, count.getFirst().size());
            }
        }

        List<Map.Entry<String, Integer>> SortedTopics = new ArrayList<>(topics.entrySet());
        SortedTopics.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        return SortedTopics;
    }
}
