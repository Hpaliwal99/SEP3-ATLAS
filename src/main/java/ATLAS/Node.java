package ATLAS;
import java.util.*;

class Node{
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
}