package ATLAS;
import java.util.*;

public class Node{
    String Predicate;
    String keyword;
    Node children = null;
    int depth = 0;

    public Node(String predicate, String keyword, int index){
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