package ATLAS;

import java.io.File;
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


}
