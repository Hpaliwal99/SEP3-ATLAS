package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Analogy {

    private final int DEFAULT_N = 3;
    private final String KB_PATH = "src/main/java/ATLAS/structured domains.txt";
    private KnowledgeBase kb;
    private List<Map<String, String>> rankedAnalogies = new ArrayList<>();
    private Set<Node> unMatchedStruct = new  HashSet<>();

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

    public List<Map<String, String>> greedyMatching(String S, String T, int n, List<Node> newStruct) throws Exception {
        List<Node> sStructures = kb.getStructures(S);
        List<Node> tStructures = kb.getStructures(T);

        if(newStruct != null && !newStruct.isEmpty()){
            tStructures.addAll(newStruct);
        }

        // precompute all hashes once
        Map<Node, String> sHashes = new HashMap<>();
        Map<Node, String> tHashes = new HashMap<>();
        for (Node n1 : sStructures) sHashes.put(n1, kb.shapeHash(n1));
        for (Node n1 : tStructures) tHashes.put(n1, kb.shapeHash(n1));

        // group T structures by hash for O(1) lookup
        Map<String, List<Node>> tByHash = new HashMap<>();
        for (Node n1 : tStructures) {
            tByHash.computeIfAbsent(tHashes.get(n1), k -> new ArrayList<>()).add(n1);
        }

        // Sort by richness
        sStructures.sort((a, b) -> Double.compare(kb.richness(b), kb.richness(a)));

        List<Map.Entry<Map<String, String>, Integer>> scoredComposites = new ArrayList<>();

        // run matching n times
        int flag = 0;
        for (Node seedNode : sStructures) {
            if (flag == n) break;
            Map<String, String> composite = new LinkedHashMap<>();

            Combiner(tByHash, sHashes.get(seedNode), composite, seedNode);

            for (Node otherS : sStructures) {
                if (otherS == seedNode) continue;
                Combiner(tByHash, sHashes.get(otherS), composite, otherS);
            }

            if (!composite.isEmpty()) {
                scoredComposites.add(Map.entry(composite, composite.size()));
            }
            flag++;
        }
        System.out.println("Done with matching");

        // Sort by number of mappings
        scoredComposites.sort((a, b) -> Double.compare(b.getKey().size(), a.getKey().size()));

        // Remove duplicates
        List<Map<String, String>> ranked = new ArrayList<>();
        for (Map.Entry<Map<String, String>, Integer> e : scoredComposites) {
            if (!ranked.contains(e.getKey())) ranked.add(e.getKey());
            System.out.println("Score: " +  e.getValue() + " -> " + e.getKey());
        }
        rankedAnalogies = ranked;
        return ranked;
    }

    private void Combiner(Map<String, List<Node>> tByHash, String sHash, Map<String, String> composite, Node otherS) throws ParseException {
        // O(1) lookup instead of iterating all T structures
        List<Node> candidates = tByHash.getOrDefault(sHash, Collections.emptyList());

        if (candidates.isEmpty()) {
            unMatchedStruct.add(otherS);
            return;
        }

        for (Node tNode : candidates) {
            Parse pS = new Parse();
            Parse pT = new Parse();
            pS.parseNode(otherS);
            pT.parseNode(tNode);

            LinkedList<String[]> pairs = Utility.getKeywordMapping(pS, pT);
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
            List<Map<String, String>> count = greedyMatching(S, key, DEFAULT_N, null);
            if (!count.isEmpty()) {
                topics.put(key, count.getFirst().size());
            }
        }

        List<Map.Entry<String, Integer>> SortedTopics = new ArrayList<>(topics.entrySet());
        SortedTopics.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        return SortedTopics;
    }

    public List<Node> getCandidateInferences(int n) throws Exception {
        if (rankedAnalogies.isEmpty()) throw new Exception("No mappings found! run greedyMatching first or change topics.");

        List<Node> candidateStructures = new ArrayList<>(unMatchedStruct);
        candidateStructures.sort((a, b) -> Double.compare(kb.richness(a), kb.richness(b)));

        List<Node> candidates = new ArrayList<>();
        int count = 0;
        for (Node seedNode : candidateStructures) {
            count++;
            if (count == n) break;
            Parse p = new Parse();
            p.parseNode(seedNode);

            Node candidate = seedNode.deepCopy();
            Node cand_Iter = candidate;
            boolean flag = true;
            while (cand_Iter != null) {
                String keyT = rankedAnalogies.getFirst().getOrDefault(cand_Iter.keyword,"");
                if (keyT.isEmpty()) flag=false;
                cand_Iter.keyword = keyT;
                cand_Iter = cand_Iter.children;
            }
            if (flag) {
                candidates.add(candidate);
            }
        }

        return candidates;
    }
}
