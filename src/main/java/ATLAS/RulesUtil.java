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

        List<Node> originalNodes = Utility.flattenChain(root);

        List<List<List<Node>>> perNodeOptions = new ArrayList<>();

        for (Node cur : originalNodes) {

            // Stripping .0 from predicate
            String cleanPredicate = cur.Predicate.contains(".") ? cur.Predicate.substring(0, cur.Predicate.lastIndexOf(".")) : cur.Predicate.split(" ")[0];
            System.out.println(cleanPredicate);


            List<Rule> rules = getRules(cleanPredicate);
            System.out.println(rules);
            List<List<Node>> ModdedOptionsNode = new ArrayList<>();
            if (!rules.isEmpty()) {
                for (Rule r : rules) {
                    Node res = applyRule(r, cur);
                    ModdedOptionsNode.add(Utility.flattenChain(res));
                }
            } else {
                ModdedOptionsNode.add(Collections.singletonList(cur.copyShallow()));
            }

            perNodeOptions.add(ModdedOptionsNode);
            System.out.println("Size of ModdedNode: " + ModdedOptionsNode.size());
        }

        List<List<List<Node>>> combinations = cartesianProduct(perNodeOptions);

        List<Node> results = new ArrayList<>();
        for (List<List<Node>> combo : combinations) {

            List<Node> chain = new ArrayList<>();
            for (List<Node> subChain : combo) {
                chain.addAll(subChain);
            }
            // fix depths and link
            for (int i = 0; i < chain.size(); i++) {
                chain.get(i).depth = i;
                chain.get(i).children = (i + 1 < chain.size()) ? chain.get(i + 1) : null;
            }
            results.add(chain.getFirst());
        }
        return results;

    }

    private <Node> List<List<Node>> cartesianProduct(List<List<Node>> LoList) {
        List<List<Node>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        for (List<Node> options : LoList) {
            List<List<Node>> newResult = new ArrayList<>();
            for (List<Node> existing : result) {
                for (Node option : options) {
                    List<Node> combo = new ArrayList<>(existing);
                    combo.add(option);
                    newResult.add(combo);
                }
            }
            result = newResult;
        }
        return result;
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

        System.out.println();
        System.out.println(r.toString());
        System.out.println();

        String[] strsplit = cur.Predicate.trim().split("\\s+");
        System.out.println("strsplit = " + Arrays.toString(strsplit));

        String argMid = strsplit.length > 1 ? strsplit[1] : ""; // Middle word
        String argKey = cur.keyword; // Last/key word
//        System.out.println("Before -> Mid = " + argMid + " Key = " + argKey);

        //Swap middle and last/key
        if (r.swap) {
            String tmp = argMid;
            argMid = argKey;
            argKey = tmp;
//            System.out.println("After -> Mid = " + argMid + " Key = " + argKey);
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


        if (r.negate) {
            newChild = new Node("not (" + r.newVerb + " " + argMid, MainKey, cur.depth+1);
        } else {
            newChild = new Node(r.newVerb + " " + argMid, MainKey, cur.depth+1);
        }
        result.children = newChild;

        if (r.preposition != null) {
            newChild.children = new Node(r.preposition, NextKey, cur.depth+2);
            newChild.children.children = null;
        }

//        System.out.println(result.toString());
//        System.out.println(result.children.toString());
//        System.out.println(result.children.children.toString());

        return result;
    }

}
