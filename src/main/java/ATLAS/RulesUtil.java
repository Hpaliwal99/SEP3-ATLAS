package ATLAS;

import java.io.File;
import java.text.ParseException;
import java.util.*;

public class RulesUtil {
    Map<String, List<Rule>> ruleMap = new HashMap<>();

    public void loadRules(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));

        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(line.isEmpty()){
                continue;
            }

            String[] parts = line.split("\\s+", 2);
            String predicate = parts[0];
            String rightSide = parts[1];

            String[] ruleParts = rightSide.split(",");

            for(String rulePart : ruleParts){
                rulePart = rulePart.trim();

                Rule r = new Rule(predicate + " " + rulePart);
                String basePredicate = predicate.split(":")[0];

                ruleMap.putIfAbsent(basePredicate, new ArrayList<Rule>());
                ruleMap.get(basePredicate).add(r);

            }
        }
    }

    public List<Rule> getRules (String predicate) {
        return ruleMap.getOrDefault(predicate, new ArrayList<>());
    }

    public Map<String, List<Rule>> getRuleMap() {
        return Collections.unmodifiableMap(ruleMap);
    }

    public List<Node> rewrite(String str) throws ParseException {

        Parse p = new Parse();
        Node root = p.parse(str);

        List<List<Node>> ReWriteOps = new ArrayList<>();
        for (Node cur = root; cur != null; cur = cur.children) {

            // Stripping .0 from predicate
            String cleanPredicate = cur.Predicate.contains(".") ? cur.Predicate.substring(0, cur.Predicate.lastIndexOf(".")) : cur.Predicate.split(" ")[0];
            System.out.println(cleanPredicate);

            List<Rule> rules = getRules(cleanPredicate);
            System.out.println(rules);
            List<Node> ModdedNode = new ArrayList<>();
            if (!rules.isEmpty()) {
                for (Rule r : rules) {
                    Node res = applyRule(r, cur);
                    ModdedNode.add(res);
                    ModdedNode.add(res.children);
                    ModdedNode.add(res.children.children);
                    ReWriteOps.add(ModdedNode);
                }
                    cur = ModdedNode.getLast();
            } else {
                ModdedNode.add(cur);
                ReWriteOps.add(ModdedNode);
            }
            System.out.println("Size of ModdedNode: " + ModdedNode.size());
        }

        //TODO: add Cartesian Prod here

        List<Node> results = new ArrayList<>();
        for (List<Node> combo : ReWriteOps) {
            int i;
            for ( i = 0; i < combo.size() - 1; i++) {
                combo.get(i).depth = i;
                combo.get(i).children = combo.get(i + 1);
            }
                combo.getLast().depth = i;
            combo.getLast().children = null;
            results.add(combo.getFirst());
        }
        return results;

    }

    //exercise      perform_of:exercise*&exercising
    //colonArg = exercise*
    //verbPrep = perform_by
    //NewVerb = perform
    //prep = of
    //byWord = exercising
    public Node applyRule(Rule r, Node cur) {
        Node result;
        Node next = cur.children;

        String[] strsplit = cur.Predicate.trim().split("\\s+");
        System.out.println("strsplit = " + Arrays.toString(strsplit));

        String argMid = strsplit.length > 1 ? strsplit[1] : ""; // Middle word
        String argKey = cur.keyword; // Last/key word
        System.out.println("Before -> Mid = " + argMid + " Key = " + argKey);

        //Swap middle and last/key
        if (r.swap) {
            String tmp = argMid;
            argMid = argKey;
            argKey = tmp;
            System.out.println("After -> Mid = " + argMid + " Key = " + argKey);
        }

        // Assign keywords for rewritten Nodes
        String MainKey;
        String NextKey;
        if (r.colonArgument != null) {
            if (r.starAfterWord) {
                NextKey = argKey;
                MainKey = r.colonArgument;
            } else {
                NextKey = r.colonArgument;
                MainKey = argKey;
            }
        } else {
            NextKey = null;
            MainKey = argKey;
        }

        /**
         * are_chronicled_in	^<communicate_about:historian&chronicling
         * (are_chronicled_in *hero myth)
         * (by chronicling
         *      (communicate historian myth
         *          (about *hero)
         */

        /**
         * are_supplied_with	^<provide_to:supplier&supplying
         * (are_supplied_with stu paper)
         * (by supplying
         *      (provide supplier paper
         *          (to stu)
         */

        // swap then insert for ^<
        // insert and push
        if (r.insert) {
            String tmp = argMid;
            argMid = NextKey;
            NextKey = MainKey;
            MainKey = tmp;
        }


        System.out.println(argMid);
        System.out.println(MainKey);
        System.out.println(NextKey);

        Node newChild;

        result = new Node("by", r.byWord, cur.depth);
//        System.out.println(result.toString());
        //TODO: Add not
        if (r.negate) {
            newChild = new Node("not (" + r.newVerb + " " + argMid, MainKey, cur.depth+1);
        } else {
            newChild = new Node(r.newVerb + " " + argMid, MainKey, cur.depth+1);
        }
//        System.out.println(newChild.toString());
        newChild.children = new Node(r.preposition, NextKey, cur.depth+2);
//        System.out.println(newChild.children.toString());
        newChild.children.children = next;
        result.children = newChild;
//        System.out.println(result.toString());
//        System.out.println(result.children.toString());
//        System.out.println(result.children.children.toString());

        return result;
    }

}
