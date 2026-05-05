package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Analogy {

    private final int DEFAULT_N = Config.defaultN();
    private final String KB_PATH = Config.kbPath();
//    private final String KB_PATH = "src/main/java/ATLAS/knowledge.txt";
    public KnowledgeBase kb;
    private List<Map<String, String>> rankedAnalogies = new ArrayList<>();
    private Set<Node> unMatchedStruct = new LinkedHashSet<>();
    private Set<Node> matchedStruct = new  LinkedHashSet<>();
    private final double INFERENCE_POWER = Config.inferencePower();

    public Set<Node> getUnMatchedStruct() {
        return unMatchedStruct;
    }

    public Set<Node> getMatchedStruct() {
        return matchedStruct;
    }

    public List<Map<String, String>> getRankedAnalogies() {
        return rankedAnalogies;
    }

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
        unMatchedStruct.clear();
        matchedStruct.clear();
        List<Node> sStructures = new ArrayList<>(kb.getStructures(S));
        List<Node> tStructures = new ArrayList<>(kb.getStructures(T));

        // For consistent output
        sStructures.sort((a, b) -> {
            int cmp = Double.compare(kb.richness(b), kb.richness(a));
            if (cmp != 0) return cmp;
            return kb.shapeHash(a).compareTo(kb.shapeHash(b)); // stable tiebreak
        });

//        if(newStruct != null && !newStruct.isEmpty()){
//            tStructures.addAll(newStruct);
//        }

        // precompute all hashes once
        Map<Node, String> sHashes = new HashMap<>();
        Map<Node, String> tHashes = new HashMap<>();
        for (Node n1 : sStructures) sHashes.put(n1, kb.shapeHash(n1));
        for (Node n1 : tStructures) tHashes.put(n1, kb.shapeHash(n1));

        // group T structures by hash for O(1) lookup
        Map<String, List<Node>> tByHash = new LinkedHashMap<>();
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
//            System.out.println("Score: " +  e.getValue() + " -> " + e.getKey());
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
                matchedStruct.add(otherS);
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

    //
    public List<Node> getCandidateInferences(int n) throws Exception {
        if (rankedAnalogies.isEmpty()) {
            System.out.println("No mappings found! run greedyMatching first or try to change topics.");
            return Collections.emptyList();
        }

        if (n==0) {
            System.out.println("n can't be 0, running method with n=1 instead");
            n = 1;
        }

        List<Node> candidateStructures = new ArrayList<>(unMatchedStruct);
        candidateStructures.sort((a, b) -> {
            int cmp = Double.compare(kb.richness(b), kb.richness(a));
            if (cmp != 0) return cmp;
            return new Parse().toFlat(a).compareTo(new Parse().toFlat(b));
        });

        List<Node> candidates = new ArrayList<>();
        int count = 0;
        for (Node seedNode : candidateStructures) {
            if (count == n) break;
            count++;
            Parse p = new Parse();
            p.parseNode(seedNode);

            Node candidate = seedNode.deepCopy();
            Node cand_Iter = candidate;
            boolean flag = true;
            while (cand_Iter != null) {
                String keyT = rankedAnalogies.getFirst().getOrDefault(cand_Iter.keyword,"");
                if (cand_Iter.keyword == null || cand_Iter.keyword.isEmpty()) {
                    cand_Iter = cand_Iter.children;
                    continue;
                }
                if (keyT.isEmpty()) {
                    flag=false;
//                    System.out.println("No Mapping for keyword ->" + cand_Iter.keyword);
                }
                cand_Iter.keyword = keyT;
                cand_Iter = cand_Iter.children;
            }
            if (flag) {
                candidates.add(candidate);
            }
        }

        // Sorting candidates by richness
        candidates.sort((a, b) -> Double.compare(kb.richness(b), kb.richness(a)));

        return candidates;
    }

    public List<List<Node>> coalesceInferences(List<Node> candidates) {
        List<List<Node>> groups = new ArrayList<>();
        List<Map<String, String>> groupMappings = new ArrayList<>();

        for (Node candidate : candidates) {
            Map<String, String> candidateMapping = new LinkedHashMap<>(rankedAnalogies.getFirst());

            boolean merged = false;
            for (int i = 0; i < groups.size(); i++) {
                if (isConsistent(groupMappings.get(i), candidateMapping)) {
                    groups.get(i).add(candidate);
                    groupMappings.get(i).putAll(candidateMapping);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                List<Node> newGroup = new ArrayList<>();
                newGroup.add(candidate);
                groups.add(newGroup);
                groupMappings.add(new LinkedHashMap<>(candidateMapping));
            }
        }

        groups.sort((a, b) -> Integer.compare(b.size(), a.size()));
        return groups;
    }

    // Returns coalesced groups ranked, if candidates have same richness, returns all with best richness
    public List<Map.Entry<List<Node>, Double>> rankCoalescedInferences(
            List<List<Node>> coalescedGroups) {

        List<Map.Entry<List<Node>, Double>> scored = new ArrayList<>();

        for (List<Node> group : coalescedGroups) {
            double score = CombinedQuality(matchedStruct, group);
            scored.add(Map.entry(group, score));
        }

        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return scored;
    }

    public double CombinedQuality(Set<Node> matched, List<Node> inferences) {
        double base = matched.stream()
                .mapToDouble(n -> Math.pow(kb.richness(n), 3))
                .sum();

        double InfScore = inferences.stream()
                .mapToDouble(n -> Math.pow(kb.richness(n), INFERENCE_POWER))
                .sum();

        return base + InfScore;
    }

}
