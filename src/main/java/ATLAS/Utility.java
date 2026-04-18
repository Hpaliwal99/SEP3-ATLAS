package ATLAS;

import java.text.ParseException;
import java.util.*;


public class Utility {

    public static LinkedList<String[]> tokenizer(String a) throws ParseException {
        if(a == null || a.isBlank()){
            throw new ParseException("Empty String", 0);
        }
        LinkedList<String[]> tokens = new LinkedList<>();
        String[] tokensA = a.replace(")", "").split("\\(");
        tokensA = Arrays.copyOfRange(tokensA, 1, tokensA.length);
        if(tokensA.length == 0){
            throw new ParseException("No tokens found after stripping parentheses.", 0);
        }
        for(int i = 0; i < tokensA.length; i++){
            tokens.add(tokensA[i].split(" "));
        }
        return tokens;
    }

    public static Boolean StringCompare(String A, String B) throws ParseException {
        if(A == null || B == null){
            throw new ParseException("String compare received null argument", 0);
        }
        LinkedList<String[]> tokensA = Utility.tokenizer(A);
        LinkedList<String[]> tokensB = Utility.tokenizer(B);

        //check depth is the same
        if(tokensA.size() != tokensB.size()){
            System.out.println("[StringCompare] Depth mismatch: A has " + tokensA.size() + " group(s), B has " + tokensB.size() + " group(s). -> false");
            return false;
        }
        String predicate = "";
        String keyword = "";

        // Check equal length of tokens Array
        for(int i = 0; i < tokensA.size(); i++){
            String[] TA = tokensA.get(i);
            String[] TB = tokensB.get(i);

            if(TA.length != TB.length){
                System.out.println("[StringCompare] Token length mismatch at group " + i + ": A=" + Arrays.toString(TA) + ", B=" + Arrays.toString(TB) + " -> false");
                return false;
            }
            if(TA.length == 1 ){
                if(!TA[0].equals(TB[0])){
                    System.out.println("[StringCompare] Single-token mismatch at group " + i + ": \"" + TA[0] + "\" vs \"" + TB[0] + "\" -> false");
                    return false;
                }
            }
            else if(TA.length >= 2){
                for(int j = 0; j < TA.length - 1; j++){
                    if(!TA[j].equals(TB[j])){
                        System.out.println("[StringCompare] Predicate mismatch at group " + i + ", position " + j + ": \"" + TA[j] + "\" vs \"" + TB[j] + "\" -> false");
                        return false;
                    }
                }
                if(TA[TA.length-1].charAt(0) == '*' ^ TB[TB.length-1].charAt(0) == '*'){
                    System.out.println("[StringCompare] Wildcard mismatch at group " + i + ": A last=\"" + TA[TA.length - 1] + "\", B last=\"" + TB[TB.length - 1] + "\" -> false");
                    return false;
                }

            }
        }
        return true;

    }

    public static LinkedList<String[]> getStringKeywordMapping(String A, String B) throws ParseException {
        if(A == null || B == null){
            throw new ParseException("getStringKeywordMapping received null argument", 0);
        }
        LinkedList<String[]> result = new LinkedList<>();

        if (Utility.StringCompare(A,B)) {
            LinkedList<String[]> tokensA = Utility.tokenizer(A);
            LinkedList<String[]> tokensB = Utility.tokenizer(B);

            for(int i = 0; i < tokensA.size(); i++){
                String[] TA = tokensA.get(i);
                String[] TB = tokensB.get(i);
                if (TA.length >= 2){
                    result.add(new String[]{TA[TA.length-1],TB[TB.length-1]});
                }
            }
            if(result.isEmpty()){
                System.out.println("[getStringKeywordMapping] Strings matched but no keyword pairs found");
            }
        } else {
            return result;
        }

        return result;
    }

    public static Boolean compare(Parse p1, Parse p2) throws ParseException {
        if(p1 == null || p2 == null){
            throw new ParseException("compare received null argument", 0);
        }
        if(p1.getHead() == null || p2.getHead() == null){
            throw new ParseException("Nodes can't be null", ((p1.getHead() == null) ? 1 : 2));
        }

        if (p1.getPredicates().size() != p2.getPredicates().size()) {
            System.out.println("[compare] Predicate count mismatch: p1=" + p1.getPredicates().size() + ", p2=" + p2.getPredicates().size() + " -> false");
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
                System.out.println("[compare] Wildcard mismatch at keyword index " + i + ": p1=\"" + p1Keyword + "\", p2=\"" + p2Keyword + "\" -> false");
                return false;
            }
        }


        return p1.getPredicates().equals(p2.getPredicates());

    }

    public static LinkedList<String[]> getKeywordMapping(Parse p1, Parse p2) throws ParseException {
        if(p1 == null || p2 == null){
            throw new ParseException("getKeywordMapping received null argument", 0);
        }
        LinkedList<String[]> result = new LinkedList<>();

        if(p1.getHead() == null || p2.getHead() == null){
            throw new ParseException("Nodes can't be null", ((p1.getHead() == null) ? 1 : 2));
        }

        if (Utility.compare(p1, p2)) {
            String[] p1keywords = p1.getKeywords().toArray(new String[0]);


            int i = 0;
            for (String key : p2.getKeywords()) {
                if (!key.isEmpty()) {
                    result.add(new String[] { p1keywords[i++], key });
                }
                else  {
                    i++;
                }
            }

        } else {
            System.out.println("Structure not equal");
            return result;
        }

        return result;

    }

    public static String toStringLL (LinkedList<String[]> list) {
        return Arrays.deepToString(list.toArray());
    }

    public static Node listToChain(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;

        for (int i = 0; i < nodes.size() - 1; i++) {
            nodes.get(i).depth = i;
            nodes.get(i).children = nodes.get(i + 1);
        }
        nodes.getLast().children = null;

        return nodes.getFirst();
    }

    public static List<Node> flattenChain(Node head) {
        List<Node> list = new ArrayList<>();
        for (Node n = head; n != null; n = n.children) {
            list.add(n);
        }
        return list;
    }
}




