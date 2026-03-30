package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Utility {

    public static Boolean StringCompare(String A, String B) throws ParseException {
        String[] tokensA = A.replace(")", "").split("\\(");
        tokensA = Arrays.copyOfRange(tokensA, 1, tokensA.length);
        if(tokensA.length == 0){
            throw new ParseException("No tokens found after stripping parentheses.", 0);
        }

        String[] tokensB = B.replace(")", "").split("\\(");
        tokensB = Arrays.copyOfRange(tokensB, 1, tokensB.length);
        if(tokensB.length == 0){
            throw new ParseException("No tokens found after stripping parentheses.", 0);
        }

        //check depth is the same
        if(tokensA.length != tokensB.length){
            return false;
        }
        String predicate = "";
        String keyword = "";

        // Check equal length of tokens Array
        for(int i = 0; i < tokensA.length; i++){
            String[] tokenListA = tokensA[i].split(" ");
            String[] tokenListB = tokensB[i].split(" ");

            if(tokenListA.length != tokenListB.length){
                return false;
            }
            if(tokenListA.length == 1 ){
                if(!tokenListA[0].equals(tokenListB[0])){
                    return false;
                }
            }
            else if(tokenListA.length >= 2){
                for(int j = 0; j < tokenListA.length - 1; j++){
                    if(!tokenListA[j].equals(tokenListB[j])){
                        return false;
                    }
                }
                if(tokenListA[tokenListA.length-1].charAt(0) == '*' ^ tokenListB[tokenListB.length-1].charAt(0) == '*'){
                    return false;
                }
            }
        }
        return true;


    }
}




