package ATLAS;

public class Rule {
    String OriginalPredicate;
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
        OriginalPredicate = parts[0];

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

}
