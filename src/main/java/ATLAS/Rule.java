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

        String[] colonSplit = leftOfAmp.split(":");
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
