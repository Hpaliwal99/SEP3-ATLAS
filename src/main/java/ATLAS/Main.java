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
        RulesUtil rulesUtil = new RulesUtil();
        rulesUtil.loadRules("src/main/java/ATLAS/rewrite rules.txt");

        Map<String, List<Rule>> ruleMap =  rulesUtil.getRuleMap();


        List<List<Node>> out = rulesUtil.rewrite("(are_chronicled_by *hero myth (are_enslaved_by vigilante tragedy))");

        System.out.println();
        for (List<Node> n : out) {
            for (Node node : n) {
                System.out.println(node);
            }
            System.out.println();
        }
        for (List<Node> n : out) {
            Parse z = new Parse();
            System.out.println(z.toFlat(Utility.listToChain(n)));
            System.out.println();
        }





    }
}