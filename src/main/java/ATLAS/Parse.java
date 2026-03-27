package ATLAS;
import java.util.*;


public class Parse {
    private Queue<String> predicates = new LinkedList<>();
    private Queue<String> keywords = new LinkedList<>();
    public Node head = null;

    public Node parse(String input){
        this.head = parseNode(input);
        return head;
    }

    private Node parseNode(String input){
        int index = 0;
        String[] tokens = input.replace(")", "").split("\\(");
        tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        System.out.println(Arrays.toString(tokens));
        String predicate = "";
        String keyword = "";

        Node current = null;

        for (String token : tokens) {
            String[] tokenList = token.split(" ");
            if (tokenList.length >= 2){
                keyword = tokenList[tokenList.length - 1];
                predicate = String.join(" ", Arrays.copyOfRange(tokenList, 0, tokenList.length-1));
//                System.out.println(predicate); // Debug Line
                predicates.add(predicate);
                keywords.add(keyword);
            }
            else if (tokenList.length == 1){
                predicate = tokenList[0];
                keyword = "";
                keywords.add(keyword);
                predicates.add(predicate);
            }
            if (index == 0) {
                head = new Node(predicate, keyword, index);
                current = head;
                index++;
            } else {
                current.children = new Node(predicate, keyword, index);
                current = current.children;
                index++;

            }
        }


        return head;
    }

    public void printIndent(Node node){
        Node current = node;
        if(node == null) {
            System.out.println("Object is empty");
        }

        while (current != null) {
            System.out.print(current.depth + " "); //Debug for depth/line count
            for(int i = 0; i < current.depth; i++){
                System.out.print("  ");
            }
            System.out.print("(" + current.Predicate);
            if(current.keyword != null){
                System.out.print(" " + current.keyword);
            }

            if (current.children == null){
                for (int i = 0; i <= current.depth; i++){
                    System.out.print(")");
                }
            }
            System.out.println();

            current = current.children;
        }

    }

    public void printQueues(){
        System.out.println("predicates: " + predicates);
        System.out.println("keywords: " + keywords);
    }

    public String toFlat(Node node){
        if(node == null) {
            return "";
        } else {
            String result = "(" + node.Predicate;
            if (node.keyword != null) {
                result += " " + node.keyword;
            }
            return result + " " + toFlat(node.children) + ")";
        }
    }

    private int counter = 0;
    public void replaceKeywords(Node node){
        if(node == null) {
            return;
        } else {
            if (!Objects.equals(node.keyword, "")) {
                node.keyword = String.valueOf(counter++);
            }

            replaceKeywords(node.children);
        }
    }

}
