package ATLAS;
import java.util.*;

public class Node{
    String Predicate = "";
    String keyword = "";
    String Topic = "";
    Node children = null;
    int depth = 0;

    public Node(String predicate, String Keyword, int index){
        if (predicate.contains("*")){
            int idx = predicate.indexOf('*');
            this.Topic = predicate.substring(idx + 1).trim();
//            this.Topic = predicate.split("\\*")[1].split("\\s+")[0].trim();
        }

        if (Keyword != null && Keyword.contains("*") ) {
            this.Topic = predicate.substring(1).trim();
//            this.Topic = predicate.split("\\*")[1].split("\\s+")[0].trim();
        }
        this.keyword = (Keyword ==  null) ? "" : Keyword;
        this.Predicate = predicate;
        this.depth = index;
        this.children = null;
    }

    public String toString(){
        return "[" + Predicate + "] [" + keyword + "] :" + depth;
    }

    public Node copyShallow() {
        Node copy = new Node(this.Predicate, this.keyword, this.depth);
        copy.children = null;
        return copy;
    }

    public Node deepCopy() {
        Node copy = new Node(this.Predicate, this.keyword, this.depth);
        copy.children = (this.children != null) ? this.children.deepCopy() : null;
        return copy;
    }
}