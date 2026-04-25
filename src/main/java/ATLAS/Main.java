package ATLAS;

import java.util.List;
import java.util.Map;
import java.util.Objects;



public class Main {
    public static void main(String[] args) throws Exception {
//
////        String input1 = "(work in *scientist (some lab (that (conduct experiment))))";
//        String input1 = "(Some artist (create art (with paint)))";
////        String input2 = "(work in *priest (some church (that (conduct sermon))))";
//        String input2 = "(Some programmer (create code (with computer)))";
//        Parse parse1 = new Parse();
//        Parse parse2 = new Parse();
//        Node root = parse1.parse(input1);
//        Node root2 = parse2.parse(input2);
//
//        System.out.println();
//        System.out.println("Printing Parse:");
//        System.out.println(parse1.printIndent(root));
//        System.out.println();
//        System.out.println("Printing flattened Parse:");
//        System.out.println(parse1.toFlat(root));
//        System.out.println();
//        parse1.printQueues();
//        System.out.println();
//
//        parse1.replaceKeywords(root);
//        System.out.println(parse1.toFlat(root));
//        System.out.println();
//        System.out.println(parse1.printIndent(root));
//
//        System.out.println(Utility.compare(parse1, parse2));
//        System.out.println(Utility.toStringLL(Utility.getKeywordMapping(parse1, parse2))); //make custom toString
//        System.out.println(Utility.StringCompare(input1, input2));
//        System.out.println(Utility.toStringLL(Utility.getStringKeywordMapping(input1,input2)));
//        RulesUtil rulesUtil = new RulesUtil();
//        rulesUtil.loadRules("src/main/java/ATLAS/rewrite rules.txt");
//
//        Map<String, List<Rule>> ruleMap =  rulesUtil.getRuleMap();
//
//
//        List<Node> out = rulesUtil.rewrite("(are_chronicled_by *hero myth (are_enslaved_by vigilante tragedy))");
//
//        System.out.println();
//        for (Node n : out) {
//            Parse z = new Parse();
//            System.out.println(z.printIndent(n));
//
//            System.out.println();
//        }
//        for (Node n : out) {
//            Parse z = new Parse();
//            System.out.println(z.toFlat(n));
//            System.out.println();
//        }

//        KnowledgeBase kb = new KnowledgeBase();
//        kb.load("src/main/java/ATLAS/knowledge.txt");

        // 4.2 - get all structures about a topic
//        List<Node> heroStructures = kb.getStructures("hero");
//        List<String> sources = kb.getSources("hero");
//        System.out.println("Possible sources for hero: " + sources);
//        System.out.println("Structures about hero");
//        for (Node n : heroStructures) {
//
//            System.out.println(n.toString());
//        }
//        List<String> ranked = kb.rankSources("hero");
//        System.out.println("Ranked sources for hero: " + ranked);

//        System.out.println("\nindex: " + kb.index);
//        System.out.println("\nShape: " + kb.shapeIndex);

//        List<String> rankbytarget = kb.rankSources("hero");
//
//        System.out.println("rankbytarget: " + rankbytarget);

        Analogy analogy = new Analogy();
//        Map<String, String> result = analogy.bestAnalogy("priest", "scientist");
//        System.out.println(result);
        List<Map<String, String>> ranked = analogy.rankedAnalogies("artist", "programmer");
        System.out.println(ranked);

//        List<Map.Entry<String,Integer>> list = analogy.topSources("programmer");
//        System.out.println(list);
//        Map<String, List<String>> allRankings = kb.rankAllTopics();
//
//        for (Map.Entry<String, List<String>> entry : allRankings.entrySet()) {
//            System.out.println("Target: " + entry.getKey());
//            System.out.println("Ranked sources: " + entry.getValue());
//            System.out.println();
//        }




    }
}