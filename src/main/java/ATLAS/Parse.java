package ATLAS;
import java.text.ParseException;
import java.util.*;


public class Parse {

    private Queue<String> predicates = new LinkedList<>();
    private Queue<String> keywords = new LinkedList<>();


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
        String[] tokens = input.replace(")", "").split("\\(");
        tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        if(tokens.length == 0){
            throw new ParseException("No tokens found after stripping parentheses.", 0);
        }
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

    public void printIndent(Node node){
        Node current = node;
        if(node == null) {
            System.out.println("[ERROR] Cannot print: Node is empty.");
            return;
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

    public Boolean compare(Parse p1) throws ParseException {
        List<String[]> result = new ArrayList<>();
        if(p1.getHead() == null || this.head == null){
            throw new ParseException("Nodes can't be null", ((this.head == null) ? 1 : 2));
        }

        if (p1.getPredicates().size() != this.getPredicates().size()) {
            return false;
        }

        List<String> p1Keywords = new ArrayList<>(p1.getKeywords());
        List<String> p2Keywords = new ArrayList<>(this.getKeywords());

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


        return p1.getPredicates().equals(this.getPredicates());

    }

    public LinkedList<String[]> getKeywordMapping(Parse p1) throws ParseException {
        LinkedList<String[]> result = new LinkedList<>();
        if(this.head == null){ // Clean this
            return result;
        }

        if (compare(p1)) {
            String[] p1keywords = p1.getKeywords().toArray(new String[0]);
            int i = 0;
            for (String key : this.keywords) {
                result.add(new String[] {key, p1keywords[i++]});
            }
        } else {
            System.out.println("Structure not equal");
            return result;
        }

        return result;

    }

}
