package ATLAS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class KnowledgeBase {

    private final Map<String, List<Node>> index = new HashMap<>();
    private final Map<String, List<Node>> shapeIndex = new HashMap<>();

    private final double INFERENCE_POWER = Config.inferencePower();

    public Map<String, List<Node>> getIndex() {
        return index;
    }

    public Map<String, List<Node>> getShapeIndex() {
        return shapeIndex;
    }

    // Load from file
    public void load(String filename) throws Exception {
        RulesUtil rulesUtil = new RulesUtil();
        rulesUtil.loadRules(Config.rulesPath());

//        try (BufferedReader br = new BufferedReader(new FileReader(filename), 1 << 16)) {
//            String line;
//            int count = 0;
//            while ((line = br.readLine()) != null) {
//                count++;
//                String[] parts = line.split("\t", 2);
//                if (parts.length < 2) {
//                    System.out.println(count + "\t" + line);
//                    continue;
//                }
//                line = parts[1].trim();
//                if (line.isEmpty()) continue;
//
//                List<Node> combos = rulesUtil.rewrite(line);
//                for (Node root : combos) {
//                    List<String> topics = findTopics(root);
//                    String shape = shapeHash(root);
//                    shapeIndex.computeIfAbsent(shape, k -> new ArrayList<>()).add(root);
//                    for (String topic: topics) {
//                        index.computeIfAbsent(topic, k -> new ArrayList<>()).add(root);
//                    }
//                }
//            }

        Scanner scanner = new Scanner(new File(filename));

        int count = 0;
        while (scanner.hasNextLine()) {
            count++;
            String line = scanner.nextLine().trim();
            try {
                line = line.split("\t")[1].trim();
            }  catch (Exception e) {
                System.out.println(count + ":\t" + line);
            }
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
            System.out.println("Loaded " + count + " lines from " + filename + "\n");

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
        StringBuilder sb = new StringBuilder();
        buildHash(node, sb);
        return sb.toString().intern();
    }

    private void buildHash(Node node, StringBuilder sb) {
        if (node == null) return;
        String pred = node.Predicate.split("\\s+")[0];
        sb.append('(').append(pred);
        buildHash(node.children, sb);
        sb.append(')');
    }

    // get relevant topics from target
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
            int count = 0;
            count = cur.Predicate.trim().split(" ").length;
            if (!cur.Topic.isEmpty()) {
                count--;
            }
            sum += count * Math.pow(10, depth);   // one node per depth level in a chain
//            sum += Math.pow(10, depth);
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

    public Double quality (String target, String source) {
        List<Node> targetStructures = getStructures(target);
        double score = 0.0;

        for(Node node : targetStructures) {
            String shape = shapeHash(node);
            List<Node> matches = shapeIndex.getOrDefault(shape, Collections.emptyList());
            double r3 = Math.pow(richness(node), 3);
            for(Node match : matches) {
                List<String> topics = findTopics(match);
                for(String topic : topics) {
                    if(!topic.equals(target)) {
                        score += r3;
                    }
                }
            }
        }

        return score;
    }

    public Double Infquality(List<Node> colCand) {

        double InfScore = 0.0;
        if(colCand.isEmpty()) return InfScore;

        for (Node node : colCand) {
            double rI = Math.pow(richness(node), INFERENCE_POWER);
            InfScore += rI;
        }

        return InfScore;
    }


}
