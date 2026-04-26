package ATLAS;
import java.text.ParseException;
import java.util.*;


public class Parse {

    private Queue<String> predicates = new LinkedList<>();
    private Queue<String> keywords = new LinkedList<>();


    private String topic;

    private Node head = null;

    public Queue<String> getPredicates() {
        return predicates;
    }

    public Queue<String> getKeywords() {
        return keywords;
    }

    public Node getHead() {
        return this.head;
    }

    public String getTopic() {
        return topic;
    }

    private void checkInput(String input) throws ParseException {
        if(input == null){
            throw new ParseException("Input can't be null", 0);
        }

        if(input.isBlank()){
            throw new ParseException("Input can't be blank or empty", 0);
        }

        String trimmed = input.strip();
        if(trimmed.charAt(0) != '('){
            throw new ParseException("Input must begin with (", 0);
        }

        int depth = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth < 0) {
                    throw new ParseException(
                            "Unexpected closing ')' with no matching '('.", i);
                }
            }
        }

        if(trimmed.contains("()")){
            throw new ParseException("Empty parenthesised group '()' found", 0);
        }
    }

    public Node parse(String input) throws ParseException {
        checkInput(input);
        this.head = parseNode(input);
        return this.head;
    }


    private Node parseNode(String input) throws ParseException {
        int index = 0;

        LinkedList<String[]> tokens = Utility.tokenizer(input);
//        System.out.println(Utility.toStringLL(tokens));

        String predicate = "";
        String keyword = "";

        Node current = null;

        for (String[] tokenList : tokens) {
            if (tokenList.length >= 2) {
                this.topic = String.valueOf(Arrays.stream(tokenList).filter(s -> s.contains("*")).findAny()).strip();
//                System.out.println(topic);
                keyword = tokenList[tokenList.length - 1];
                predicate = String.join(" ", Arrays.copyOfRange(tokenList, 0, tokenList.length - 1));
//                System.out.println(predicate); // Debug Line
                this.predicates.add(predicate);
                this.keywords.add(keyword);
            } else if (tokenList.length == 1) {
                predicate = tokenList[0];
                keyword = "";
                this.keywords.add(keyword);
                this.predicates.add(predicate);
            }
            if (index == 0) {
                this.head = new Node(predicate, keyword, index);
                current = this.head;
                index++;
            } else {
                current.children = new Node(predicate, keyword, index);
                current = current.children;
                index++;

            }
        }

        if(this.head == null){
            throw new ParseException("parsing produced nothing", 0);
        }

        return this.head;
    }

    public String printIndent(Node node){
        StringBuilder sb = new StringBuilder();
        Node current = node;

        if(node == null) {
            throw new IllegalArgumentException("[ERROR] Cannot print: Node is empty.");
        }

        while (current != null) {
            sb.append(current.depth).append(" ");  //Debug for depth/line count

            sb.append("  ".repeat(Math.max(0, current.depth)));
            sb.append("(");
            sb.append(current.Predicate);
            if(current.keyword != null){
                sb.append(" ");
                sb.append(current.keyword);
            }

            if (current.children == null){
                sb.append(")".repeat(Math.max(0, current.depth + 1)));
            }
            sb.append("\n");

            current = current.children;
        }

        return sb.toString();

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
