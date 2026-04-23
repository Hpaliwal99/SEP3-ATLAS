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
    String context;
    boolean negate;
    boolean swap;
    boolean insert;

    public Rule(String line){
        this.context=null;
        parseRule(line);
    }

    @Override
    public String toString() {
        return "Rule{verb=" + preposition +
                ", arg=" + colonArgument +
                ", by=" + byWord + "}";
    }

    private void parseRule(String line){
        String[] parts = line.trim().split("\\s+",2);
        if(parts.length < 2){
            return;
        }
        originalPredicate = parts[0];

        String rightSide = parts[1];

        while(!rightSide.isEmpty()){
            char c = rightSide.charAt(0);
            if(c == '!'){
                negate = true;
                rightSide = rightSide.substring(1);
            }
            else if(c == '<'){
                swap = true;
                rightSide = rightSide.substring(1);
            }
            else if(c == '^'){
                insert = true;
                rightSide = rightSide.substring(1);
            }
            else{
                break;
            }
        }

        String leftOfAmp;
        if(rightSide.contains("&")){
            String[] ampSplit = rightSide.split("&");
            leftOfAmp = ampSplit[0];
            byWord = ampSplit[1];
        }
        else{
            leftOfAmp = rightSide;
            byWord = null;
        }

        String verbPreposition = leftOfAmp;
        if(leftOfAmp.contains(":")) {
            String[] colonSplit = leftOfAmp.split(":", 2);

//            System.out.println("colonSplit length: " + colonSplit.length + " | value: " + Arrays.toString(colonSplit));
//            if (colonSplit.length < 2) {
//                throw new IllegalArgumentException("Malformed rule - expected something after ':' in: " + leftOfAmp);
//            }
            verbPreposition = colonSplit[0];
            colonArgument = colonSplit[1];

            if(colonArgument.endsWith("*")){
                starAfterWord = true;
                colonArgument = colonArgument.substring(0, colonArgument.length() - 1);
            }

        }


        String[] vp = verbPreposition.split("_");
        newVerb = vp[0];
        if(vp.length > 1){
            preposition = vp[1];
        }
        else {
            preposition = null;
        }


    }





}
