package ATLAS;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KnowledgeBase {
    Map<String, List<Node>> index = new HashMap<>();

    public void load(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(line.isEmpty()) continue;

            Parse p = new Parse();
            Node root = p.parse(line);

            List<String> topics = findTopics(root);
            System.out.println("Line: " + line);
            System.out.println("Topics found: " + topics);
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
}
