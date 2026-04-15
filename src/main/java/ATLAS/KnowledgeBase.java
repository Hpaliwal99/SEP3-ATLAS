package ATLAS;

import java.io.File;
import java.util.*;

public class KnowledgeBase {
    Map<String, List<Node>> index = new HashMap<>();
    Map<String, List<Node>> shapeIndex = new HashMap<>();

    public void load(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(line.isEmpty()) continue;

            Parse p = new Parse();
            Node root = p.parse(line);

            List<String> topics = findTopics(root);
            String shape = shapeHash(root);
            System.out.println("Shape: " + shape);

            shapeIndex.putIfAbsent(shape, new ArrayList<>());
            shapeIndex.get(shape).add(root);

//            System.out.println("Line: " + line);
//            System.out.println("Topics found: " + topics);
            System.out.println();
            for(String topic : topics) {
                index.putIfAbsent(topic, new ArrayList<>());
                index.get(topic).add(root);
            }
        }
    }

    private List<String> findTopics(Node node) {
        List<String> topics = new ArrayList<>();

        Node cur = node;
        while(cur != null) {
            if(cur.keyword != null && cur.keyword.startsWith("*")) {
                topics.add(cur.keyword.substring(1));
            }
            if(cur.Predicate != null) {
                for(String word : cur.Predicate.split("\\s+")){
                    if(word.startsWith("*")) {
                        topics.add(word.substring(1));
                    }
                }
            }
            cur = cur.children;
        }
        return topics;
    }

    public List<Node> getStructures(String topic) {
        return index.getOrDefault(topic, new ArrayList<>());
    }

    private String shapeHash(Node node) {
        if(node == null) return "";

        String pred = node.Predicate.split("\\s+")[0];
        return "(" + pred + shapeHash(node.children) + ")";
    }

    public List<String> getSources(String target) {
        List<Node> targetStructures = getStructures(target);
        Set<String> sources = new HashSet<>();

        for(Node node : targetStructures) {
            String shape = shapeHash(node);
            List<Node> matches = shapeIndex.getOrDefault(shape, new ArrayList<>());

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

    private Double richness(Node node) {
        if (node == null) return 0.0;

        // count nodes at each relative depth from this node's root
        Map<Integer, Integer> countPerDepth = new HashMap<>();
        Node cur = node;
        int relativeDepth = 0;
        while (cur != null) {
            countPerDepth.merge(relativeDepth, 1, Integer::sum);
            cur = cur.children;
            relativeDepth++;
        }

        // sum count_i * 10^i
        double sum = 0;
        for (Map.Entry<Integer, Integer> entry : countPerDepth.entrySet()) {
            int depth = entry.getKey();
            int count = entry.getValue();
            sum += count * Math.pow(10, depth);
        }

        return (Double) Math.log10(sum);
    }

    public List<String> rankSources(String target) {
        List<Node> targetStructures = getStructures(target);
        Map<String, Double> scores = new HashMap<>();

        for(Node node : targetStructures) {
            String shape = shapeHash(node);
            List<Node> matches = shapeIndex.getOrDefault(shape, new ArrayList<>());

            for(Node match : matches) {
                List<String> topics = findTopics(match);
                for(String topic : topics) {
                    if(!topic.equals(target)) {
                        Double r = richness(node);
                        scores.merge(topic, (Double) Math.pow(r, 3), Double::sum);
                    }
                }
            }
        }
        List<String> ranked = new ArrayList<>(scores.keySet());
        ranked.sort((a, b) -> Double.compare(scores.get(b), scores.get(a)));

        for(String s : ranked) {
            System.out.println(s + " -> score: " + scores.get(s));
        }
        return ranked;
    }

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
