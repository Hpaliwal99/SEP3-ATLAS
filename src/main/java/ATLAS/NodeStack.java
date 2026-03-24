package ATLAS;

import java.util.*;

public class NodeStack {
    public NodeStack(String input) {

        for (String x: ProcessString(input)) {
            System.out.println(x);
        }
    }

    private Queue<String> ProcessString(String s) {
        Queue<String> q = new LinkedList<>();
        q.addAll(Arrays.asList(s.split(" ")));
        return q;
    }
}
