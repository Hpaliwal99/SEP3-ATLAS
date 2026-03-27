package ATLAS;
import java.util.*;

class Node{
    String Predicate;
    String keyword;
    List<Node> children = new ArrayList<>();

    Node(String predicate, String keyword){
        this.Predicate = predicate;
        this.keyword = keyword;
    }
}

public class Parse {
    private Queue<String> predicates = new LinkedList<>();
    private Queue<String> keywords = new LinkedList<>();
    private int index = 0;

    public Node parse(String input){
        return parseNode(input);
    }
    private Node parseNode(String input){
        //character processing needed

        if (tokens.length >= 2){
            keyword = tokens[tokens.length - 1];
            for (int i = 0; i < tokens.length - 1; i++){
                predicate += tokens[i] + " ";
            }
            predicate = predicate.trim();
            predicates.add(predicate);
            keywords.add(keyword);
        }
        else if (tokens.length == 1){
            predicate = tokens[0];
            keyword = "";
            keywords.add(keyword);
            predicates.add(predicate);
        }
        Node node = new Node(predicate, keyword);
        node.children = children;

        return node;
    }

    public void printIndent(Node node, int depth){
        if(node == null) return;

        for(int i = 0; i < depth; i++){
            System.out.print("  ");
        }
        System.out.print("(" + node.Predicate);
        if(node.keyword != null){
            System.out.print(" " + node.keyword);
        }
        if(node.children.isEmpty()){
            System.out.print(")");
            return;
        }
        System.out.println();

        for(Node child : node.children){
            printIndent(child, depth + 1);
        }

        System.out.print(")");
    }
    public void printQueues(){
        System.out.println("predicates: " + predicates);
        System.out.println("keywords: " + keywords);
    }

    public String toFlat(Node node){
        if(node == null) return "";

        String result = "(" + node.Predicate;
        if(node.keyword != null){
            result += " " + node.keyword;
        }
        for(Node child : node.children){
            result += " " + toFlat(child);
        }
        result += ")";
        return result;
    }
    private int counter = 0;
    public void replaceKeywords(Node node){
        if(node == null) return;
        if(node.keyword != ""){
            node.keyword = String.valueOf(counter++);
        }
        for(Node child : node.children){
            replaceKeywords(child);
        }
    }
    public static void main(String[] args){
        String input = "(work in scientist (some lab (that (conduct experiment))))";
        Parse parse1 = new Parse();
        Node root = parse1.parse(input);

        parse1.printIndent(root, 0);
        System.out.println();
        System.out.println(parse1.toFlat(root));
        parse1.printQueues();

        parse1.replaceKeywords(root);
        System.out.println(parse1.toFlat(root));
    }
}
