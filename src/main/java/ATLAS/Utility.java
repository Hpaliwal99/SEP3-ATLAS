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
    public static Boolean compare(Parse p1, Parse p2) throws ParseException {

        if(p1.getHead() == null || p2.getHead() == null){
            throw new ParseException("Nodes can't be null", ((p1.getHead() == null) ? 1 : 2));
        }

        if (p1.getPredicates().size() != p2.getPredicates().size()) {
            return false;
        }

        List<String> p1Keywords = new ArrayList<>(p1.getKeywords());
        List<String> p2Keywords = new ArrayList<>(p2.getKeywords());

        for(int i = 0; i < p1Keywords.size(); i++){
            String p1Keyword = p1Keywords.get(i);
            String p2Keyword = p2Keywords.get(i);
            if(p1Keyword.isEmpty() || p2Keyword.isEmpty()){
                continue;
            }
            if(p1Keyword.charAt(0) == '*' ^ p2Keyword.charAt(0) == '*'){
                return false;
            }
        }


        return p1.getPredicates().equals(p2.getPredicates());

    }

    public static LinkedList<String[]> getKeywordMapping(Parse p1, Parse p2) throws ParseException {
        LinkedList<String[]> result = new LinkedList<>();

        if(p1.getHead() == null || p2.getHead() == null){
            throw new ParseException("Nodes can't be null", ((p1.getHead() == null) ? 1 : 2));
        }

        if (Utility.compare(p1, p2)) {
            String[] p1keywords = p1.getKeywords().toArray(new String[0]);
            int i = 0;
            for (String key : p2.getKeywords()) {
                result.add(new String[] {key, p1keywords[i++]});
            }
        } else {
            System.out.println("Structure not equal");
            return result;
        }

        return result;

    }

}




