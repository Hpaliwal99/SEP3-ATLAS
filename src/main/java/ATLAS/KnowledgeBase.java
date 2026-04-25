package ATLAS;

import java.io.File;
import java.util.*;

public class KnowledgeBase {

    private Map<String, List<Node>> index = new HashMap<>();
    private Map<String, List<Node>> shapeIndex = new HashMap<>();

    public Map<String, List<Node>> getIndex() {
        return index;
    }

    public Map<String, List<Node>> getShapeIndex() {
        return shapeIndex;
    }

    // Load from file
    public void load(String filename) throws Exception {
        RulesUtil rulesUtil = new RulesUtil();
        rulesUtil.loadRules("src/main/java/ATLAS/rewrite rules.txt");

        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
//            line = line.split("\t")[1].trim();
//            System.out.println(line);
            if(line.isEmpty()) continue;

            List<Node> combos = rulesUtil.rewrite(line);
            for (Node root : combos) {

                List<String> topics = findTopics(root);
                String shape = shapeHash(root);
//                System.out.println("Shape: " + shape);

                shapeIndex.putIfAbsent(shape, new ArrayList<>());
                shapeIndex.get(shape).add(root);
    //            System.out.println("Line: " + line);
    //            System.out.println("Topics found: " + topics);
//                System.out.println();
                for(String topic : topics) {
                    index.putIfAbsent(topic, new ArrayList<>());
                    index.get(topic).add(root);
                }
            }
        }
    }

    // Extract topic from node struct
    private List<String> findTopics(Node node) {
        List<String> topics = new ArrayList<>();

        Node cur = node;
        while(cur != null) {
            if(cur.keyword != null && cur.keyword.startsWith("*")) {
                topics.add(cur.keyword.substring(1).intern());
            }
            if(cur.Predicate != null) {
                for(String word : cur.Predicate.split("\\s+")){
                    if(word.startsWith("*")) {
                        topics.add(word.substring(1).intern());
                    }
                }
            }
            cur = cur.children;
        }
        return topics;
    }

    // Get structs from topic
    public List<Node> getStructures(String topic) {
        return index.getOrDefault(topic, new ArrayList<>());
    }

    // Strip Struct for hashmap
    public String shapeHash(Node node) {
        StringBuffer sb = new StringBuffer();
        buildHash(node, sb);
        return sb.toString().intern();
    }

    private void buildHash(Node node, StringBuffer sb) {
        if (node == null) return;
        String pred = node.Predicate.split("\\s+")[0];
        sb.append('(').append(pred);
        buildHash(node.children, sb);
        sb.append(')');
    }

    // get relevant topics from structs
    public List<String> getSources(String target) {
        List<Node> targetStructures = getStructures(target);
        Set<String> sources = new HashSet<>();

        for(Node node : targetStructures) {
            String shape = shapeHash(node);
            List<Node> matches = shapeIndex.getOrDefault(shape, Collections.emptyList());

            for(Node match : matches) {
                List<String> topics = findTopics(match);
                for(String topic : topics) {
                    if(!topic.equals(target)) {
                        sources.add(topic);
                    }
                }
            }
        }
        return new ArrayList<>(sources);
    }

    // Calculate richness of struct
    public Double richness(Node node) {
        if (node == null) return 0.0;

        // Single pass: count depth directly, no intermediate Map needed
        double sum = 0.0;
        int depth = 0;
        for (Node cur = node; cur != null; cur = cur.children, depth++) {
            sum += Math.pow(10, depth);   // one node per depth level in a chain
        }
        return Math.log10(sum);
    }

    // Rank Topics by richness
    public List<String> rankSources(String target) {
        List<Node> targetStructures = getStructures(target);
        Map<String, Double> scores = new HashMap<>();

        for(Node node : targetStructures) {
            String shape = shapeHash(node);
            List<Node> matches = shapeIndex.getOrDefault(shape, new ArrayList<>());
            double r3 = Math.pow(richness(node), 3);
            for(Node match : matches) {
                for(String topic : findTopics(match)) {
                    if(!topic.equals(target)) {
                        scores.merge(topic, r3, Double::sum);
                    }
                }
            }
        }
        List<String> ranked = new ArrayList<>(scores.keySet());
        ranked.sort((a, b) -> Double.compare(scores.get(b), scores.get(a)));

        System.out.println(target);
        for(String s : ranked) {
            System.out.println(String.format("%.4f",scores.get(s)) + " -> score: " + s);
        }
        System.out.println();
        return ranked;
    }

    // rank all topics against each other.
    public Map<String, List<String>> rankAllTopics() {
        Map<String, List<String>> allRankings = new HashMap<>();

        for (String target : index.keySet()) {
            List<String> ranked = rankSources(target);
            if (!ranked.isEmpty()) {
                allRankings.put(target, ranked);
            }
        }

        return allRankings;
    }
}
