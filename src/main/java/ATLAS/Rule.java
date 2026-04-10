package ATLAS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Rule {
    String originalPredicate;
    String newVerb;
    String preposition;
    String colonArgument;
    boolean starAfterWord;
    String byWord;
    boolean negate;
    boolean swap;
    boolean insert;

    public Rule(String line){
        parseRule(line);
    }

    private void parseRule(String line){
        String[] parts = line.split(" ");
        originalPredicate = parts[0];

        String rightSide = parts[1];

        if(rightSide.startsWith("!")){
            negate = true;
            rightSide = rightSide.substring(1);
        }

        if(rightSide.startsWith("<")){
            swap = true;
            rightSide = rightSide.substring(1);
        }

        if(rightSide.startsWith("^")){
            insert = true;
            rightSide = rightSide.substring(1);
        }

        String[] bySplit = rightSide.split(" ");
        String left = bySplit[0];
        byWord = bySplit[1];

        String[] colonSplit = rightSide.split(":");
        String verbPreposition = colonSplit[0];
        colonArgument = colonSplit[1];

        if(colonArgument.endsWith("*")){
            starAfterWord = true;
            colonArgument = colonArgument.replace("*", "");
        }

        String[] vp = verbPreposition.split("_");
        newVerb = vp[0];
        preposition = vp[1];
    }

    Map<String, List<Rule>> ruleMap = new HashMap<>();

    public void loadRules(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));

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
                ruleMap.putIfAbsent(r.originalPredicate, new ArrayList<Rule>());
                ruleMap.get(r.originalPredicate).add(r);

            }
        }
    }



}
