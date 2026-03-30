package ATLAS;

import java.text.ParseException;
import java.util.*;

public class Utility {

    public Boolean StringCompare(String A, String B) throws ParseException {
        int index = 0;
        String[] tokensA = A.replace(")", "").split("\\(");
        tokensA = Arrays.copyOfRange(tokensA, 1, tokensA.length);
        if(tokensA.length == 0){
            throw new ParseException("No tokens found after stripping parentheses.", 0);
        }
        System.out.println(Arrays.toString(tokens)); // Debug Line
        String predicate = "";
        String keyword = "";

        Node current = null;

        // Check equal length of tokens Array

        for (String token : tokens) {
            String[] tokenList = token.split(" ");
            if (tokenList.length >= 2){
                keyword = tokenList[tokenList.length - 1];
                predicate = String.join(" ", Arrays.copyOfRange(tokenList, 0, tokenList.length-1));
//                System.out.println(predicate); // Debug Line
//                predicates.add(predicate);
//                keywords.add(keyword);
            }
            else if (tokenList.length == 1){
                predicate = tokenList[0];
                keyword = "";
//                keywords.add(keyword);
//                predicates.add(predicate);
            }
            if (index == 0) {
//                this.head = new Node(predicate, keyword, index);
//                current = this.head;
                index++;
            } else {
//                current.children = new Node(predicate, keyword, index);
//                current = current.children;
                index++;

            }
        }
        if(this.head == null){
            throw new ParseException("parsing produced nothing", 0);
        }

        return this.head;
    }

}


