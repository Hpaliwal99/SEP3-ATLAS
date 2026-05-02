package ATLAS;
import java.util.*;

public class Node{
    String Predicate;
    String keyword;
    String Topic = null;
    Node children = null;
    int depth = 0;

    public Node(String predicate, String keyword, int index){
        if (predicate.contains("*")){
            this.Topic = predicate.split("\\*")[1].trim();
        }
        this.Predicate = predicate;
        this.keyword = keyword;
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